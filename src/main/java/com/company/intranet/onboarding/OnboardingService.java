package com.company.intranet.onboarding;

import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeRepository;
import com.company.intranet.onboarding.dto.OnboardingChecklistDto;
import com.company.intranet.onboarding.dto.OnboardingItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final OnboardingItemRepository         onboardingItemRepository;
    private final OnboardingTemplateItemRepository templateItemRepository;
    private final EmployeeRepository               employeeRepository;

    @Transactional
    public void initializeForEmployee(UUID employeeId) {
        List<OnboardingItem> existing = onboardingItemRepository.findByEmployeeIdOrderBySortOrderAsc(employeeId);
        Set<String> existingKeys = existing.stream()
                .map(OnboardingItem::getTaskKey)
                .collect(Collectors.toSet());

        List<OnboardingItem> toCreate = templateItemRepository.findByActiveTrueOrderBySortOrder().stream()
                .filter(template -> !existingKeys.contains(template.getTaskKey()))
                .map(template -> OnboardingItem.builder()
                        .employeeId(employeeId)
                        .taskKey(template.getTaskKey())
                        .labelSv(template.getLabelSv())
                        .sortOrder(template.getSortOrder())
                        .completed(false)
                        .build())
                .toList();

        onboardingItemRepository.saveAll(toCreate);
    }

    @Transactional
    public OnboardingChecklistDto getChecklist(UUID employeeId) {
        if (onboardingItemRepository.findByEmployeeIdOrderBySortOrderAsc(employeeId).isEmpty()) {
            initializeForEmployee(employeeId);
        }
        List<OnboardingItem> items = onboardingItemRepository.findByEmployeeIdOrderBySortOrderAsc(employeeId);

        Set<UUID> adminIds = items.stream()
                .map(OnboardingItem::getCompletedBy)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Map<UUID, String> adminNames = employeeRepository.findAllById(adminIds).stream()
                .collect(Collectors.toMap(Employee::getId, Employee::getFullName));

        List<OnboardingItemDto> dtos = items.stream()
                .map(item -> toDto(item, adminNames))
                .toList();

        boolean complete = !items.isEmpty() && items.stream().allMatch(OnboardingItem::isCompleted);
        return new OnboardingChecklistDto(complete, dtos);
    }

    @Transactional
    public OnboardingChecklistDto toggleItem(UUID employeeId, UUID itemId, Employee admin) {
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
        onboardingItemRepository.save(item);

        return getChecklist(employeeId);
    }

    @Transactional
    public OnboardingChecklistDto completeOnboarding(UUID employeeId, Employee admin) {
        List<OnboardingItem> items = onboardingItemRepository.findByEmployeeIdOrderBySortOrderAsc(employeeId);
        Instant now = Instant.now();
        items.stream()
                .filter(item -> !item.isCompleted())
                .forEach(item -> {
                    item.setCompleted(true);
                    item.setCompletedAt(now);
                    item.setCompletedBy(admin.getId());
                });
        onboardingItemRepository.saveAll(items);

        List<OnboardingItemDto> dtos = items.stream()
                .map(item -> toDto(item, Map.of(admin.getId(), admin.getFullName())))
                .toList();
        return new OnboardingChecklistDto(true, dtos);
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private OnboardingItemDto toDto(OnboardingItem item, Map<UUID, String> adminNames) {
        String completedByName = item.getCompletedBy() != null
                ? adminNames.get(item.getCompletedBy())
                : null;
        return new OnboardingItemDto(
                item.getId(),
                item.getTaskKey(),
                item.getLabelSv(),
                item.getSortOrder(),
                item.isCompleted(),
                item.getCompletedAt(),
                completedByName
        );
    }
}
