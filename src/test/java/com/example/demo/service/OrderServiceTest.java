
package com.example.demo.service;

import com.example.demo.dto.CreateOrderRequest;
import com.example.demo.dto.OrderItemRequest;

import com.example.demo.entity.DiningSession;
import com.example.demo.entity.DiningSessionStatus;
import com.example.demo.entity.Employee;
import com.example.demo.entity.Menu;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;

import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.DiningSessionRepository;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.MenuRepositoryJPA;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private DiningSessionRepository diningSessionRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private MenuRepositoryJPA menuRepositoryJPA;

    @InjectMocks
    private OrderService orderService;


    // ==========================================
    // CREATE ORDER TESTS
    // ==========================================

    // Test 1: Empty order items
    @Test
    void createOrder_WhenItemsAreEmpty_ShouldThrowException() {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(Collections.emptyList());

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(request)
        );
    }


    // Test 2: Null order items
    @Test
    void createOrder_WhenItemsAreNull_ShouldThrowException() {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(request)
        );
    }


    // Test 3: Dining session not found
    @Test
    void createOrder_WhenDiningSessionNotFound_ShouldThrowException() {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setSessionId(1L);
        request.setEmployeeId(1L);

        OrderItemRequest item = new OrderItemRequest();
        item.setMenuId(1L);
        item.setQuantity(2);

        request.setItems(List.of(item));

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> orderService.createOrder(request)
        );
    }


    // Test 4: Dining session is completed
    @Test
    void createOrder_WhenDiningSessionIsCompleted_ShouldThrowException() {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setSessionId(1L);
        request.setEmployeeId(1L);

        OrderItemRequest item = new OrderItemRequest();
        item.setMenuId(1L);
        item.setQuantity(2);

        request.setItems(List.of(item));

        DiningSession diningSession = new DiningSession();
        diningSession.setSessionId(1L);
        diningSession.setStatus(DiningSessionStatus.COMPLETED);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(diningSession));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(request)
        );
    }


    // Test 5: Employee is inactive
    @Test
    void createOrder_WhenEmployeeIsInactive_ShouldThrowException() {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setSessionId(1L);
        request.setEmployeeId(1L);
        request.setItems(List.of(new OrderItemRequest()));

        DiningSession diningSession = new DiningSession();
        diningSession.setStatus(DiningSessionStatus.ACTIVE);

        Employee employee = new Employee();
        employee.setActive(false);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(diningSession));

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(request)
        );
    }


    // Test 6: Employee not found
    @Test
    void createOrder_WhenEmployeeNotFound_ShouldThrowException() {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setSessionId(1L);
        request.setEmployeeId(1L);
        request.setItems(List.of(new OrderItemRequest()));

        DiningSession diningSession = new DiningSession();
        diningSession.setStatus(DiningSessionStatus.ACTIVE);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(diningSession));

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> orderService.createOrder(request)
        );
    }


    // Test 7: Negative quantity
    @Test
    void createOrder_WhenQuantityIsNegative_ShouldThrowException() {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setSessionId(1L);
        request.setEmployeeId(1L);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setMenuId(1L);
        itemRequest.setQuantity(-1);

        request.setItems(List.of(itemRequest));

        DiningSession diningSession = new DiningSession();
        diningSession.setStatus(DiningSessionStatus.ACTIVE);

        Employee employee = new Employee();
        employee.setActive(true);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(diningSession));

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(request)
        );
    }


    // Test 8: Zero quantity
    @Test
    void createOrder_WhenQuantityIsZero_ShouldThrowException() {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setSessionId(1L);
        request.setEmployeeId(1L);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setMenuId(1L);
        itemRequest.setQuantity(0);

        request.setItems(List.of(itemRequest));

        DiningSession diningSession = new DiningSession();
        diningSession.setStatus(DiningSessionStatus.ACTIVE);

        Employee employee = new Employee();
        employee.setActive(true);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(diningSession));

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(request)
        );
    }


    // Test 9: Menu not found
    @Test
    void createOrder_WhenMenuNotFound_ShouldThrowException() {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setSessionId(1L);
        request.setEmployeeId(1L);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setMenuId(1L);
        itemRequest.setQuantity(1);

        request.setItems(List.of(itemRequest));

        DiningSession diningSession = new DiningSession();
        diningSession.setStatus(DiningSessionStatus.ACTIVE);

        Employee employee = new Employee();
        employee.setActive(true);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(diningSession));

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        when(menuRepositoryJPA.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> orderService.createOrder(request)
        );
    }


    // Test 10: Menu is inactive
    @Test
    void createOrder_WhenMenuIsInactive_ShouldThrowException() {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setSessionId(1L);
        request.setEmployeeId(1L);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setMenuId(1L);
        itemRequest.setQuantity(1);

        request.setItems(List.of(itemRequest));

        DiningSession diningSession = new DiningSession();
        diningSession.setStatus(DiningSessionStatus.ACTIVE);

        Employee employee = new Employee();
        employee.setActive(true);

        Menu menu = new Menu();
        menu.setMenuName("Noodle");
        menu.setActive(false);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(diningSession));

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        when(menuRepositoryJPA.findById(1L))
                .thenReturn(Optional.of(menu));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(request)
        );
    }


    // ==========================================
    // UPDATE ORDER STATUS TESTS
    // ==========================================

    // Test 11: Null status
    @Test
    void updateOrderStatus_WhenStatusIsNull_ShouldThrowException() {

        Long orderId = 1L;

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateOrderStatus(orderId, null)
        );
    }


    // Test 12: PENDING -> PREPARING
    @Test
    void updateOrderStatus_WhenPending_ShouldChangeToPreparing() {

        Long orderId = 1L;

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.updateOrderStatus(
                orderId,
                OrderStatus.PREPARING
        );

        assertEquals(
                OrderStatus.PREPARING,
                result.getStatus()
        );
    }


    // Test 13: PREPARING -> COMPLETED
    @Test
    void updateOrderStatus_WhenPreparing_ShouldChangeToCompleted() {

        Long orderId = 1L;

        Order order = new Order();
        order.setStatus(OrderStatus.PREPARING);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.updateOrderStatus(
                orderId,
                OrderStatus.COMPLETED
        );

        assertEquals(
                OrderStatus.COMPLETED,
                result.getStatus()
        );
    }


    // Test 14: Invalid status transition
    @Test
    void updateOrderStatus_WhenInvalidTransition_ShouldThrowException() {

        Long orderId = 1L;

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateOrderStatus(
                        orderId,
                        OrderStatus.COMPLETED
                )
        );
    }


    // Test 15: Order not found
    @Test
    void updateOrderStatus_WhenOrderNotFound_ShouldThrowException() {

        Long orderId = 999L;

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> orderService.updateOrderStatus(
                        orderId,
                        OrderStatus.PREPARING
                )
        );
    }


    // ==========================================
    // CANCEL ORDER TESTS
    // ==========================================

    // Test 16: Cancel PENDING order
    @Test
    void cancelOrder_WhenPending_ShouldChangeToCancelled() {

        Long orderId = 1L;

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.cancelOrder(orderId);

        assertEquals(
                OrderStatus.CANCELLED,
                result.getStatus()
        );
    }


    // Test 17: Cancel PREPARING order
    @Test
    void cancelOrder_WhenPreparing_ShouldChangeToCancelled() {

        Long orderId = 1L;

        Order order = new Order();
        order.setStatus(OrderStatus.PREPARING);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.cancelOrder(orderId);

        assertEquals(
                OrderStatus.CANCELLED,
                result.getStatus()
        );
    }


    // Test 18: Cannot cancel COMPLETED order
    @Test
    void cancelOrder_WhenCompleted_ShouldThrowException() {

        Long orderId = 1L;

        Order order = new Order();
        order.setStatus(OrderStatus.COMPLETED);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.cancelOrder(orderId)
        );
    }


    // Test 19: Cannot cancel CANCELLED order again
    @Test
    void cancelOrder_WhenAlreadyCancelled_ShouldThrowException() {

        Long orderId = 1L;

        Order order = new Order();
        order.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.cancelOrder(orderId)
        );
    }


    // Test 20: Order not found when cancelling
    @Test
    void cancelOrder_WhenOrderNotFound_ShouldThrowException() {

        Long orderId = 999L;

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> orderService.cancelOrder(orderId)
        );
    }


    // ==========================================
    // ADDITIONAL VALIDATION TEST
    // ==========================================

    // Test 21: Null quantity
    @Test
    void createOrder_WhenQuantityIsNull_ShouldThrowException() {

        CreateOrderRequest request = new CreateOrderRequest();
        request.setSessionId(1L);
        request.setEmployeeId(1L);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setMenuId(1L);
        itemRequest.setQuantity(null);

        request.setItems(List.of(itemRequest));

        DiningSession diningSession = new DiningSession();
        diningSession.setStatus(DiningSessionStatus.ACTIVE);

        Employee employee = new Employee();
        employee.setActive(true);

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.of(diningSession));

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(request)
        );
    }

}
