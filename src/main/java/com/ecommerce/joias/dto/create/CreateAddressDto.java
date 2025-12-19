package com.ecommerce.joias.dto.create;

import jakarta.validation.constraints.NotBlank;

public record CreateAddressDto(
    @NotBlank(message = "O CEP é obrigatório")
    String cep,

    @NotBlank(message = "O endereço(rua) é obrigatório")
    String street,

    @NotBlank(message = "O número da casa é obrigatório")
    String num
) {
}
