package com.example.demo;

import com.example.demo.entity.DiningSessionStatus;
import com.example.demo.entity.Payment;
import com.example.demo.entity.DiningSession;
import com.example.demo.entity.DiningTable;

import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.DiningSessionRepository;
import com.example.demo.repository.DiningTableRepository;

import com.example.demo.service.PaymentService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private DiningSessionRepository diningSessionRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private DiningTableRepository diningTableRepository;

    @InjectMocks
    private PaymentService paymentService;


    // 1. 결제 성공
    @Test
    void createPayment_success() {

        DiningSession session = new DiningSession();
        session.setSessionId(1L);
        session.setTableId(1L);
        session.setStatus(DiningSessionStatus.ACTIVE);

        DiningTable table = new DiningTable();
        table.setStatus("OCCUPIED");

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(session));

        when(paymentRepository.existsByDiningSession(session))
                .thenReturn(false);

        when(orderItemRepository.calculateTotalAmount(1L))
                .thenReturn(new BigDecimal("250.00"));

        when(paymentRepository.saveAndFlush(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(diningTableRepository.findById(1L))
                .thenReturn(Optional.of(table));

        Payment result = paymentService.createPayment(1L, "CASH");

        assertNotNull(result);
        assertEquals(new BigDecimal("250.00"), result.getAmount());
        assertEquals("CASH", result.getPaymentMethod());
        assertEquals("COMPLETED", result.getPaymentStatus());

        assertEquals(
                DiningSessionStatus.COMPLETED,
                session.getStatus()
        );

        assertNotNull(session.getEndedAt());
        assertEquals("AVAILABLE", table.getStatus());

        verify(paymentRepository, times(1))
                .saveAndFlush(any(Payment.class));
    }


    // 2. 식사 세션이 존재하지 않는 경우
    @Test
    void createPayment_sessionNotFound() {

        when(diningSessionRepository.findById(999L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> paymentService.createPayment(999L, "CASH")
        );

        assertEquals(
                "Dining session not found",
                exception.getMessage()
        );

        verify(paymentRepository, never())
                .saveAndFlush(any(Payment.class));
    }


    // 3. 식사 세션이 ACTIVE 상태가 아닌 경우
    @Test
    void createPayment_sessionNotActive() {

        DiningSession session = new DiningSession();
        session.setSessionId(1L);
        session.setStatus(DiningSessionStatus.COMPLETED);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(session));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.createPayment(1L, "CASH")
        );

        assertEquals(
                "Dining session is not active",
                exception.getMessage()
        );

        verify(paymentRepository, never())
                .saveAndFlush(any(Payment.class));
    }


    // 4. 이미 결제가 존재하는 경우
    @Test
    void createPayment_alreadyExists() {

        DiningSession session = new DiningSession();
        session.setSessionId(1L);
        session.setStatus(DiningSessionStatus.ACTIVE);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(session));

        when(paymentRepository.existsByDiningSession(session))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.createPayment(1L, "CASH")
        );

        assertEquals(
                "Payment already exists",
                exception.getMessage()
        );

        verify(paymentRepository, never())
                .saveAndFlush(any(Payment.class));

        verify(orderItemRepository, never())
                .calculateTotalAmount(1L);
    }


    // 5. 잘못된 결제 수단인 경우
    @Test
    void createPayment_invalidPaymentMethod() {

        DiningSession session = new DiningSession();
        session.setSessionId(1L);
        session.setStatus(DiningSessionStatus.ACTIVE);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(session));

        when(paymentRepository.existsByDiningSession(session))
                .thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.createPayment(1L, "BITCOIN")
        );

        assertEquals(
                "Invalid payment method",
                exception.getMessage()
        );

        verify(paymentRepository, never())
                .saveAndFlush(any(Payment.class));

        verify(orderItemRepository, never())
                .calculateTotalAmount(1L);
    }


    // 6. 결제 금액이 0인 경우
    @Test
    void createPayment_zeroAmount() {

        DiningSession session = new DiningSession();
        session.setSessionId(1L);
        session.setStatus(DiningSessionStatus.ACTIVE);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(session));

        when(paymentRepository.existsByDiningSession(session))
                .thenReturn(false);

        when(orderItemRepository.calculateTotalAmount(1L))
                .thenReturn(BigDecimal.ZERO);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.createPayment(1L, "CASH")
        );

        assertEquals(
                "Order amount must be greater than zero",
                exception.getMessage()
        );

        verify(paymentRepository, never())
                .saveAndFlush(any(Payment.class));
    }


    // 7. 결제 금액이 null인 경우
    @Test
    void createPayment_nullAmount() {

        DiningSession session = new DiningSession();
        session.setSessionId(1L);
        session.setStatus(DiningSessionStatus.ACTIVE);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(session));

        when(paymentRepository.existsByDiningSession(session))
                .thenReturn(false);

        when(orderItemRepository.calculateTotalAmount(1L))
                .thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.createPayment(1L, "CASH")
        );

        assertEquals(
                "Order amount must be greater than zero",
                exception.getMessage()
        );

        verify(paymentRepository, never())
                .saveAndFlush(any(Payment.class));
    }


    // 8. DiningTable이 존재하지 않는 경우
    @Test
    void createPayment_diningTableNotFound() {

        DiningSession session = new DiningSession();
        session.setSessionId(1L);
        session.setTableId(1L);
        session.setStatus(DiningSessionStatus.ACTIVE);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(session));

        when(paymentRepository.existsByDiningSession(session))
                .thenReturn(false);

        when(orderItemRepository.calculateTotalAmount(1L))
                .thenReturn(new BigDecimal("250.00"));

        when(paymentRepository.saveAndFlush(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(diningTableRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> paymentService.createPayment(1L, "CASH")
        );

        assertEquals(
                "Dining table not found",
                exception.getMessage()
        );

        verify(paymentRepository, times(1))
                .saveAndFlush(any(Payment.class));
    }


    // 9. DiningTable이 OCCUPIED 상태가 아닌 경우
    @Test
    void createPayment_diningTableNotOccupied() {

        DiningSession session = new DiningSession();
        session.setSessionId(1L);
        session.setTableId(1L);
        session.setStatus(DiningSessionStatus.ACTIVE);

        DiningTable table = new DiningTable();
        table.setStatus("AVAILABLE");

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(session));

        when(paymentRepository.existsByDiningSession(session))
                .thenReturn(false);

        when(orderItemRepository.calculateTotalAmount(1L))
                .thenReturn(new BigDecimal("250.00"));

        when(paymentRepository.saveAndFlush(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(diningTableRepository.findById(1L))
                .thenReturn(Optional.of(table));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.createPayment(1L, "CASH")
        );

        assertEquals(
                "Dining table is not occupied",
                exception.getMessage()
        );

        verify(paymentRepository, times(1))
                .saveAndFlush(any(Payment.class));

        assertEquals("AVAILABLE", table.getStatus());
    }

    // 10. 결제 금액이 음수인 경우
    @Test
    void createPayment_negativeAmount() {

        DiningSession session = new DiningSession();
        session.setSessionId(1L);
        session.setStatus(DiningSessionStatus.ACTIVE);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(session));

        when(paymentRepository.existsByDiningSession(session))
                .thenReturn(false);

        when(orderItemRepository.calculateTotalAmount(1L))
                .thenReturn(new BigDecimal("-100.00"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.createPayment(1L, "CASH")
        );

        assertEquals(
                "Order amount must be greater than zero",
                exception.getMessage()
        );

        verify(paymentRepository, never())
                .saveAndFlush(any(Payment.class));
    }

    // 11. 결제 수단이 빈 문자열인 경우
    @Test
    void createPayment_emptyPaymentMethod() {

        DiningSession session = new DiningSession();
        session.setSessionId(1L);
        session.setStatus(DiningSessionStatus.ACTIVE);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(session));

        when(paymentRepository.existsByDiningSession(session))
                .thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.createPayment(1L, "")
        );

        assertEquals(
                "Invalid payment method",
                exception.getMessage()
        );

        verify(orderItemRepository, never())
                .calculateTotalAmount(1L);

        verify(paymentRepository, never())
                .saveAndFlush(any(Payment.class));
    }

    // 12. 결제 수단이 null인 경우
    @Test
    void createPayment_nullPaymentMethod() {

        DiningSession session = new DiningSession();
        session.setSessionId(1L);
        session.setStatus(DiningSessionStatus.ACTIVE);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(session));

        when(paymentRepository.existsByDiningSession(session))
                .thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.createPayment(1L, null)
        );

        assertEquals(
                "Invalid payment method",
                exception.getMessage()
        );

        verify(orderItemRepository, never())
                .calculateTotalAmount(1L);

        verify(paymentRepository, never())
                .saveAndFlush(any(Payment.class));
    }
}
