package com.company.intranet.onboarding;

import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeRepository;
import com.company.intranet.onboarding.dto.OnboardingItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final OnboardingItemRepository onboardingItemRepository;
    private final EmployeeRepository       employeeRepository;

    @Transactional
    public void initializeForEmployee(UUID employeeId) {
        List<OnboardingItem> existing = onboardingItemRepository.findByEmployeeId(employeeId);
        Set<OnboardingTask> existingTasks = existing.stream()
                .map(OnboardingItem::getTask)
                .collect(Collectors.toSet());

        List<OnboardingItem> toCreate = Arrays.stream(OnboardingTask.values())
                .filter(task -> !existingTasks.contains(task))
                .map(task -> OnboardingItem.builder()
                        .employeeId(employeeId)
                        .task(task)
                        .completed(false)
                        .build())
                .toList();

        onboardingItemRepository.saveAll(toCreate);
    }

    @Transactional
    public List<OnboardingItemDto> getChecklist(UUID employeeId) {
        if (onboardingItemRepository.findByEmployeeId(employeeId).isEmpty()) {
            initializeForEmployee(employeeId);
        }
        List<OnboardingItem> items = onboardingItemRepository.findByEmployeeId(employeeId);

        Set<UUID> adminIds = items.stream()
                .map(OnboardingItem::getCompletedBy)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Map<UUID, String> adminNames = employeeRepository.findAllById(adminIds).stream()
                .collect(Collectors.toMap(Employee::getId, Employee::getFullName));

        return items.stream()
                .map(item -> toDto(item, adminNames))
                .toList();
    }

    @Transactional
    public OnboardingItemDto toggleItem(UUID itemId, Employee admin) {
        OnboardingItem item = onboardingItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Onboarding item not found"));

        if (item.isCompleted()) {
            item.setCompleted(false);
            item.setCompletedAt(null);
            item.setCompletedBy(null);
        } else {
            item.setCompleted(true);
            item.setCompletedAt(Instant.now());
            item.setCompletedBy(admin.getId());
        }

        OnboardingItem saved = onboardingItemRepository.save(item);
        return toDto(saved, Map.of(admin.getId(), admin.getFullName()));
    }

    @Transactional(readOnly = true)
    public boolean isOnboardingComplete(UUID employeeId) {
        List<OnboardingItem> items = onboardingItemRepository.findByEmployeeId(employeeId);
        if (items.size() < OnboardingTask.values().length) return false;
        return items.stream().allMatch(OnboardingItem::isCompleted);
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private OnboardingItemDto toDto(OnboardingItem item, Map<UUID, String> adminNames) {
        String completedByName = item.getCompletedBy() != null
                ? adminNames.get(item.getCompletedBy())
                : null;
        return new OnboardingItemDto(
                item.getId(),
                item.getTask().name(),
                item.isCompleted(),
                item.getCompletedAt(),
                completedByName
        );
    }
}
