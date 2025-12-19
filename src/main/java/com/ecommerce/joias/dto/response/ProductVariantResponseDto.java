package com.ecommerce.joias.dto.response;

import java.math.BigDecimal;

public record ProductVariantResponseDto(
        Integer productVariantId,
        String size,
        String sku,
        BigDecimal price,
        Integer stockQuantity,
        BigDecimal weightGrams
) {
}
