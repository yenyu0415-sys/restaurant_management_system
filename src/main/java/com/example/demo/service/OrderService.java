package com.example.demo.service;

import com.example.demo.dto.CreateOrderRequest;
import com.example.demo.dto.OrderItemRequest;
import com.example.demo.dto.OrderItemResponse;
import com.example.demo.dto.OrderResponse;

import com.example.demo.entity.*;

import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.DiningSessionRepository;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.MenuRepositoryJPA;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DiningSessionRepository diningSessionRepository;
    private final EmployeeRepository employeeRepository;
    private final MenuRepositoryJPA menuRepositoryJPA;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            DiningSessionRepository diningSessionRepository,
            EmployeeRepository employeeRepository,
            MenuRepositoryJPA menuRepositoryJPA) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.diningSessionRepository = diningSessionRepository;
        this.employeeRepository = employeeRepository;
        this.menuRepositoryJPA = menuRepositoryJPA;
    }

    // 주문 저장
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    // 모든 주문 조회
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    // ID로 특정 주문 조회
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {

        Order order = getOrderEntityById(id);

        return convertToOrderResponse(order);
    }

    // 내부 주문 Entity 조회
    private Order getOrderEntityById(Long id) {

        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + id));
    }

    // 세션 ID로 주문 조회
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersBySessionId(Long sessionId) {

        return orderRepository
                .findByDiningSession_SessionId(sessionId)
                .stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    // Order Entity를 OrderResponse DTO로 변환
    private OrderResponse convertToOrderResponse(Order order) {

        List<OrderItem> orderItems =
                orderItemRepository.findByOrder_OrderId(
                        order.getOrderId());

        List<OrderItemResponse> itemResponses =
                new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItem item : orderItems) {

            BigDecimal unitPrice = item.getUnitPrice();

            BigDecimal subtotal = unitPrice.multiply(
                    BigDecimal.valueOf(item.getQuantity()));

            totalAmount = totalAmount.add(subtotal);

            OrderItemResponse itemResponse =
                    new OrderItemResponse(
                            item.getMenu().getMenuName(),
                            item.getQuantity(),
                            unitPrice,
                            subtotal
                    );

            itemResponses.add(itemResponse);
        }

        return new OrderResponse(
                order.getOrderId(),
                order.getDiningSession().getSessionId(),
                order.getEmployee().getEmployeeId(),
                order.getOrderTime(),
                order.getStatus(),
                itemResponses,
                totalAmount
        );
    }

    // 주문 삭제
    public void deleteOrder(Long id) {

        Order order = getOrderEntityById(id);

        orderRepository.delete(order);
    }

    // 주문 생성
    @Transactional
    public Order createOrder(CreateOrderRequest request) {

        if (request.getItems() == null
                || request.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                    "Order items cannot be empty");
        }

        // 식사 세션 조회
        DiningSession diningSession =
                diningSessionRepository.findById(request.getSessionId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dining session not found"));

        // 활성 세션 확인
        if (diningSession.getStatus()
                != DiningSessionStatus.ACTIVE) {

            throw new IllegalArgumentException(
                    "Dining session is not active");
        }

        // 직원 조회
        Employee employee =
                employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employee not found"));

        // 직원 활성 상태 확인
        if (!employee.isActive()) {

            throw new IllegalArgumentException(
                    "Employee is inactive");
        }

        // 주문 생성
        Order order = new Order(LocalDateTime.now());

        order.setDiningSession(diningSession);
        order.setEmployee(employee);
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        // 주문 항목 생성
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {

            // 수량 검증
            if (itemRequest.getQuantity() == null
                    || itemRequest.getQuantity() <= 0) {

                throw new IllegalArgumentException(
                        "Quantity must be greater than zero");
            }

            // 메뉴 조회
            Menu menu = menuRepositoryJPA
                    .findById(itemRequest.getMenuId())
                    .orElseThrow(() ->
                            new RuntimeException("Menu not found"));

            // 메뉴 활성 상태 확인
            if (!menu.isActive()) {

                throw new IllegalArgumentException(
                        "Menu is inactive: " + menu.getMenuName());
            }

            // 주문 상세 생성
            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(savedOrder);
            orderItem.setMenu(menu);
            orderItem.setQuantity(itemRequest.getQuantity());

            // 주문 당시 메뉴 가격 저장
            orderItem.setUnitPrice(
                    BigDecimal.valueOf(menu.getPrice()));

            orderItems.add(orderItem);
        }

        // 주문 상세 일괄 저장
        orderItemRepository.saveAll(orderItems);

        return savedOrder;
    }

    // 주문 상태 변경
    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {

        Order order = getOrderEntityById(id);

        if (status == null) {

            throw new IllegalArgumentException(
                    "Order status cannot be null");
        }

        OrderStatus currentStatus = order.getStatus();

        // PENDING → PREPARING
        if (currentStatus == OrderStatus.PENDING) {

            if (status != OrderStatus.PREPARING) {

                throw new IllegalArgumentException(
                        "PENDING can only change to PREPARING");
            }

            // PREPARING → COMPLETED
        } else if (currentStatus == OrderStatus.PREPARING) {

            if (status != OrderStatus.COMPLETED) {

                throw new IllegalArgumentException(
                        "PREPARING can only change to COMPLETED");
            }

            // 완료 또는 취소 상태에서는 변경 불가
        } else {

            throw new IllegalArgumentException(
                    "Order status cannot be changed");
        }

        order.setStatus(status);

        return orderRepository.save(order);
    }
}

