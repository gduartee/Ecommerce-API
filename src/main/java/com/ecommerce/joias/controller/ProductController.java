package com.ecommerce.joias.controller;

import com.ecommerce.joias.dto.ApiResponse;
import com.ecommerce.joias.dto.CreateProductDto;
import com.ecommerce.joias.dto.ProductResponseDto;
import com.ecommerce.joias.dto.UpdateProductDto;
import com.ecommerce.joias.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody @Valid CreateProductDto createProductDto){
        var product = productService.createProduct(createProductDto);

        var location = URI.create("/products/" + product.productId());

        return ResponseEntity.created(location).body(product);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable("productId") Integer productId){
        var product = productService.getProductById(productId);

        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProductResponseDto>> listProducts(){
        var products = productService.listProducts();

        return ResponseEntity.ok(products);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> updateProductById(@PathVariable("productId") Integer productId, @RequestBody UpdateProductDto updateProductDto){
        productService.updateProductById(productId, updateProductDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProductById(@PathVariable("productId") Integer productId){
        productService.deleteProductById(productId);

        return ResponseEntity.noContent().build();
    }
}
