package com.ecommerce.joias.dto.response;

public record ImageResponseDto(
        Integer imageId,
        String url,
        String publicId,
        Integer parentId,
        String parentType,
        Boolean isMain
) {
}
