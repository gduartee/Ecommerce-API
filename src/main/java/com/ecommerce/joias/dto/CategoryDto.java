package com.ecommerce.joias.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryDto(
        @NotBlank(message = "Nome da categoria obrigatório")
        String name
) {
}
