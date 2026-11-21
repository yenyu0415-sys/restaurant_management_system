package com.example.demo.service;

import com.example.demo.entity.Payment;
import com.example.demo.entity.DiningSession;
import com.example.demo.entity.DiningTable;
import com.example.demo.entity.DiningSessionStatus;

import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.DiningSessionRepository;
import com.example.demo.repository.DiningTableRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final DiningSessionRepository diningSessionRepository;
    private final OrderItemRepository orderItemRepository;
    private final DiningTableRepository diningTableRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            DiningSessionRepository diningSessionRepository,
            OrderItemRepository orderItemRepository,
            DiningTableRepository diningTableRepository) {

        this.paymentRepository = paymentRepository;
        this.diningSessionRepository = diningSessionRepository;
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

        // 5. 전체 주문 금액 계산
        BigDecimal totalAmount =
                orderItemRepository.calculateTotalAmount(
                        diningSession.getSessionId()
                );

        if (totalAmount == null
                || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Order amount must be greater than zero");
        }

        // 6. 현재 시간 설정
        LocalDateTime now = LocalDateTime.now();

        // 7. 결제 생성
        Payment payment = new Payment();

        payment.setDiningSession(diningSession);
        payment.setAmount(totalAmount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus("COMPLETED");
        payment.setPaidAt(now);

        Payment savedPayment = paymentRepository.saveAndFlush(payment);

        // 8. DiningTable 조회
        DiningTable diningTable =
                diningTableRepository.findById(diningSession.getTableId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dining table not found"));

        // 9. 테이블 상태 확인
        if (!"OCCUPIED".equals(diningTable.getStatus())) {
            throw new IllegalArgumentException(
                    "Dining table is not occupied");
        }

        // 10. DiningSession 종료
        diningSession.setStatus(DiningSessionStatus.COMPLETED);
        diningSession.setEndedAt(now);

        // 11. DiningTable 사용 가능 상태로 변경
        diningTable.setStatus("AVAILABLE");

        return savedPayment;
    }
}
