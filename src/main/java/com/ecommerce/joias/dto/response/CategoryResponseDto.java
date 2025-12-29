package com.ecommerce.joias.dto.response;

import java.util.List;

public record CategoryResponseDto(
        Integer categoryId,
        String name,
        List<SubcategoryResponseDto> subCategories
) {
}
