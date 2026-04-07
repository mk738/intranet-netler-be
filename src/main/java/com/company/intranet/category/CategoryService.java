package com.company.intranet.category;

import com.company.intranet.category.dto.CategoryDto;
import com.company.intranet.category.dto.CreateCategoryRequest;
import com.company.intranet.category.dto.UpdateCategoryRequest;
import com.company.intranet.common.exception.AppException;
import com.company.intranet.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDto> getByType(Category.CategoryType type) {
        return categoryRepository.findByTypeOrderByNameAsc(type).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public CategoryDto create(CreateCategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCaseAndType(request.name().trim(), request.type())) {
            throw new AppException(
                    ErrorCode.CATEGORY_NAME_TAKEN,
                    "A category with that name already exists for type " + request.type(),
                    HttpStatus.CONFLICT);
        }

        Category category = Category.builder()
                .name(request.name().trim())
                .type(request.type())
                .build();

        return toDto(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDto update(UUID id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        ErrorCode.CATEGORY_NOT_FOUND,
                        "Category not found",
                        HttpStatus.NOT_FOUND));

        if (categoryRepository.existsByNameIgnoreCaseAndTypeAndIdNot(
                request.name().trim(), category.getType(), id)) {
            throw new AppException(
                    ErrorCode.CATEGORY_NAME_TAKEN,
                    "A category with that name already exists for type " + category.getType(),
                    HttpStatus.CONFLICT);
        }

        category.setName(request.name().trim());
        return toDto(categoryRepository.save(category));
    }

    @Transactional
    public void delete(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new AppException(
                    ErrorCode.CATEGORY_NOT_FOUND,
                    "Category not found",
                    HttpStatus.NOT_FOUND);
        }
        categoryRepository.deleteById(id);
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private CategoryDto toDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getType().name(),
                category.getCreatedAt() != null ? category.getCreatedAt().toString() : null
        );
    }
}
