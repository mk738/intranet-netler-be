package com.company.intranet.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByTypeOrderByNameAsc(Category.CategoryType type);

    boolean existsByNameIgnoreCaseAndType(String name, Category.CategoryType type);

    boolean existsByNameIgnoreCaseAndTypeAndIdNot(String name, Category.CategoryType type, UUID id);
}
