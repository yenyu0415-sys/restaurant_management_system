
package com.example.demo.controller;

import com.example.demo.dto.PaymentResponse;
import com.example.demo.entity.Payment;
import com.example.demo.service.PaymentService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 전체 결제 내역 조회
    @GetMapping
    public List<PaymentResponse> getAllPayments() {
        return paymentService.getAllPayments();
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
