package com.example.demo.repository;

import com.example.demo.entity.OrderItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    // 특정 식사 세션의 전체 주문 금액 계산
    @Query("""
        SELECT COALESCE(
            SUM(oi.unitPrice * oi.quantity), 0
        )
        FROM OrderItem oi
        WHERE oi.order.diningSession.sessionId = :sessionId
    """)
    BigDecimal calculateTotalAmount(
            @Param("sessionId") Long sessionId
    );

    // 특정 주문의 주문 상세 조회
    List<OrderItem> findByOrder_OrderId(Long orderId);
}
