package com.ecommerce.joias.dto.update;

import java.math.BigDecimal;

public record UpdateProductVariantDto(
        String size,
        String sku,
        BigDecimal price,
        Integer stockQuantity,
        BigDecimal weightGrams
) {
}
