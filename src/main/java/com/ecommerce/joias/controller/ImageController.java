package com.ecommerce.joias.controller;

import com.ecommerce.joias.dto.response.ApiResponse;
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
                                                        @RequestParam(value = "isMain", defaultValue = "false") Boolean isMain) {

        var savedImage = imageService.uploadImage(file, parentId, parentType, isMain);

        URI location = URI.create("/images/" + savedImage.imageId());

        return ResponseEntity.created(location).body(savedImage);
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<ImageResponseDto> getImageById(@PathVariable("imageId") Integer imageId) {
        var imageDto = imageService.getImageById(imageId);

        return ResponseEntity.ok(imageDto);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ImageResponseDto>> listImages(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "limit", defaultValue = "10") Integer limit
    ) {
        var listImagesDto = imageService.listImages(page, limit);

        return ResponseEntity.ok(listImagesDto);
    }

    @GetMapping("/{parentId}/{parentType}")
    public ResponseEntity<ApiResponse<ImageResponseDto>> findByParentIdAndParentType(@PathVariable("parentId") Integer parentId, @PathVariable("parentType") String parentType,
                                                                                     @RequestParam(name = "page", defaultValue = "0") int page,
                                                                                     @RequestParam(name = "limit", defaultValue = "10") int limit) {

        var imagesDto = imageService.findByParentIdAndParentType(parentId, parentType, page, limit);

        return ResponseEntity.ok(imagesDto);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImageById(@PathVariable("imageId") Integer imageId) {
        imageService.deleteImageById(imageId);

        return ResponseEntity.noContent().build();
    }
}
