package com.company.intranet.category;

import com.company.intranet.category.dto.CategoryDto;
import com.company.intranet.category.dto.CreateCategoryRequest;
import com.company.intranet.category.dto.UpdateCategoryRequest;
import com.company.intranet.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getByType(
            @RequestParam Category.CategoryType type) {
        log.info("GET /api/categories type={}", type);
        return ResponseEntity.ok(ApiResponse.success(categoryService.getByType(type)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    public ResponseEntity<ApiResponse<CategoryDto>> create(
            @RequestBody @Valid CreateCategoryRequest request) {
        log.info("POST /api/categories name={} type={}", request.name(), request.type());
        CategoryDto result = categoryService.create(request);
        log.info("Category created id={} name={} type={}", result.id(), result.name(), result.type());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    public ResponseEntity<ApiResponse<CategoryDto>> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateCategoryRequest request) {
        log.info("PUT /api/categories/{} name={}", id, request.name());
        CategoryDto result = categoryService.update(id, request);
        log.info("Category updated id={}", id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        log.info("DELETE /api/categories/{}", id);
        categoryService.delete(id);
        log.info("Category deleted id={}", id);
        return ResponseEntity.noContent().build();
    }
}
