package com.ecommerce.joias.service;

import com.ecommerce.joias.dto.response.ApiResponse;
import com.ecommerce.joias.dto.CategoryDto;
import com.ecommerce.joias.dto.response.CategoryResponseDto;
import com.ecommerce.joias.dto.ProductShortDto;
import com.ecommerce.joias.entity.Category;
import com.ecommerce.joias.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(CategoryDto createCategoryDto){

        if(categoryRepository.existsByName(createCategoryDto.name()))
            throw new RuntimeException("Nome de categoria já está em uso");

        // DTO -> ENTITY
        var categoryEntity = new Category();
        categoryEntity.setName(createCategoryDto.name());

        return categoryRepository.save(categoryEntity);
    }

    public CategoryResponseDto getCategoryById(Integer categoryId)
    {
       var category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

       var productList = category.getProducts().stream().map(product -> new ProductShortDto(
               product.getProductId(),
               product.getName(),
               product.getDescription()
       )).toList();

       return new CategoryResponseDto(
               category.getCategoryId(),
               category.getName(),
               productList
       );
    }

    public ApiResponse<CategoryResponseDto> listCategories(){
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
                        ).toList()
                )
        ).toList();

        return new ApiResponse<>(
                categoriesDto,
                categoriesDto.size()
        );
    }

    public void updateCategoryById(Integer categoryId, CategoryDto updateCategoryDto){
        var categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Categoria não encontrada."));

        if(!categoryEntity.getName().equals(updateCategoryDto.name())){
            boolean nameAlreadyTaken = categoryRepository.existsByNameAndCategoryIdNot(updateCategoryDto.name(), categoryEntity.getCategoryId());

            if(nameAlreadyTaken)
                throw new RuntimeException("Já existe uma categoria com este nome.");
        }

        categoryEntity.setName(updateCategoryDto.name());

        categoryRepository.save(categoryEntity);
    }

    public void deleteCategoryById(Integer categoryId){
        var categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Não existe nenhuma categoria com esse id."));

        categoryRepository.deleteById(categoryId);
    }
}
