package com.ecommerce.joias.controller;

import com.ecommerce.joias.dto.create.CreateSubcategoryDto;
import com.ecommerce.joias.dto.response.ApiResponse;
import com.ecommerce.joias.dto.response.SubcategoryResponseDto;
import com.ecommerce.joias.dto.update.UpdateSubcategoryDto;
import com.ecommerce.joias.service.SubcategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/subcategories")
public class SubcategoryController {
    private final SubcategoryService subcategoryService;

    public SubcategoryController(SubcategoryService subcategoryService) {
        this.subcategoryService = subcategoryService;
    }

    @PostMapping
    public ResponseEntity<SubcategoryResponseDto> createSubcategory(@RequestBody @Valid CreateSubcategoryDto createSubcategoryDto) {
        var subcategorySaved = subcategoryService.createSubcategory(createSubcategoryDto);

        URI location = URI.create("/subcategories/" + subcategorySaved.subCategoryId());

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{subcategoryId}")
    public ResponseEntity<SubcategoryResponseDto> getSubcategoryById(@PathVariable("subcategoryId") Integer subcategoryId) {
        var subCategoryDto = subcategoryService.getSubcategoryById(subcategoryId);

        return ResponseEntity.ok(subCategoryDto);
    }

    @GetMapping("/categoryId/{categoryId}")
    public ResponseEntity<ApiResponse<SubcategoryResponseDto>> getSubcategoriesByCategoryId(
            @PathVariable("categoryId") Integer categoryId,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "limit", defaultValue = "10") Integer limit
    ) {
        var subCategories = subcategoryService.getSubcategoriesByCategoryId(categoryId, page, limit);

        return ResponseEntity.ok(subCategories);
    }

    @PutMapping("/{subcategoryId}")
    public ResponseEntity<Void> updateSubcategoryById(@PathVariable("subcategoryId") Integer subcategoryId, @RequestBody UpdateSubcategoryDto updateSubcategoryDto) {
        subcategoryService.updateSubcategoryById(subcategoryId, updateSubcategoryDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{subcategoryId}")
    public ResponseEntity<Void> deleteSubcategoryById(@PathVariable("subcategoryId") Integer subcategoryId) {
        subcategoryService.deleteSubcategoryById(subcategoryId);

        return ResponseEntity.noContent().build();
    }

}
