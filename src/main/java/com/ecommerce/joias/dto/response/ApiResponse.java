package com.ecommerce.joias.dto.response;

import java.util.List;

public record ApiResponse<T>(
        List<T> data,
        int totalElements
) {
}
