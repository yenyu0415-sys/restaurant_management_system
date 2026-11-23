
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
class PaymentDuplicateIntegrationTest {

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
    void shouldPreventDuplicatePayment() {

        String uniqueId = UUID.randomUUID().toString();

        // 1. DiningTable 생성
        DiningTable table =
                new DiningTable("DUP-" + uniqueId, 4);

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

        employee.setEmployeeName("Duplicate Tester");
        employee.setUsername("duplicate-" + uniqueId);
        employee.setPassword("test-password");
        employee.setRole("STAFF");
        employee.setCreatedAt(LocalDateTime.now());

        Employee savedEmployee =
                employeeRepository.saveAndFlush(employee);

        // 4. Menu 생성
        Menu menu =
                new Menu("Duplicate Noodle", 250, "NOODLE");

        Menu savedMenu =
                menuRepository.saveAndFlush(menu);

        // 5. Order 생성
        Order order = new Order(LocalDateTime.now());

        order.setDiningSession(savedSession);
        order.setEmployee(savedEmployee);

        // 결제 가능한 주문 상태
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

        // 7. 첫 번째 결제
        Payment firstPayment =
                paymentService.createPayment(
                        savedSession.getSessionId(),
                        "CASH"
                );

        assertNotNull(firstPayment.getPaymentId());

        // 8. 두 번째 결제는 예외 발생 예상
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.createPayment(
                        savedSession.getSessionId(),
                        "CASH"
                )
        );

        assertEquals(
                "Payment already exists",
                exception.getMessage()
        );

        // 9. Payment가 한 건만 존재하는지 확인
        assertTrue(
                paymentRepository.existsByDiningSession(savedSession)
        );

        assertEquals(
                1L,
                paymentRepository.countByDiningSession(savedSession)
        );
    }
}
