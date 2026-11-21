package com.example.demo.controller;

import com.example.demo.entity.Payment;
import com.example.demo.service.PaymentService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 결제 생성
    @PostMapping
    public Payment createPayment(
            @RequestParam Long sessionId,
            @RequestParam String paymentMethod) {

        return paymentService.createPayment(
                sessionId,
                paymentMethod
        );
    }
}
