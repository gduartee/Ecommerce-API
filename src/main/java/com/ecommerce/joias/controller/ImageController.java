package com.ecommerce.joias.controller;

import com.ecommerce.joias.service.ImageService;

public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }
}
