package com.ecommerce.joias.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateProductVariantDto(
        @NotNull(message = "Id do produto obrigatório")
        Integer productId,

        @NotBlank(message = "Tamanho do produto obrigatório")
        String size,

        @NotBlank(message = "Código SKU obrigatório")
        String sku,

        @NotNull(message = "Preço do produto obrigatório")
        BigDecimal price,

        @NotNull(message = "Quantidade disponível no estoque obrigatório")
        @Positive
        Integer stockQuantity,

        @NotNull(message = "Peso em gramas obrigatório")
        BigDecimal weightGrams
) {
}
