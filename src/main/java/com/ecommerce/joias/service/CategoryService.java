package com.ecommerce.joias.service;

import com.ecommerce.joias.dto.response.ApiResponse;
import com.ecommerce.joias.dto.create.CreateCategoryDto;
import com.ecommerce.joias.dto.response.CategoryResponseDto;
import com.ecommerce.joias.dto.response.ProductShortResponseDto;
import com.ecommerce.joias.dto.response.SubcategoryResponseDto;
import com.ecommerce.joias.dto.update.UpdateCategoryDto;
import com.ecommerce.joias.entity.Category;
import com.ecommerce.joias.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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


        return categoryRepository.save(categoryEntity);
    }

    public CategoryResponseDto getCategoryById(Integer categoryId) {
        var category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        return new CategoryResponseDto(
                category.getCategoryId(),
                category.getName(),
                category.getSubcategories().stream().map(subcategory -> new SubcategoryResponseDto(
                        subcategory.getSubcategoryId(),
                        subcategory.getName(),
                        subcategory.getProducts().stream().map(product -> new ProductShortResponseDto(
                                product.getProductId(),
                                product.getName(),
                                product.getDescription()
                        )).toList()
                )).toList()
        );
    }

    public ApiResponse<CategoryResponseDto> listCategories(Integer page, Integer limit, String name) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Category> pageData;

        if (name != null && !name.isBlank())
            pageData = categoryRepository.findByNameContainingIgnoreCase(name, pageable);
        else
            pageData = categoryRepository.findAll(pageable);

        var categoriesDto = pageData.getContent().stream().map(
                category -> new CategoryResponseDto(
                        category.getCategoryId(),
                        category.getName(),
                        category.getSubcategories().stream().map(subCategory -> new SubcategoryResponseDto(
                                subCategory.getSubcategoryId(),
                                subCategory.getName(),
                                subCategory.getProducts().stream().map(product -> new ProductShortResponseDto(
                                        product.getProductId(),
                                        product.getName(),
                                        product.getDescription()
                                )).toList()
                        )).toList()
                )
        ).toList();

        return new ApiResponse<>(
                categoriesDto,
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.getNumber(),
                pageData.getSize()
        );
    }

    public void updateCategoryById(Integer categoryId, UpdateCategoryDto updateCategoryDto) {
        var categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Categoria não encontrada."));

        if (updateCategoryDto.name() != null)
            categoryEntity.setName(updateCategoryDto.name());

        categoryRepository.save(categoryEntity);
    }

    public void deleteCategoryById(Integer categoryId) {
        var categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Não existe nenhuma categoria com esse id."));

        // Se a lista de subcategorias não estiver vazia, proíbe a deleção
        if (!categoryEntity.getSubcategories().isEmpty())
            throw new RuntimeException("Não é possível deletar esta categoria pois existem subcategorias vinculadas à ela.");

        categoryRepository.deleteById(categoryId);
    }
}
