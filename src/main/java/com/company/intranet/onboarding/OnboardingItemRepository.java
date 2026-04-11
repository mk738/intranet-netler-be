package com.company.intranet.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OnboardingItemRepository extends JpaRepository<OnboardingItem, UUID> {

    List<OnboardingItem> findByEmployeeIdOrderBySortOrderAsc(UUID employeeId);

    void deleteByEmployeeId(UUID employeeId);

    boolean existsByEmployeeIdAndCompleted(UUID employeeId, boolean completed);

    Optional<OnboardingItem> findByEmployeeIdAndTaskKey(UUID employeeId, String taskKey);

    boolean existsByTaskKey(String taskKey);
}
