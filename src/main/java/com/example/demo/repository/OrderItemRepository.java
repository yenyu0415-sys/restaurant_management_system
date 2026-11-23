
package com.example.demo.repository;

import com.example.demo.entity.OrderItem;
import com.example.demo.entity.OrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    // 특정 식사 세션의 취소되지 않은 주문 금액 계산
    @Query("""
        SELECT COALESCE(
            SUM(oi.unitPrice * oi.quantity), 0
        )
        FROM OrderItem oi
        WHERE oi.order.diningSession.sessionId = :sessionId
        AND oi.order.status <> :cancelledStatus
    """)
    BigDecimal calculateTotalAmount(
            @Param("sessionId") Long sessionId,
            @Param("cancelledStatus") OrderStatus cancelledStatus
    );

    // 특정 주문의 주문 상세 조회
    List<OrderItem> findByOrder_OrderId(Long orderId);
}
