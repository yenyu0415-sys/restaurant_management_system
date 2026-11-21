
package com.example.demo.repository;

import com.example.demo.entity.Order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 특정 식사 세션의 주문 목록 조회
    List<Order> findByDiningSession_SessionId(Long sessionId);

}
