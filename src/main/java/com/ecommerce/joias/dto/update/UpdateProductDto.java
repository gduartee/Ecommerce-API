package com.ecommerce.joias.dto.update;

public record UpdateProductDto(
        Integer categoryId,
        String name,
        String description,
        String material
) {
}
