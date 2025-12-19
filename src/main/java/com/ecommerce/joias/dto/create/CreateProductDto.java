package com.ecommerce.joias.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductDto(
        @NotNull(message = "Id da categoria obrigatório")
        Integer categoryId,

        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotBlank(message = "Descrição do produto obrigatória")
        String description,

        @NotBlank(message = "Material obrigatório")
        String material
) {
}
