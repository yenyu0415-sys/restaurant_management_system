
package com.example.demo.service;

import com.example.demo.dto.CreateOrderRequest;
import com.example.demo.dto.OrderItemRequest;

import com.example.demo.entity.DiningSession;
import com.example.demo.entity.DiningSessionStatus;

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

import static org.junit.jupiter.api.Assertions.assertThrows;
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

}
