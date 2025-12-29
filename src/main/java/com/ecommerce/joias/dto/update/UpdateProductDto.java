package com.ecommerce.joias.dto.update;

public record UpdateProductDto(
        Integer subcategoryId,
        String name,
        String description,
        String material
) {
}
