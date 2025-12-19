package com.ecommerce.joias.service;

import com.ecommerce.joias.dto.create.CreateProductDto;
import com.ecommerce.joias.dto.response.ApiResponse;
import com.ecommerce.joias.dto.response.ProductResponseDto;
import com.ecommerce.joias.dto.response.ProductVariantResponseDto;
import com.ecommerce.joias.dto.update.UpdateProductDto;
import com.ecommerce.joias.entity.Product;
import com.ecommerce.joias.repository.CategoryRepository;
import com.ecommerce.joias.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponseDto createProduct(CreateProductDto createProductDto) {
        var category = categoryRepository.findById(createProductDto.categoryId()).orElseThrow(() -> new RuntimeException("Categoria correspondente ao id fornecido não encontrada."));

        // DTO -> ENTITY
        var productEntity = new Product();
        productEntity.setName(createProductDto.name());
        productEntity.setCategory(category);
        productEntity.setDescription(createProductDto.description());
        productEntity.setMaterial(createProductDto.material());

        var productSaved = productRepository.save(productEntity);

        var categoryInfo = new ProductResponseDto.CategoryInfo(
                category.getCategoryId(),
                category.getName()
        );

        return new ProductResponseDto(
                productSaved.getProductId(),
                productSaved.getName(),
                productSaved.getDescription(),
                productSaved.getMaterial(),
                categoryInfo,
                java.util.List.of()
        );
    }

    public ProductResponseDto getProductById(Integer productId) {
        var product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        var productVariantsDto = product.getVariants().stream()
                .map(variant -> new ProductVariantResponseDto(
                        variant.getProductVariantId(),
                        variant.getSize(),
                        variant.getSku(),
                        variant.getPrice(),
                        variant.getStockQuantity(),
                        variant.getWeightGrams()
                )).toList();

        var categoryInfo = new ProductResponseDto.CategoryInfo(
                product.getCategory().getCategoryId(),
                product.getCategory().getName()
        );

        return new ProductResponseDto(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getMaterial(),
                categoryInfo,
                productVariantsDto
        );
    }

    public ApiResponse<ProductResponseDto> listProducts() {
        var products = productRepository.findAll();

        var productsDto = products.stream().map(product -> new ProductResponseDto(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getMaterial(),
                new ProductResponseDto.CategoryInfo(
                        product.getCategory().getCategoryId(),
                        product.getCategory().getName()
                ),

                product.getVariants().stream().map(productVariant -> new ProductVariantResponseDto(
                        product.getVariants().getFirst().getProductVariantId(),
                        product.getVariants().getFirst().getSize(),
                        product.getVariants().getFirst().getSku(),
                        product.getVariants().getFirst().getPrice(),
                        product.getVariants().getFirst().getStockQuantity(),
                        product.getVariants().getFirst().getWeightGrams()
                )).toList()


        )).toList();

        return new ApiResponse<>(
                productsDto,
                productsDto.size()
        );
    }

    public void updateProductById(Integer productId, UpdateProductDto updateProductDto) {
        var productEntity = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Produto não encontrado."));

        if(updateProductDto.name() != null)
            productEntity.setName(updateProductDto.name());

        if(updateProductDto.description() != null)
            productEntity.setDescription(updateProductDto.description());

        if(updateProductDto.material() != null)
            productEntity.setMaterial(updateProductDto.material());

        productRepository.save(productEntity);
    }

    public void deleteProductById(Integer productId) {
        productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Produto com esse id não encontrado"));

        productRepository.deleteById(productId);
    }
}
