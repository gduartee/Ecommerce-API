package com.ecommerce.joias.service;

import com.ecommerce.joias.dto.response.ApiResponse;
import com.ecommerce.joias.dto.create.CreateProductVariantDto;
import com.ecommerce.joias.dto.response.ProductVariantResponseDto;
import com.ecommerce.joias.dto.update.UpdateProductVariantDto;
import com.ecommerce.joias.entity.ProductVariant;
import com.ecommerce.joias.repository.ProductRepository;
import com.ecommerce.joias.repository.ProductVariantRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

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

        if (productVariantRepository.existsBySku(createProductVariantDto.sku()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma variante com esse SKU.");

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

    public ProductVariantResponseDto getProductVariantById(Integer productVariantId) {
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

    public List<ProductVariantResponseDto> listProductVariantsByProductId(Integer productId){
        var productVariants = productVariantRepository.findAllByProductProductId(productId);

        return productVariants.stream().map(variant -> new ProductVariantResponseDto(
                variant.getProductVariantId(),
                variant.getSize(),
                variant.getSku(),
                variant.getPrice(),
                variant.getStockQuantity(),
                variant.getWeightGrams()
        )).toList();
    }

    public ProductVariantResponseDto findProductVariantByProductId(Integer productId){
        var productVariantEntity = productVariantRepository.findByProductProductId(productId).orElseThrow(() -> new RuntimeException("Variação do produto não encontrada!"));

        return new ProductVariantResponseDto(
                productVariantEntity.getProductVariantId(),
                productVariantEntity.getSize(),
                productVariantEntity.getSku(),
                productVariantEntity.getPrice(),
                productVariantEntity.getStockQuantity(),
                productVariantEntity.getWeightGrams()
        );
    }

    public ApiResponse<ProductVariantResponseDto> listProductVariants(Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit);

        var pageData = productVariantRepository.findAll(pageable);

        var productVariantsDtos = pageData.getContent().stream().map(productVariant -> new ProductVariantResponseDto(
                productVariant.getProductVariantId(),
                productVariant.getSize(),
                productVariant.getSku(),
                productVariant.getPrice(),
                productVariant.getStockQuantity(),
                productVariant.getWeightGrams()
        )).toList();

        return new ApiResponse<>(
                productVariantsDtos,
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.getNumber(),
                pageData.getSize()
        );
    }

    public void updateProductVariantById(Integer productVariantId, UpdateProductVariantDto updateProductVariantDto) {
        var productVariantEntity = productVariantRepository.findById(productVariantId).orElseThrow(() -> new RuntimeException("Variação de produto não encontrada."));


        if (updateProductVariantDto.size() != null)
            productVariantEntity.setSize(updateProductVariantDto.size());

        if (updateProductVariantDto.sku() != null) {
            if (!productVariantEntity.getSku().equals(updateProductVariantDto.sku()))
                if (productVariantRepository.existsBySkuAndProductVariantIdNot(updateProductVariantDto.sku(), productVariantId))
                    throw new RuntimeException("Já existe um produto com esse SKU");

            productVariantEntity.setSku(updateProductVariantDto.sku());
        }

        if (updateProductVariantDto.price() != null) {
            if (updateProductVariantDto.price().compareTo(BigDecimal.ZERO) <= 0)
                throw new RuntimeException("O preço deve ser maior do que 0");

            productVariantEntity.setPrice(updateProductVariantDto.price());
        }

        if (updateProductVariantDto.stockQuantity() != null) {
            if (updateProductVariantDto.stockQuantity() < 0)
                throw new RuntimeException("O estoque não pode ser negativo");

            productVariantEntity.setStockQuantity(updateProductVariantDto.stockQuantity());
        }


        if (updateProductVariantDto.weightGrams() != null) {
            if (updateProductVariantDto.weightGrams().compareTo(BigDecimal.ZERO) <= 0)
                throw new RuntimeException("O peso em gramas deve ser maior do que 0");

            productVariantEntity.setWeightGrams(updateProductVariantDto.weightGrams());
        }

        productVariantRepository.save(productVariantEntity);
    }

    public void deleteProductVariantById(Integer productVariantId) {
        productVariantRepository.findById(productVariantId).orElseThrow(() -> new RuntimeException("Variação de produto não encontrada."));

        productVariantRepository.deleteById(productVariantId);
    }
}
