package com.ecommerce.joias.dto;

import jakarta.validation.constraints.Pattern;

public record UpdateEmployeeDto(
        String name,
        String email,
        @Pattern(regexp = "MANAGER|EMPLOYEE", message = "A role deve ser apenas 'MANAGER' ou 'EMPLOYEE'")
        String role
) {
}
