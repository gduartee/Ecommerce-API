package com.ecommerce.joias.controller;

import com.ecommerce.joias.dto.response.ImageResponseDto;
import com.ecommerce.joias.entity.Image;
import com.ecommerce.joias.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequestMapping("/images")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/{parentType}/{parentId}")
    public ResponseEntity<ImageResponseDto> uploadImage(@PathVariable("parentType") String parentType,
                                                        @PathVariable("parentId") Integer parentId,
                                                        @RequestParam("file") MultipartFile file,
                                                        @RequestParam(value = "isMain", defaultValue = "false") Boolean isMain){

        var savedImage = imageService.uploadImage(file, parentId, parentType, isMain);

        URI location = URI.create("/images/" + savedImage.imageId());

        return ResponseEntity.created(location).body(savedImage);
    }
}
