package com.ecommerce.joias.dto.response;

import java.util.List;

public record ProductResponseDto(
        Integer productId,
        String name,
        String description,
        String material,
        CategoryInfo category,
        List<ProductVariantResponseDto> productVariants
) {
    public record CategoryInfo(
            Integer categoryId,
            String name
    ) {}
}
