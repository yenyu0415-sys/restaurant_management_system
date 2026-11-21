package com.example.demo;

import com.example.demo.entity.DiningSession;
import com.example.demo.entity.DiningSessionStatus;
import com.example.demo.entity.Payment;

import com.example.demo.repository.DiningSessionRepository;
import com.example.demo.repository.PaymentRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PaymentIntegrationTest {

    @Autowired
    private DiningSessionRepository diningSessionRepository;

    @Autowired
    private PaymentRepository paymentRepository;


    @Test
    void shouldSaveAndRetrievePayment() {

        // 1. DiningSession 생성
        DiningSession session = new DiningSession();

        session.setTableId(1L);
        session.setStartedAt(LocalDateTime.now());
        session.setStatus(DiningSessionStatus.ACTIVE);

        DiningSession savedSession =
                diningSessionRepository.saveAndFlush(session);


        // 2. Payment 생성
        Payment payment = new Payment();

        payment.setDiningSession(savedSession);
        payment.setAmount(new BigDecimal("250.00"));
        payment.setPaymentMethod("CASH");
        payment.setPaymentStatus("COMPLETED");
        payment.setPaidAt(LocalDateTime.now());


        // 3. Payment DB 저장
        Payment savedPayment =
                paymentRepository.saveAndFlush(payment);


        // 4. DB에서 Payment 조회
        Payment foundPayment =
                paymentRepository.findById(savedPayment.getPaymentId())
                        .orElseThrow();


        // 5. 결과 검증
        assertNotNull(foundPayment.getPaymentId());

        assertEquals(
                new BigDecimal("250.00"),
                foundPayment.getAmount()
        );

        assertEquals("CASH", foundPayment.getPaymentMethod());

        assertEquals("COMPLETED", foundPayment.getPaymentStatus());

        assertEquals(
                savedSession.getSessionId(),
                foundPayment.getDiningSession().getSessionId()
        );

        assertTrue(
                paymentRepository.existsByDiningSession(savedSession)
        );
    }
}
