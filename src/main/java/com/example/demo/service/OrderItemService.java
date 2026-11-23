
package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.OrderStatus;

import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    public OrderItemService(
            OrderItemRepository orderItemRepository,
            OrderRepository orderRepository) {

        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
    }

    // 주문 상세 항목 저장
    public OrderItem saveOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    // 모든 주문 상세 항목 조회
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    // ID로 주문 상세 항목 조회
    public OrderItem getOrderItemById(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "OrderItem not found: " + id));
    }

    // 주문 상세 항목 삭제
    public void deleteOrderItem(Long id) {
        OrderItem orderItem = getOrderItemById(id);
        orderItemRepository.delete(orderItem);
    }

    // 특정 식사 세션의 취소되지 않은 주문 금액 조회
    public BigDecimal getTotalAmount(Long sessionId) {
        return orderItemRepository.calculateTotalAmount(
                sessionId,
                OrderStatus.CANCELLED
        );
    }

    // DB에서 실제 Order 조회
    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found: " + orderId));
    }
}
