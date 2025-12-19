package com.ecommerce.joias.dto.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderDto(
        @NotNull(message = "O ID do endereço de entrega é obrigatório")
        Integer addressId,

        @NotEmpty(message = "O pedido não pode estar vazio")
        @Valid
        List<CreateOrderItemDto> items
) {
}
