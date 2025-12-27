package com.ecommerce.joias.repository;

import com.ecommerce.joias.dto.response.SubcategoryResponseDto;
import com.ecommerce.joias.entity.Subcategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubcategoryRepository extends JpaRepository<Subcategory, Integer> {
    List<Subcategory> findAllByCategoryId(Integer categoryId, Pageable pageable);
}
