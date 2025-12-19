package com.ecommerce.joias.controller;

import com.ecommerce.joias.dto.response.ApiResponse;
import com.ecommerce.joias.dto.CategoryDto;
import com.ecommerce.joias.dto.response.CategoryResponseDto;
import com.ecommerce.joias.entity.Category;
import com.ecommerce.joias.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody @Valid CategoryDto createCategoryDto){
        var createdCategory = categoryService.createCategory(createCategoryDto);

        URI location = URI.create("/categories/" + createdCategory.getCategoryId());

        return ResponseEntity.created(location).body(createdCategory);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable("categoryId") Integer categoryId){
        var category = categoryService.getCategoryById(categoryId);

        return ResponseEntity.ok(category);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CategoryResponseDto>> listCategories(){
        var categories = categoryService.listCategories();

        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Void> updateCategoryById(@PathVariable("categoryId") Integer categoryId, @RequestBody @Valid CategoryDto updateCategoryDto){

        categoryService.updateCategoryById(categoryId, updateCategoryDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable("categoryId") Integer categoryId){
        categoryService.deleteCategoryById(categoryId);

        return ResponseEntity.noContent().build();
    }
}
