package com.ecommerce.joias.repository;

import com.ecommerce.joias.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
    boolean existsBySku(String sku);
    boolean existsBySkuAndProductVariantIdNot(String sku, Integer productVariantId);
}
