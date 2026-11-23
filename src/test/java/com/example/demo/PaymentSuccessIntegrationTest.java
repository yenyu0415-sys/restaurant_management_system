
package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.PaymentService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PaymentSuccessIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private DiningTableRepository diningTableRepository;

    @Autowired
    private DiningSessionRepository diningSessionRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MenuRepositoryJPA menuRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void shouldCompletePaymentSuccessfully() {

        String uniqueId = UUID.randomUUID().toString();

        // 1. DiningTable 생성
        DiningTable table =
                new DiningTable("SUCCESS-" + uniqueId, 4);

        table.setStatus("OCCUPIED");

        DiningTable savedTable =
                diningTableRepository.saveAndFlush(table);

        // 2. DiningSession 생성
        DiningSession session = new DiningSession();

        session.setTableId(savedTable.getTableId());
        session.setStartedAt(LocalDateTime.now());
        session.setStatus(DiningSessionStatus.ACTIVE);

        DiningSession savedSession =
                diningSessionRepository.saveAndFlush(session);

        // 3. Employee 생성
        Employee employee = new Employee();

        employee.setEmployeeName("Success Tester");
        employee.setUsername("success-" + uniqueId);
        employee.setPassword("test-password");
        employee.setRole("STAFF");
        employee.setCreatedAt(LocalDateTime.now());

        Employee savedEmployee =
                employeeRepository.saveAndFlush(employee);

        // 4. Menu 생성
        Menu menu =
                new Menu("Success Noodle", 250, "NOODLE");

        Menu savedMenu =
                menuRepository.saveAndFlush(menu);

        // 5. Order 생성
        Order order = new Order(LocalDateTime.now());

        order.setDiningSession(savedSession);
        order.setEmployee(savedEmployee);

        // 모든 주문 처리가 완료된 상태
        order.setStatus(OrderStatus.COMPLETED);

        Order savedOrder =
                orderRepository.saveAndFlush(order);

        // 6. OrderItem 생성
        OrderItem orderItem = new OrderItem();

        orderItem.setOrder(savedOrder);
        orderItem.setMenu(savedMenu);
        orderItem.setQuantity(1);
        orderItem.setUnitPrice(new BigDecimal("250.00"));

        orderItemRepository.saveAndFlush(orderItem);

        // 7. 정상 결제 실행
        Payment payment =
                paymentService.createPayment(
                        savedSession.getSessionId(),
                        "CASH"
                );

        // 8. Payment 검증
        assertNotNull(payment.getPaymentId());

        assertEquals(
                new BigDecimal("250.00"),
                payment.getAmount()
        );

        assertEquals("CASH", payment.getPaymentMethod());
        assertEquals("COMPLETED", payment.getPaymentStatus());

        // 9. DiningSession 검증
        DiningSession foundSession =
                diningSessionRepository.findById(
                        savedSession.getSessionId()
                ).orElseThrow();

        assertEquals(
                DiningSessionStatus.COMPLETED,
                foundSession.getStatus()
        );

        assertNotNull(foundSession.getEndedAt());

        // 10. DiningTable 검증
        DiningTable foundTable =
                diningTableRepository.findById(
                        savedTable.getTableId()
                ).orElseThrow();

        assertEquals("AVAILABLE", foundTable.getStatus());

        // 11. Payment DB 저장 확인
        assertTrue(
                paymentRepository.existsByDiningSession(savedSession)
        );

        // 12. 실제 DB에서 Payment 재조회
        Payment foundPayment =
                paymentRepository.findById(payment.getPaymentId())
                        .orElseThrow();

        assertEquals(
                new BigDecimal("250.00"),
                foundPayment.getAmount()
        );

        assertEquals("CASH", foundPayment.getPaymentMethod());
        assertEquals("COMPLETED", foundPayment.getPaymentStatus());

        assertNotNull(foundPayment.getPaidAt());
    }
}
