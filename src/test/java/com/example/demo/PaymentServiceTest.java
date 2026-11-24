
package com.example.demo;

import com.example.demo.entity.DiningSessionStatus;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.Payment;
import com.example.demo.entity.DiningSession;
import com.example.demo.entity.DiningTable;

import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
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
import java.time.LocalDateTime;
import java.util.List;
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
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private DiningTableRepository diningTableRepository;

    @InjectMocks
    private PaymentService paymentService;


    // ==========================================
    // CREATE PAYMENT TESTS
    // ==========================================

    // Test 1: Payment success
    @Test
    void createPayment_success() {

        DiningSession session = prepareSuccessfulPayment(1L);

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

        verify(paymentRepository, times(1))
                .saveAndFlush(any(Payment.class));
    }


    // Test 2: Dining session not found
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


    // Test 3: Dining session is not active
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


    // Test 4: Duplicate payment
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
                .calculateTotalAmount(
                        anyLong(),
                        any(OrderStatus.class)
                );
    }


    // Test 5: Invalid payment method
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
    }


    // Test 6: Zero payment amount
    @Test
    void createPayment_zeroAmount() {

        DiningSession session = new DiningSession();
        session.setSessionId(1L);
        session.setStatus(DiningSessionStatus.ACTIVE);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(session));

        when(paymentRepository.existsByDiningSession(session))
                .thenReturn(false);

        when(orderRepository
                .existsByDiningSession_SessionIdAndStatusIn(
                        1L,
                        List.of(
                                OrderStatus.PENDING,
                                OrderStatus.PREPARING
                        )
                )).thenReturn(false);

        when(orderItemRepository.calculateTotalAmount(
                1L,
                OrderStatus.CANCELLED
        )).thenReturn(BigDecimal.ZERO);

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


    // Test 7: Null payment amount
    @Test
    void createPayment_nullAmount() {

        DiningSession session = new DiningSession();
        session.setSessionId(1L);
        session.setStatus(DiningSessionStatus.ACTIVE);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(session));

        when(paymentRepository.existsByDiningSession(session))
                .thenReturn(false);

        when(orderRepository
                .existsByDiningSession_SessionIdAndStatusIn(
                        1L,
                        List.of(
                                OrderStatus.PENDING,
                                OrderStatus.PREPARING
                        )
                )).thenReturn(false);

        when(orderItemRepository.calculateTotalAmount(
                1L,
                OrderStatus.CANCELLED
        )).thenReturn(null);

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


    // Test 8: Dining table not found
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

        when(orderRepository
                .existsByDiningSession_SessionIdAndStatusIn(
                        1L,
                        List.of(
                                OrderStatus.PENDING,
                                OrderStatus.PREPARING
                        )
                )).thenReturn(false);

        when(orderItemRepository.calculateTotalAmount(
                1L,
                OrderStatus.CANCELLED
        )).thenReturn(new BigDecimal("250.00"));

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

        verify(paymentRepository, never())
                .saveAndFlush(any(Payment.class));
    }


    // Test 9: Dining table is not occupied
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

        when(orderRepository
                .existsByDiningSession_SessionIdAndStatusIn(
                        1L,
                        List.of(
                                OrderStatus.PENDING,
                                OrderStatus.PREPARING
                        )
                )).thenReturn(false);

        when(orderItemRepository.calculateTotalAmount(
                1L,
                OrderStatus.CANCELLED
        )).thenReturn(new BigDecimal("250.00"));

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

        verify(paymentRepository, never())
                .saveAndFlush(any(Payment.class));

        assertEquals("AVAILABLE", table.getStatus());
    }


    // Test 10: Negative payment amount
    @Test
    void createPayment_negativeAmount() {

        DiningSession session = new DiningSession();
        session.setSessionId(1L);
        session.setStatus(DiningSessionStatus.ACTIVE);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(session));

        when(paymentRepository.existsByDiningSession(session))
                .thenReturn(false);

        when(orderRepository
                .existsByDiningSession_SessionIdAndStatusIn(
                        1L,
                        List.of(
                                OrderStatus.PENDING,
                                OrderStatus.PREPARING
                        )
                )).thenReturn(false);

        when(orderItemRepository.calculateTotalAmount(
                1L,
                OrderStatus.CANCELLED
        )).thenReturn(new BigDecimal("-100.00"));

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


    // Test 11: Empty payment method
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
                .calculateTotalAmount(
                        anyLong(),
                        any(OrderStatus.class)
                );

        verify(paymentRepository, never())
                .saveAndFlush(any(Payment.class));
    }


    // Test 12: Null payment method
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
                .calculateTotalAmount(
                        anyLong(),
                        any(OrderStatus.class)
                );

        verify(paymentRepository, never())
                .saveAndFlush(any(Payment.class));
    }


    // Test 13: Unfinished orders exist
    @Test
    void createPayment_unfinishedOrdersExist() {

        DiningSession session = new DiningSession();
        session.setSessionId(1L);
        session.setStatus(DiningSessionStatus.ACTIVE);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(session));

        when(paymentRepository.existsByDiningSession(session))
                .thenReturn(false);

        when(orderRepository
                .existsByDiningSession_SessionIdAndStatusIn(
                        1L,
                        List.of(
                                OrderStatus.PENDING,
                                OrderStatus.PREPARING
                        )
                )).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.createPayment(1L, "CASH")
        );

        assertEquals(
                "Unfinished orders exist. Please check the orders before payment.",
                exception.getMessage()
        );

        verify(orderItemRepository, never())
                .calculateTotalAmount(
                        anyLong(),
                        any(OrderStatus.class)
                );

        verify(paymentRepository, never())
                .saveAndFlush(any(Payment.class));
    }


    // Test 14: CARD payment success
    @Test
    void createPayment_cardSuccess() {

        prepareSuccessfulPayment(1L);

        Payment result = paymentService.createPayment(1L, "CARD");

        assertEquals("CARD", result.getPaymentMethod());
        assertEquals("COMPLETED", result.getPaymentStatus());
    }


    // Test 15: MOBILE_PAY payment success
    @Test
    void createPayment_mobilePaySuccess() {

        prepareSuccessfulPayment(1L);

        Payment result = paymentService.createPayment(1L, "MOBILE_PAY");

        assertEquals("MOBILE_PAY", result.getPaymentMethod());
        assertEquals("COMPLETED", result.getPaymentStatus());
    }


    // Test 16: Payment method with spaces
    @Test
    void createPayment_paymentMethodWithSpaces() {

        prepareSuccessfulPayment(1L);

        Payment result = paymentService.createPayment(1L, " CASH ");

        assertEquals("CASH", result.getPaymentMethod());
    }


    // ==========================================
    // GET ALL PAYMENTS TESTS
    // ==========================================

    // Test 17: Get all payments
    @Test
    void getAllPayments_shouldReturnPayments() {

        DiningSession session = new DiningSession();
        session.setSessionId(1L);

        Payment payment = new Payment();
        payment.setDiningSession(session);
        payment.setAmount(new BigDecimal("250.00"));
        payment.setPaymentMethod("CASH");
        payment.setPaymentStatus("COMPLETED");
        payment.setPaidAt(LocalDateTime.now());

        when(paymentRepository.findAll())
                .thenReturn(List.of(payment));

        var result = paymentService.getAllPayments();

        assertNotNull(result);
        assertEquals(1, result.size());
    }


    // Test 18: Get all payments when empty
    @Test
    void getAllPayments_whenEmpty_shouldReturnEmptyList() {

        when(paymentRepository.findAll())
                .thenReturn(List.of());

        var result = paymentService.getAllPayments();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    // ==========================================
    // HELPER METHOD
    // ==========================================

    private DiningSession prepareSuccessfulPayment(Long sessionId) {

        DiningSession session = new DiningSession();
        session.setSessionId(sessionId);
        session.setTableId(1L);
        session.setStatus(DiningSessionStatus.ACTIVE);

        DiningTable table = new DiningTable();
        table.setStatus("OCCUPIED");

        when(diningSessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        when(paymentRepository.existsByDiningSession(session))
                .thenReturn(false);

        when(orderRepository
                .existsByDiningSession_SessionIdAndStatusIn(
                        sessionId,
                        List.of(
                                OrderStatus.PENDING,
                                OrderStatus.PREPARING
                        )
                )).thenReturn(false);

        when(orderItemRepository.calculateTotalAmount(
                sessionId,
                OrderStatus.CANCELLED
        )).thenReturn(new BigDecimal("250.00"));

        when(diningTableRepository.findById(1L))
                .thenReturn(Optional.of(table));

        when(paymentRepository.saveAndFlush(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        return session;
    }

}
