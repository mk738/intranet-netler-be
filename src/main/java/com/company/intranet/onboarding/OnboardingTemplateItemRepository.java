package com.company.intranet.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OnboardingTemplateItemRepository extends JpaRepository<OnboardingTemplateItem, UUID> {

    List<OnboardingTemplateItem> findByActiveTrueOrderBySortOrder();

    List<OnboardingTemplateItem> findAllByOrderBySortOrderAsc();

    Optional<OnboardingTemplateItem> findByTaskKey(String taskKey);

    boolean existsByTaskKey(String taskKey);

    @Query("SELECT COALESCE(MAX(t.sortOrder), 0) FROM OnboardingTemplateItem t")
    int findMaxSortOrder();
}
