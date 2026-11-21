package com.example.demo.controller;

import com.example.demo.entity.Payment;
import com.example.demo.service.PaymentService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;


    // 1. 정상 결제 요청
    @Test
    void createPayment_success() {

        // given
        Payment payment = new Payment();

        payment.setPaymentMethod("CASH");
        payment.setPaymentStatus("COMPLETED");

        when(paymentService.createPayment(6L, "CASH"))
                .thenReturn(payment);

        // when
        Payment result =
                paymentController.createPayment(6L, "CASH");

        // then
        assertNotNull(result);

        assertEquals("CASH", result.getPaymentMethod());

        assertEquals(
                "COMPLETED",
                result.getPaymentStatus()
        );

        verify(paymentService, times(1))
                .createPayment(6L, "CASH");
    }

    @Test
    void createPayment_invalidPaymentMethod() {

        // given
        when(paymentService.createPayment(6L, "BITCOIN"))
                .thenThrow(
                        new IllegalArgumentException(
                                "Invalid payment method"
                        )
                );

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentController.createPayment(6L, "BITCOIN")
        );

        assertEquals(
                "Invalid payment method",
                exception.getMessage()
        );

        verify(paymentService, times(1))
                .createPayment(6L, "BITCOIN");
    }
}

