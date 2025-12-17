package com.ecommerce.joias.service;

import com.ecommerce.joias.dto.CategoryDto;
import com.ecommerce.joias.entity.Category;
import com.ecommerce.joias.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Category getCategoryById(Integer categoryId)
    {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    }

    public List<Category> listCategories(){
        return categoryRepository.findAll();
    }

    public void updateCategoryById(Integer categoryId, CategoryDto updateCategoryDto){
        var categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Categoria não encontrada."));

        if(!categoryEntity.getName().equals(updateCategoryDto.name()))
            if(categoryRepository.existsByName(updateCategoryDto.name()))
                throw new RuntimeException("Já existe uma categoria com esse nome");

        categoryEntity.setName(updateCategoryDto.name());

        categoryRepository.save(categoryEntity);
    }

    public void deleteCategoryById(Integer categoryId){
        var categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Não existe nenhuma categoria com esse id."));

        categoryRepository.deleteById(categoryId);
    }
}
