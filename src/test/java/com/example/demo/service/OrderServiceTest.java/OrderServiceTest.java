
package com.example.demo.service.OrderServiceTest.java;

import com.example.demo.dto.CreateOrderRequest;

import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.DiningSessionRepository;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.MenuRepositoryJPA;

import com.example.demo.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    // Mock Repository
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

    // Inject Mock dependencies into OrderService
    @InjectMocks
    private OrderService orderService;


    // Test 1: Empty order items
    @Test
    void createOrder_WhenItemsAreEmpty_ShouldThrowException() {

        // Arrange
        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(java.util.Collections.emptyList());

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.createOrder(request)
        );
    }

}
