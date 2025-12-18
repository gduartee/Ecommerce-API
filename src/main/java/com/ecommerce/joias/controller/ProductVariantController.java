package com.ecommerce.joias.controller;

import com.ecommerce.joias.dto.ApiResponse;
import com.ecommerce.joias.dto.CreateProductVariantDto;
import com.ecommerce.joias.dto.ProductVariantResponseDto;
import com.ecommerce.joias.service.ProductVariantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/variants")
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    public ProductVariantController(ProductVariantService productVariantService){
        this.productVariantService = productVariantService;
    }

    @PostMapping
    public ResponseEntity<ProductVariantResponseDto> createProduct(@RequestBody @Valid CreateProductVariantDto createProductVariantDto){
        var productVariantCreated = productVariantService.createProductVariant(createProductVariantDto);

        URI location = URI.create("/products/variants/" + productVariantCreated.productVariantId());

        return ResponseEntity.created(location).body(productVariantCreated);
    }

    @GetMapping("/{productVariantId}")
    public ResponseEntity<ProductVariantResponseDto> getProductVariantById(@PathVariable("productVariantId") Integer productVariantId){
        var productVariantDto = productVariantService.getProductVariantById(productVariantId);

        return ResponseEntity.ok(productVariantDto);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProductVariantResponseDto>> listProductVariants(){
        var productVariants = productVariantService.listProductVariants();

        return ResponseEntity.ok(productVariants);
    }
}
