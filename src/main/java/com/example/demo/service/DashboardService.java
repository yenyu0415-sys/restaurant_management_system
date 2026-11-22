
package com.example.demo.service;

import com.example.demo.dto.DashboardResponse;
import com.example.demo.entity.OrderStatus;

import com.example.demo.repository.DiningTableRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.PaymentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class DashboardService {

    private final DiningTableRepository diningTableRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public DashboardService(
            DiningTableRepository diningTableRepository,
            OrderRepository orderRepository,
            PaymentRepository paymentRepository) {

        this.diningTableRepository = diningTableRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    // Dashboard 전체 통계 조회
    @Transactional(readOnly = true)
    public DashboardResponse getDashboardSummary() {

        // 전체 테이블 수
        long totalTables = diningTableRepository.count();

        // 진행 중인 주문 수
        long pendingOrders =
                orderRepository.countByStatus(OrderStatus.PENDING);

        long preparingOrders =
                orderRepository.countByStatus(OrderStatus.PREPARING);

        long activeOrders = pendingOrders + preparingOrders;

        // 대만 현지 날짜 기준 오늘의 시작과 종료 시간
        LocalDate today = LocalDate.now(
                ZoneId.of("Asia/Taipei"));

        LocalDateTime startOfDay = today.atStartOfDay();

        LocalDateTime startOfNextDay =
                today.plusDays(1).atStartOfDay();

        // 오늘 완료된 결제 금액
        BigDecimal todayPayments =
                paymentRepository.sumCompletedPayments(
                        "COMPLETED",
                        startOfDay,
                        startOfNextDay
                );

        // Dashboard DTO 반환
        return new DashboardResponse(
                totalTables,
                activeOrders,
                todayPayments
        );
    }
}
