package com.ecommerce.joias.dto.create;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryDto(
        @NotBlank(message = "Nome da categoria obrigatório")
        String name,

        Integer parentId
) {
}
