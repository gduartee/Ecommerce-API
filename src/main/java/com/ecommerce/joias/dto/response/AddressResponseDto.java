package com.ecommerce.joias.dto.response;

public record AddressResponseDto(
        Integer addressId,
        String cep,
        String street,
        String num
) {
}
