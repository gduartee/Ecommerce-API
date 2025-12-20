package com.ecommerce.joias.dto.response;

import com.ecommerce.joias.dto.ProductShortDto;

import java.util.List;

public record CategoryResponseDto(
        Integer categoryId,
        String name,
        List<ProductShortDto> products,
        List<SubCategoriesResponseDto> subCategories
) {
}
