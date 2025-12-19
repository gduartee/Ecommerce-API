package com.ecommerce.joias.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateEmployeeDto(
        @NotBlank(message = "Nome obrigatório")
        String name,

        @NotBlank(message = "E-mail obrigatório")
        String email,

        @NotBlank(message = "Senha obrigatória")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String password,

        @NotBlank(message = "ROLE obrigatório")
        @Pattern(regexp = "MANAGER|EMPLOYEE", message = "A role deve ser apenas 'MANAGER' ou 'EMPLOYEE'")
        String role
) {
}
