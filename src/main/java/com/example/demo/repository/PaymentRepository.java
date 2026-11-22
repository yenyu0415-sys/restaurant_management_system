
package com.example.demo.repository;

import com.example.demo.entity.DiningSession;
import com.example.demo.entity.Payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    // 確認該用餐 Session 是否已有付款紀錄
    boolean existsByDiningSession(DiningSession diningSession);

    // 查詢該用餐 Session 的付款筆數
    long countByDiningSession(DiningSession diningSession);

    // 計算指定日期區間內已完成的付款總額
    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.paymentStatus = :paymentStatus
        AND p.paidAt >= :start
        AND p.paidAt < :end
        """)
    BigDecimal sumCompletedPayments(
            @Param("paymentStatus") String paymentStatus,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
