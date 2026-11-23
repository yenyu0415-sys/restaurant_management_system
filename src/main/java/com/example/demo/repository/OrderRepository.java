
package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 특정 식사 세션의 주문 목록 조회
    List<Order> findByDiningSession_SessionId(Long sessionId);

    // 주문 상태별 개수 조회
    long countByStatus(OrderStatus status);

    // 특정 식사 세션에 미처리 주문이 있는지 확인
    boolean existsByDiningSession_SessionIdAndStatusIn(
            Long sessionId,
            List<OrderStatus> statuses
    );
}
