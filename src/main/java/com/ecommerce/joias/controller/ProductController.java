package com.ecommerce.joias.controller;

import com.ecommerce.joias.dto.response.ApiResponse;
import com.ecommerce.joias.dto.create.CreateProductDto;
import com.ecommerce.joias.dto.response.ProductResponseDto;
import com.ecommerce.joias.dto.update.UpdateProductDto;
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
    public ResponseEntity<ApiResponse<ProductResponseDto>> listProducts(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "limit", defaultValue = "10") Integer limit,
            @RequestParam(name = "name", required = false) String name
    ){
        var products = productService.listProducts(page, limit, name);

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
