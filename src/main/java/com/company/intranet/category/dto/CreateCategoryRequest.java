package com.company.intranet.category.dto;

import com.company.intranet.category.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank @Size(max = 50) String name,
        @NotNull Category.CategoryType type
) {}
