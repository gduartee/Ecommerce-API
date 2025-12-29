package com.ecommerce.joias.service;

import com.ecommerce.joias.dto.create.CreateProductDto;
import com.ecommerce.joias.dto.response.ApiResponse;
import com.ecommerce.joias.dto.response.ProductResponseDto;
import com.ecommerce.joias.dto.response.ProductVariantResponseDto;
import com.ecommerce.joias.dto.update.UpdateProductDto;
import com.ecommerce.joias.entity.Product;
import com.ecommerce.joias.repository.CategoryRepository;
import com.ecommerce.joias.repository.ProductRepository;
import com.ecommerce.joias.repository.SubcategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final SubcategoryRepository subcategoryRepository;

    public ProductService(ProductRepository productRepository, SubcategoryRepository subcategoryRepository) {
        this.productRepository = productRepository;
        this.subcategoryRepository = subcategoryRepository;
    }

    public ProductResponseDto createProduct(CreateProductDto createProductDto) {
        var subcategory = subcategoryRepository.findById(createProductDto.subcategoryId()).orElseThrow(() -> new RuntimeException("Subcategoria correspondente ao id fornecido não encontrada."));

        // DTO -> ENTITY
        var productEntity = new Product();
        productEntity.setName(createProductDto.name());
        productEntity.setSubcategory(subcategory);
        productEntity.setDescription(createProductDto.description());
        productEntity.setMaterial(createProductDto.material());

        var productSaved = productRepository.save(productEntity);

        var subcategoryInfo = new ProductResponseDto.SubcategoryInfo(
                subcategory.getSubcategoryId(),
                subcategory.getName()
        );

        return new ProductResponseDto(
                productSaved.getProductId(),
                productSaved.getName(),
                productSaved.getDescription(),
                productSaved.getMaterial(),
                subcategoryInfo,
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

        var categoryInfo = new ProductResponseDto.SubcategoryInfo(
                product.getSubcategory().getSubcategoryId(),
                product.getSubcategory().getName()
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

    public ApiResponse<ProductResponseDto> listProducts(Integer page, Integer limit, String name) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Product> pageData;

        if(name != null && !name.isBlank())
            pageData = productRepository.findByNameContainingIgnoreCase(name, pageable);
        else
            pageData = productRepository.findAll(pageable);

        var productsDto = pageData.getContent().stream().map(product -> new ProductResponseDto(
                product.getProductId(),
                product.getName(),
                product.getDescription(),
                product.getMaterial(),
                new ProductResponseDto.SubcategoryInfo(
                        product.getSubcategory().getSubcategoryId(),
                        product.getSubcategory().getName()
                ),

                product.getVariants().stream().map(productVariant -> new ProductVariantResponseDto(
                        productVariant.getProductVariantId(),
                        productVariant.getSize(),
                        productVariant.getSku(),
                        productVariant.getPrice(),
                        productVariant.getStockQuantity(),
                        productVariant.getWeightGrams()
                )).toList()
        )).toList();

        return new ApiResponse<>(
                productsDto,
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.getNumber(),
                pageData.getSize()
        );
    }

    public void updateProductById(Integer productId, UpdateProductDto updateProductDto) {
        var productEntity = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Produto não encontrado."));

        if (updateProductDto.subcategoryId() != null && updateProductDto.subcategoryId() != productEntity.getSubcategory().getSubcategoryId()) {
            var subcategory = subcategoryRepository.findById(updateProductDto.subcategoryId()).orElseThrow(() -> new RuntimeException("Subcategoria não encontrada"));

            productEntity.setSubcategory(subcategory);
        }

        if (updateProductDto.name() != null)
            productEntity.setName(updateProductDto.name());

        if (updateProductDto.description() != null)
            productEntity.setDescription(updateProductDto.description());

        if (updateProductDto.material() != null)
            productEntity.setMaterial(updateProductDto.material());

        productRepository.save(productEntity);
    }

    public void deleteProductById(Integer productId) {
        productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Produto com esse id não encontrado"));

        productRepository.deleteById(productId);
    }
}
