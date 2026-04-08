package com.company.intranet.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OnboardingItemRepository extends JpaRepository<OnboardingItem, UUID> {

    List<OnboardingItem> findByEmployeeId(UUID employeeId);

    void deleteByEmployeeId(UUID employeeId);

    boolean existsByEmployeeIdAndCompleted(UUID employeeId, boolean completed);

    java.util.Optional<OnboardingItem> findByEmployeeIdAndTask(UUID employeeId, OnboardingTask task);
}
