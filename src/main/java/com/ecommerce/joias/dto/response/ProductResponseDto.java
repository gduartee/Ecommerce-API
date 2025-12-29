package com.ecommerce.joias.dto.response;

import java.util.List;

public record ProductResponseDto(
        Integer productId,
        String name,
        String description,
        String material,
        SubcategoryInfo subcategory,
        List<ProductVariantResponseDto> productVariants
) {
    public record SubcategoryInfo(
            Integer subcategoryId,
            String name
    ) {}
}
