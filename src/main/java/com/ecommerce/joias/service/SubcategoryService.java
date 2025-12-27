package com.ecommerce.joias.service;

import com.ecommerce.joias.dto.create.CreateSubcategoryDto;
import com.ecommerce.joias.dto.response.ApiResponse;
import com.ecommerce.joias.dto.response.SubcategoryResponseDto;
import com.ecommerce.joias.dto.update.UpdateSubcategoryDto;
import com.ecommerce.joias.entity.Subcategory;
import com.ecommerce.joias.repository.CategoryRepository;
import com.ecommerce.joias.repository.SubcategoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;

@Service
public class SubcategoryService {
    private final SubcategoryRepository subcategoryRepository;
    private final CategoryRepository categoryRepository;

    public SubcategoryService(SubcategoryRepository subcategoryRepository, CategoryRepository categoryRepository) {
        this.subcategoryRepository = subcategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    public SubcategoryResponseDto createSubcategory(CreateSubcategoryDto createSubcategoryDto) {
        var categoryEntity = categoryRepository.findById(createSubcategoryDto.categoryId()).orElseThrow(() -> new RuntimeException("Categoria com esse id não encontrada"));

        // DTO -> ENTITY
        var subcategoryEntity = new Subcategory();
        subcategoryEntity.setName(createSubcategoryDto.name());
        subcategoryEntity.setCategory(categoryEntity);

        var subcategorySaved = subcategoryRepository.save(subcategoryEntity);

        return new SubcategoryResponseDto(
                subcategorySaved.getSubcategoryId(),
                subcategorySaved.getName()
        );
    }

    public SubcategoryResponseDto getSubcategoryById(Integer subcategoryId) {
        var subcategoryEntity = subcategoryRepository.findById(subcategoryId).orElseThrow(() -> new RuntimeException("Subcategoria não encontrada!"));

        return new SubcategoryResponseDto(
                subcategoryEntity.getSubcategoryId(),
                subcategoryEntity.getName()
        );
    }

    public ApiResponse<SubcategoryResponseDto> getSubcategoriesByCategoryId(Integer categoryId, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit);
        var subcategoriesEntity = subcategoryRepository.findAllByCategoryId(categoryId, pageable);

        var subcategoriesDto = subcategoriesEntity.stream().map(subcategory -> new SubcategoryResponseDto(
                subcategory.getSubcategoryId(),
                subcategory.getName()
        )).toList();

        return new ApiResponse<>(
                subcategoriesDto,

                )

    }

    public void updateSubcategoryById(Integer subcategoryId, UpdateSubcategoryDto updateSubcategoryDto) {
        var subcategoryEntity = subcategoryRepository.findById(subcategoryId).orElseThrow(() -> new RuntimeException("Subcategoria não encontrada!"));

        if (updateSubcategoryDto.name() != null)
            subcategoryEntity.setName(updateSubcategoryDto.name());

        subcategoryRepository.save(subcategoryEntity);
    }

    public void deleteSubcategoryById(Integer subcategoryId) {
        subcategoryRepository.findById(subcategoryId).orElseThrow(() -> new RuntimeException("Subcategoria não encontrada!"));

        subcategoryRepository.deleteById(subcategoryId);
    }
}
