package com.ecommerce.joias.dto.response;

import com.ecommerce.joias.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(
        Integer orderId,
        LocalDateTime createdAt,
        BigDecimal totalPrice,
        OrderStatus status,
        String trackingCode,
        List<OrderItemResponseDto> items
) {
    public record OrderItemResponseDto(
      Integer orderItemId,
      String productSku,
      Integer quantity,
      BigDecimal unitPrice,
      BigDecimal subTotal // (qtd * preco)
    ){}
}
