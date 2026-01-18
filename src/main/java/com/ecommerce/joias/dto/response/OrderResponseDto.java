package com.ecommerce.joias.dto.response;

import com.ecommerce.joias.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponseDto(
        Integer orderId,
        LocalDateTime createdAt,
        BigDecimal totalPrice,
        OrderStatus status,
        String trackingCode,
        UserOrderDto user,
        AddressOrderDto address,
        List<OrderItemResponseDto> items
) {
    public record UserOrderDto(
            UUID userId,
            String name,
            String email,
            String phoneNumber,
            String cpf
    ) {}

    public record AddressOrderDto(
            String cep,
            String street,
            String num
    ) {}

    public record OrderItemResponseDto(
      Integer orderItemId,
      String productName,
      String variantSize,
      String productSku,
      Integer quantity,
      BigDecimal unitPrice,
      BigDecimal subTotal // (qtd * preco)
    ){}
}
