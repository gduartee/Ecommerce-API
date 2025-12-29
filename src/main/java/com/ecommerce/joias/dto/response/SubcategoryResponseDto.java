package com.ecommerce.joias.dto.response;

import java.util.List;

public record SubcategoryResponseDto(
        Integer subCategoryId,
        String name,
        List<ProductShortResponseDto> products
) {
}
