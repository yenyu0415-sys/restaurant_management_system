
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

    // Save Order
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    // Get All Orders
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    // Get Order By ID
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {

        Order order = getOrderEntityById(id);

        return convertToOrderResponse(order);
    }

    // Find Order Entity By ID
    private Order getOrderEntityById(Long id) {

        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Order not found: " + id));
    }

    // Get Orders By Dining Session ID
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersBySessionId(Long sessionId) {

        return orderRepository
                .findByDiningSession_SessionId(sessionId)
                .stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    // Convert Order Entity To Response DTO
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

    // Delete Order
    // Physical deletion - not used for normal cancellation
    public void deleteOrder(Long id) {

        Order order = getOrderEntityById(id);

        orderRepository.delete(order);
    }

    // Create Order
    @Transactional
    public Order createOrder(CreateOrderRequest request) {

        if (request.getItems() == null
                || request.getItems().isEmpty()) {

            throw new IllegalArgumentException(
                    "Order items cannot be empty");
        }

        // Find Dining Session
        DiningSession diningSession =
                diningSessionRepository.findById(request.getSessionId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dining session not found"));

        // Check Active Session
        if (diningSession.getStatus()
                != DiningSessionStatus.ACTIVE) {

            throw new IllegalArgumentException(
                    "Dining session is not active");
        }

        // Find Employee
        Employee employee =
                employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employee not found"));

        // Check Active Employee
        if (!employee.isActive()) {

            throw new IllegalArgumentException(
                    "Employee is inactive");
        }

        // Create Order
        Order order = new Order(LocalDateTime.now());

        order.setDiningSession(diningSession);
        order.setEmployee(employee);
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        // Create Order Items
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {

            // Validate Quantity
            if (itemRequest.getQuantity() == null
                    || itemRequest.getQuantity() <= 0) {

                throw new IllegalArgumentException(
                        "Quantity must be greater than zero");
            }

            // Find Menu
            Menu menu = menuRepositoryJPA
                    .findById(itemRequest.getMenuId())
                    .orElseThrow(() ->
                            new RuntimeException("Menu not found"));

            // Check Active Menu
            if (!menu.isActive()) {

                throw new IllegalArgumentException(
                        "Menu is inactive: " + menu.getMenuName());
            }

            // Create Order Item
            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(savedOrder);
            orderItem.setMenu(menu);
            orderItem.setQuantity(itemRequest.getQuantity());

            // Save Menu Price At Order Time
            orderItem.setUnitPrice(
                    BigDecimal.valueOf(menu.getPrice()));

            orderItems.add(orderItem);
        }

        // Save All Order Items
        orderItemRepository.saveAll(orderItems);

        return savedOrder;
    }

    // Cancel Order
    @Transactional
    public Order cancelOrder(Long id) {

        Order order = getOrderEntityById(id);

        OrderStatus currentStatus = order.getStatus();

        // Only PENDING and PREPARING orders can be cancelled
        if (currentStatus != OrderStatus.PENDING
                && currentStatus != OrderStatus.PREPARING) {

            throw new IllegalArgumentException(
                    "Only PENDING or PREPARING orders can be cancelled");
        }

        // Change status instead of deleting the order
        order.setStatus(OrderStatus.CANCELLED);

        return orderRepository.save(order);
    }

    // Update Order Status
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

            // Completed or Cancelled orders cannot be changed
        } else {

            throw new IllegalArgumentException(
                    "Order status cannot be changed");
        }

        order.setStatus(status);

        return orderRepository.save(order);
    }
}
