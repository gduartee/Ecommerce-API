package com.ecommerce.joias.dto.update;

import com.ecommerce.joias.entity.enums.OrderStatus;

public record UpdateOrderDto(
        OrderStatus status,
        String trackingCode
) {
}
