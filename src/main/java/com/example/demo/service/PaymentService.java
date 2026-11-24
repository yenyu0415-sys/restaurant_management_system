
package com.example.demo.service;

import com.example.demo.dto.PaymentResponse;
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

    // Get All Payments
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {

        return paymentRepository.findAll()
                .stream()
                .map(payment -> new PaymentResponse(
                        payment.getPaymentId(),
                        payment.getDiningSession().getSessionId(),
                        payment.getAmount(),
                        payment.getPaymentMethod(),
                        payment.getPaymentStatus(),
                        payment.getPaidAt()
                ))
                .toList();
    }

    // Create Payment and Complete Dining Session
    @Transactional
    public Payment createPayment(Long sessionId, String paymentMethod) {

        // 1. Find Dining Session
        DiningSession diningSession =
                diningSessionRepository.findById(sessionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dining session not found"));

        // 2. Check Duplicate Payment
        if (paymentRepository.existsByDiningSession(diningSession)) {
            throw new IllegalArgumentException(
                    "Payment already exists");
        }

        // 3. Check Dining Session Status
        if (diningSession.getStatus() != DiningSessionStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Dining session is not active");
        }

        // 4. Validate Payment Method
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException(
                    "Invalid payment method");
        }

        paymentMethod = paymentMethod.trim().toUpperCase();

        if (!"CASH".equals(paymentMethod)
                && !"CARD".equals(paymentMethod)
                && !"MOBILE_PAY".equals(paymentMethod)) {

            throw new IllegalArgumentException(
                    "Invalid payment method");
        }

        // 5. Check Unfinished Orders
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

        // 6. Calculate Amount Excluding Cancelled Orders
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

        // 7. Find Dining Table
        DiningTable diningTable =
                diningTableRepository.findById(diningSession.getTableId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dining table not found"));

        // 8. Check Dining Table Status
        if (!"OCCUPIED".equals(diningTable.getStatus())) {
            throw new IllegalArgumentException(
                    "Dining table is not occupied");
        }

        // 9. Set Current Time
        LocalDateTime now = LocalDateTime.now();

        // 10. Create Payment
        Payment payment = new Payment();

        payment.setDiningSession(diningSession);
        payment.setAmount(totalAmount);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus("COMPLETED");
        payment.setPaidAt(now);

        Payment savedPayment =
                paymentRepository.saveAndFlush(payment);

        // 11. Complete Dining Session
        diningSession.setStatus(DiningSessionStatus.COMPLETED);
        diningSession.setEndedAt(now);

        // 12. Make Dining Table Available
        diningTable.setStatus("AVAILABLE");

        return savedPayment;
    }
}
