package com.ecommerce.joias.dto.create;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderItemDto(
        @NotNull(message = "O ID da variante do produto é obrigatório")
        Integer variantId,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantity
) {
}
