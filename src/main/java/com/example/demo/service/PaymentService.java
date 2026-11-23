
package com.example.demo.service;

import com.example.demo.entity.Payment;
import com.example.demo.entity.DiningSession;
import com.example.demo.entity.DiningTable;
import com.example.demo.entity.DiningSessionStatus;
import com.example.demo.entity.OrderStatus;

import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.DiningSessionRepository;
import com.example.demo.repository.DiningTableRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final DiningSessionRepository diningSessionRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DiningTableRepository diningTableRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            DiningSessionRepository diningSessionRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            DiningTableRepository diningTableRepository) {

        this.paymentRepository = paymentRepository;
        this.diningSessionRepository = diningSessionRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.diningTableRepository = diningTableRepository;
    }

    // 결제 생성 및 식사 세션 종료
    @Transactional
    public Payment createPayment(Long sessionId, String paymentMethod) {

        // 1. 식사 세션 조회
        DiningSession diningSession =
                diningSessionRepository.findById(sessionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dining session not found"));

        // 2. 중복 결제 확인
        if (paymentRepository.existsByDiningSession(diningSession)) {
            throw new IllegalArgumentException(
                    "Payment already exists");
        }

        // 3. 세션 상태 확인
        if (diningSession.getStatus() != DiningSessionStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Dining session is not active");
        }

        // 4. 결제 수단 확인
        if (!"CASH".equals(paymentMethod)
                && !"CREDIT_CARD".equals(paymentMethod)
                && !"MOBILE_PAYMENT".equals(paymentMethod)) {

            throw new IllegalArgumentException(
                    "Invalid payment method");
        }

        // 5. 미처리 주문 확인
        boolean hasUnfinishedOrders =
                orderRepository.existsByDiningSession_SessionIdAndStatusIn(
                        sessionId,
                        List.of(
                                OrderStatus.PENDING,
                                OrderStatus.PREPARING
                        )
                );

        if (hasUnfinishedOrders) {
            throw new IllegalArgumentException(
                    "Unfinished orders exist. Please check the orders before payment.");
        }

        // 6. 취소되지 않은 주문 금액 계산
        BigDecimal totalAmount =
                orderItemRepository.calculateTotalAmount(
                        sessionId,
                        OrderStatus.CANCELLED
                );

        if (totalAmount == null
                || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Order amount must be greater than zero");
        }

        // 7. DiningTable 조회
        DiningTable diningTable =
                diningTableRepository.findById(diningSession.getTableId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dining table not found"));

        // 8. 테이블 상태 확인
        if (!"OCCUPIED".equals(diningTable.getStatus())) {
            throw new IllegalArgumentException(
                    "Dining table is not occupied");
        }

        // 9. 현재 시간 설정
        LocalDateTime now = LocalDateTime.now();

        // 10. 결제 생성
        Payment payment = new Payment();

        payment.setDiningSession(diningSession);
        payment.setAmount(totalAmount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus("COMPLETED");
        payment.setPaidAt(now);

        Payment savedPayment =
                paymentRepository.saveAndFlush(payment);

        // 11. DiningSession 종료
        diningSession.setStatus(DiningSessionStatus.COMPLETED);
        diningSession.setEndedAt(now);

        // 12. DiningTable 사용 가능 상태로 변경
        diningTable.setStatus("AVAILABLE");

        return savedPayment;
    }
}
