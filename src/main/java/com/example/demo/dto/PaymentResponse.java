
package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {

    private Long paymentId;
    private Long sessionId;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paidAt;

    public PaymentResponse(
            Long paymentId,
            Long sessionId,
            BigDecimal amount,
            String paymentMethod,
            String paymentStatus,
            LocalDateTime paidAt) {

        this.paymentId = paymentId;
        this.sessionId = sessionId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paidAt = paidAt;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }
}
