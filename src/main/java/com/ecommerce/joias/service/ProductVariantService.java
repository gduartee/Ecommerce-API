package com.ecommerce.joias.service;

import com.ecommerce.joias.dto.ApiResponse;
import com.ecommerce.joias.dto.CreateProductVariantDto;
import com.ecommerce.joias.dto.ProductVariantResponseDto;
import com.ecommerce.joias.entity.ProductVariant;
import com.ecommerce.joias.repository.ProductRepository;
import com.ecommerce.joias.repository.ProductVariantRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductVariantService {
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;

    public ProductVariantService(ProductVariantRepository productVariantRepository, ProductRepository productRepository) {
        this.productVariantRepository = productVariantRepository;
        this.productRepository = productRepository;
    }

    public ProductVariantResponseDto createProductVariant(CreateProductVariantDto createProductVariantDto) {
       var product = productRepository.findById(createProductVariantDto.productId()).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

       var productVariantEntity = new ProductVariant(
               product,
               createProductVariantDto.size(),
               createProductVariantDto.sku(),
               createProductVariantDto.price(),
               createProductVariantDto.stockQuantity(),
               createProductVariantDto.weightGrams()
       );

       var productVariantSaved = productVariantRepository.save(productVariantEntity);

       return new ProductVariantResponseDto(
               productVariantSaved.getProductVariantId(),
               productVariantSaved.getSize(),
               productVariantSaved.getSku(),
               productVariantSaved.getPrice(),
               productVariantSaved.getStockQuantity(),
               productVariantSaved.getWeightGrams()
       );
    }

    public ProductVariantResponseDto getProductVariantById(Integer productVariantId){
        var productVariantEntity = productVariantRepository.findById(productVariantId).orElseThrow(() -> new RuntimeException("Variação de produto não encontrada"));

        return new ProductVariantResponseDto(
                productVariantEntity.getProductVariantId(),
                productVariantEntity.getSize(),
                productVariantEntity.getSku(),
                productVariantEntity.getPrice(),
                productVariantEntity.getStockQuantity(),
                productVariantEntity.getWeightGrams()
        );
    }

    public ApiResponse<ProductVariantResponseDto> listProductVariants(){
        var productVariantsDtos = productVariantRepository.findAll().stream().map(productVariant -> new ProductVariantResponseDto(
                productVariant.getProductVariantId(),
                productVariant.getSize(),
                productVariant.getSku(),
                productVariant.getPrice(),
                productVariant.getStockQuantity(),
                productVariant.getWeightGrams()
        )).toList();

        return new ApiResponse<>(
                productVariantsDtos,
                productVariantsDtos.size()
        );
    }
}
