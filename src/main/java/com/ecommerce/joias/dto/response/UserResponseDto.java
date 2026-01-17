package com.ecommerce.joias.dto.response;

import java.util.List;
import java.util.UUID;

public record UserResponseDto(
        UUID userId,
        String name,
        String email,
        String phoneNumber,
        String cpf,
        String password,
        List<AddressResponseDto> addresses
) {
}
