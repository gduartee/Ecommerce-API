package com.ecommerce.joias.service;

import com.ecommerce.joias.dto.response.ApiResponse;
import com.ecommerce.joias.dto.create.CreateCategoryDto;
import com.ecommerce.joias.dto.response.CategoryResponseDto;
import com.ecommerce.joias.dto.response.ProductShortResponseDto;
import com.ecommerce.joias.dto.response.SubCategoriesResponseDto;
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
                category.getProducts().stream().map(product -> new ProductShortResponseDto(
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

    public ApiResponse<CategoryResponseDto> listCategories(Integer page, Integer limit, String name) {
        Pageable pageable = PageRequest.of(page, limit);
        Page<Category> pageData;

        if(name != null && !name.isBlank())
            pageData = categoryRepository.findByNameContainingIgnoreCase(name, pageable);
        else
            pageData = categoryRepository.findAll(pageable);

        var categoriesDto = pageData.getContent().stream().map(
                category -> new CategoryResponseDto(
                        category.getCategoryId(),
                        category.getName(),
                        category.getProducts().stream().map(
                                product -> new ProductShortResponseDto(
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
            categoryEntity.setParent(null);
        }

        categoryRepository.save(categoryEntity);
    }

    public void deleteCategoryById(Integer categoryId) {
        var categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Não existe nenhuma categoria com esse id."));

        // Se a lista de produtos não estiver vazia, proíbe a deleção
        if (!categoryEntity.getProducts().isEmpty())
            throw new RuntimeException("Não é possível deletar esta categoria pois existem produtos vinculados a ela.");

        // Se a lista de subCategorias não estiver vazia, proíbe a deleção
        if (!categoryEntity.getSubCategories().isEmpty())
            throw new RuntimeException("Não é possível deletar pois existem subcategorias vinculadas.");

        categoryRepository.deleteById(categoryId);
    }
}
