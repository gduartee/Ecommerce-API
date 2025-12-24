package com.ecommerce.joias.dto.response;

public record EmployeeResponseDto(
        Integer employeeId,
        String name,
        String email,
        String password,
        String role
) {
}
