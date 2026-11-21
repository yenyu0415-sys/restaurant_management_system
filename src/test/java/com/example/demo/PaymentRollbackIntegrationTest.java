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
class PaymentRollbackIntegrationTest {

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
    void shouldRollbackPaymentWhenTableIsNotOccupied() {

        // 1. DiningTable 생성
        String uniqueId = UUID.randomUUID().toString();

        DiningTable table =
                new DiningTable("RB-" + uniqueId, 4);

        table.setStatus("AVAILABLE");

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

        employee.setEmployeeName("Rollback Tester");
        employee.setUsername("rb-" + uniqueId);
        employee.setPassword("test-password");
        employee.setRole("STAFF");
        employee.setCreatedAt(LocalDateTime.now());

        Employee savedEmployee =
                employeeRepository.saveAndFlush(employee);


        // 4. Menu 생성
        Menu menu =
                new Menu("Rollback Noodle", 250, "NOODLE");

        Menu savedMenu =
                menuRepository.saveAndFlush(menu);


        // 5. Order 생성
        Order order = new Order(LocalDateTime.now());

        order.setDiningSession(savedSession);
        order.setEmployee(savedEmployee);
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder =
                orderRepository.saveAndFlush(order);


        // 6. OrderItem 생성
        OrderItem orderItem = new OrderItem();

        orderItem.setOrder(savedOrder);
        orderItem.setMenu(savedMenu);
        orderItem.setQuantity(1);
        orderItem.setUnitPrice(new BigDecimal("250.00"));

        orderItemRepository.saveAndFlush(orderItem);


        // 7. PaymentService 실행 및 예외 확인
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.createPayment(
                        savedSession.getSessionId(),
                        "CASH"
                )
        );


        // 8. Payment가 롤백되었는지 확인
        assertFalse(
                paymentRepository.existsByDiningSession(savedSession)
        );


        // 9. DiningSession 상태 확인
        DiningSession foundSession =
                diningSessionRepository.findById(
                        savedSession.getSessionId()
                ).orElseThrow();

        assertEquals(
                DiningSessionStatus.ACTIVE,
                foundSession.getStatus()
        );


        // 10. DiningTable 상태 확인
        DiningTable foundTable =
                diningTableRepository.findById(
                        savedTable.getTableId()
                ).orElseThrow();

        assertEquals("AVAILABLE", foundTable.getStatus());
    }
}
