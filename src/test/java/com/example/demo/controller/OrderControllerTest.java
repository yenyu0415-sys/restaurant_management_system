
package com.example.demo.controller;

import com.example.demo.dto.CreateOrderRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderStatus;
import com.example.demo.service.OrderService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;


    // 1. 전체 주문 조회 테스트
    @Test
    void getAllOrders_shouldReturnEmptyList() throws Exception {

        // Given
        when(orderService.getAllOrders())
                .thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        // Verify
        verify(orderService).getAllOrders();
    }


    // 2. 주문 단건 조회 테스트
    @Test
    void getOrderById_shouldReturnOrder() throws Exception {

        // Given
        LocalDateTime orderTime =
                LocalDateTime.of(2026, 11, 20, 14, 0);

        OrderResponse response = new OrderResponse(
                1L,
                10L,
                3L,
                orderTime,
                OrderStatus.PENDING,
                List.of(),
                new BigDecimal("150.00")
        );

        when(orderService.getOrderById(1L))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.sessionId").value(10))
                .andExpect(jsonPath("$.employeeId").value(3))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(150.00))
                .andExpect(jsonPath("$.items").isEmpty());

        // Verify
        verify(orderService).getOrderById(1L);
    }


    // 3. 주문 생성 테스트
    @Test
    void createOrder_shouldReturnCreatedOrder() throws Exception {

        // Given
        String requestJson = """
                {
                    "sessionId": 10,
                    "employeeId": 3,
                    "items": [
                        {
                            "menuId": 1,
                            "quantity": 2
                        }
                    ]
                }
                """;

        Order savedOrder = new Order(
                LocalDateTime.of(2026, 11, 20, 14, 0)
        );

        savedOrder.setOrderId(1L);
        savedOrder.setStatus(OrderStatus.PENDING);

        when(orderService.createOrder(any(CreateOrderRequest.class)))
                .thenReturn(savedOrder);

        // When & Then
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));

        // Verify
        verify(orderService)
                .createOrder(any(CreateOrderRequest.class));
    }


    @Test
    void updateOrderStatus_shouldChangeToPreparing() throws Exception {

        // Given
        Order updatedOrder = new Order(
                LocalDateTime.of(2026, 11, 20, 14, 0)
        );

        updatedOrder.setOrderId(1L);
        updatedOrder.setStatus(OrderStatus.PREPARING);

        when(orderService.updateOrderStatus(1L, OrderStatus.PREPARING))
                .thenReturn(updatedOrder);

        // When & Then
        mockMvc.perform(patch("/orders/1/status")
                        .param("status", "PREPARING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("PREPARING"));

        // Verify
        verify(orderService)
                .updateOrderStatus(1L, OrderStatus.PREPARING);
    }

    @Test
    void getOrdersBySessionId_shouldReturnOrders() throws Exception {

        // Given
        OrderResponse response = new OrderResponse(
                1L,
                10L,
                3L,
                LocalDateTime.of(2026, 11, 20, 14, 0),
                OrderStatus.PENDING,
                List.of(),
                new BigDecimal("150.00")
        );

        when(orderService.getOrdersBySessionId(10L))
                .thenReturn(List.of(response));

        // When & Then
        mockMvc.perform(get("/orders/session/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].sessionId").value(10))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].totalAmount").value(150.00));

        // Verify
        verify(orderService).getOrdersBySessionId(10L);
    }

    @Test
    void deleteOrder_shouldReturnOk() throws Exception {

        // Given
        doNothing().when(orderService).deleteOrder(1L);

        // When & Then
        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isOk());

        // Verify
        verify(orderService).deleteOrder(1L);
    }
}
