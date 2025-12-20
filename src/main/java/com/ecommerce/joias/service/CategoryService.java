package com.ecommerce.joias.service;

import com.ecommerce.joias.dto.response.ApiResponse;
import com.ecommerce.joias.dto.create.CreateCategoryDto;
import com.ecommerce.joias.dto.response.CategoryResponseDto;
import com.ecommerce.joias.dto.ProductShortDto;
import com.ecommerce.joias.dto.response.SubCategoriesResponseDto;
import com.ecommerce.joias.dto.update.UpdateCategoryDto;
import com.ecommerce.joias.entity.Category;
import com.ecommerce.joias.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(CreateCategoryDto createCategoryDto) {

        // DTO -> ENTITY
        var categoryEntity = new Category();
        categoryEntity.setName(createCategoryDto.name());

        if (createCategoryDto.parentId() != null) {
            var parent = categoryRepository.findById(createCategoryDto.parentId()).orElseThrow(() -> new RuntimeException("Categoria pai não encontrada"));

            parent.addSubCategory(categoryEntity);
        }

        return categoryRepository.save(categoryEntity);
    }

    public CategoryResponseDto getCategoryById(Integer categoryId) {
        var category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        return new CategoryResponseDto(
                category.getCategoryId(),
                category.getName(),
                category.getProducts().stream().map(product -> new ProductShortDto(
                        product.getProductId(),
                        product.getName(),
                        product.getDescription()
                )).toList(),
                category.getSubCategories().stream().map(subCategory -> new SubCategoriesResponseDto(
                        subCategory.getCategoryId(),
                        subCategory.getName()
                )).toList()
        );
    }

    public ApiResponse<CategoryResponseDto> listCategories() {
        var categories = categoryRepository.findAll();

        var categoriesDto = categories.stream().map(
                category -> new CategoryResponseDto(
                        category.getCategoryId(),
                        category.getName(),
                        category.getProducts().stream().map(
                                product -> new ProductShortDto(
                                        product.getProductId(),
                                        product.getName(),
                                        product.getDescription()
                                )
                        ).toList(),
                        category.getSubCategories().stream().map(subCategory -> new SubCategoriesResponseDto(
                                subCategory.getCategoryId(),
                                subCategory.getName()
                        )).toList()
                )
        ).toList();

        return new ApiResponse<>(
                categoriesDto,
                categoriesDto.size()
        );
    }

    public void updateCategoryById(Integer categoryId, UpdateCategoryDto updateCategoryDto) {
        var categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Categoria não encontrada."));

        if (updateCategoryDto.name() != null)
            categoryEntity.setName(updateCategoryDto.name());

        if (updateCategoryDto.parentId() != null) {
            // Garante que o id da categoria pai realmente mudou
            if (categoryEntity.getParent() == null || !categoryEntity.getParent().getCategoryId().equals(updateCategoryDto.parentId())) {
                var newParent = categoryRepository.findById(updateCategoryDto.parentId()).orElseThrow(() -> new RuntimeException("Nova categoria pai não encontrada."));

                // Evitar que uma categoria seja pai dela mesma
                if (newParent.getCategoryId().equals(categoryId))
                    throw new RuntimeException("Uma categoria não pode ser pai dela mesma.");

                categoryEntity.setParent(newParent);
            }
        } else {
            // Se enviou null, significa que virou categoria raiz
            categoryEntity.setParent(null)  ;
        }

        categoryRepository.save(categoryEntity);
    }

    public void deleteCategoryById(Integer categoryId) {
        var categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Não existe nenhuma categoria com esse id."));

        categoryRepository.deleteById(categoryId);
    }
}
