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


    // Test 1: Empty order items
    @Test
    void createOrder_WhenItemsAreEmpty_ShouldThrowException() {

        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(Collections.emptyList());

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(request)
        );
    }


    // Test 2: Null order items
    @Test
    void createOrder_WhenItemsAreNull_ShouldThrowException() {

        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(null);

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(request)
        );
    }


    // Test 3: Dining session not found
    @Test
    void createOrder_WhenDiningSessionNotFound_ShouldThrowException() {

        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setSessionId(1L);
        request.setEmployeeId(1L);

        OrderItemRequest item = new OrderItemRequest();
        item.setMenuId(1L);
        item.setQuantity(2);

        request.setItems(List.of(item));

        when(diningSessionRepository.findById(1L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                RuntimeException.class,
                () -> orderService.createOrder(request)
        );
    }


    // Test 4: Dining session is completed
    @Test
    void createOrder_WhenDiningSessionIsCompleted_ShouldThrowException() {

        // Arrange
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

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(request)
        );
    }

    @Test
    void createOrder_WhenEmployeeIsInactive_ShouldThrowException() {

        // given
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

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(request));
    }

    @Test
    void createOrder_WhenEmployeeNotFound_ShouldThrowException() {

        // given
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

        // when & then
        assertThrows(RuntimeException.class,
                () -> orderService.createOrder(request));
    }

    @Test
    void createOrder_WhenQuantityIsNegative_ShouldThrowException() {

        // given
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

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(request));
    }
    @Test
    void createOrder_WhenQuantityIsZero_ShouldThrowException() {

        // given
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

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(request));
    }
    @Test
    void createOrder_WhenMenuNotFound_ShouldThrowException() {

        // given
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

        // when & then
        assertThrows(RuntimeException.class,
                () -> orderService.createOrder(request));

    }

    @Test
    void createOrder_WhenMenuIsInactive_ShouldThrowException() {

        // given
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

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(request));
    }

    @Test
    void updateOrderStatus_WhenStatusIsNull_ShouldThrowException() {

        // given
        Long orderId = 1L;

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> orderService.updateOrderStatus(orderId, null));
    }

    @Test
    void updateOrderStatus_WhenPending_ShouldChangeToPreparing() {

        // given
        Long orderId = 1L;

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Order result = orderService.updateOrderStatus(
                orderId, OrderStatus.PREPARING
        );

        // then
        assertEquals(OrderStatus.PREPARING, result.getStatus());
    }

    @Test
    void updateOrderStatus_WhenPreparing_ShouldChangeToCompleted() {

        // given
        Long orderId = 1L;

        Order order = new Order();
        order.setStatus(OrderStatus.PREPARING);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Order result = orderService.updateOrderStatus(
                orderId, OrderStatus.COMPLETED
        );

        // then
        assertEquals(OrderStatus.COMPLETED, result.getStatus());
    }

    @Test
    void updateOrderStatus_WhenInvalidTransition_ShouldThrowException() {

        // given
        Long orderId = 1L;

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> orderService.updateOrderStatus(
                        orderId, OrderStatus.COMPLETED
                ));
    }
    @Test
    void updateOrderStatus_WhenOrderNotFound_ShouldThrowException() {

        // given
        Long orderId = 999L;

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(RuntimeException.class,
                () -> orderService.updateOrderStatus(
                        orderId, OrderStatus.PREPARING
                ));
    }
    }