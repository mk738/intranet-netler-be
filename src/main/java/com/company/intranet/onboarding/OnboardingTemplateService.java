package com.company.intranet.onboarding;

import com.company.intranet.common.exception.AppException;
import com.company.intranet.common.exception.ErrorCode;
import com.company.intranet.onboarding.dto.OnboardingTemplateItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OnboardingTemplateService {

    private final OnboardingTemplateItemRepository templateItemRepository;
    private final OnboardingItemRepository         onboardingItemRepository;

    @Transactional(readOnly = true)
    public List<OnboardingTemplateItemDto> getTemplate() {
        return templateItemRepository.findAllByOrderBySortOrderAsc().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public OnboardingTemplateItemDto createTemplateItem(String taskKey, String labelSv) {
        if (templateItemRepository.existsByTaskKey(taskKey)) {
            throw new AppException(
                    ErrorCode.ONBOARDING_TEMPLATE_KEY_TAKEN,
                    "A template item with taskKey '" + taskKey + "' already exists.",
                    HttpStatus.CONFLICT);
        }

        OnboardingTemplateItem item = OnboardingTemplateItem.builder()
                .taskKey(taskKey)
                .labelSv(labelSv)
                .sortOrder(templateItemRepository.findMaxSortOrder() + 1)
                .active(true)
                .build();

        return toDto(templateItemRepository.save(item));
    }

    @Transactional
    public OnboardingTemplateItemDto updateTemplateItem(UUID id, String labelSv,
                                                        Integer sortOrder, Boolean active) {
        OnboardingTemplateItem item = templateItemRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        ErrorCode.ONBOARDING_TEMPLATE_NOT_FOUND,
                        "Template item not found.",
                        HttpStatus.NOT_FOUND));

        if (labelSv != null)   item.setLabelSv(labelSv);
        if (sortOrder != null) item.setSortOrder(sortOrder);
        if (active != null)    item.setActive(active);

        return toDto(templateItemRepository.save(item));
    }

    @Transactional
    public void deleteTemplateItem(UUID id) {
        OnboardingTemplateItem item = templateItemRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        ErrorCode.ONBOARDING_TEMPLATE_NOT_FOUND,
                        "Template item not found.",
                        HttpStatus.NOT_FOUND));

        if (onboardingItemRepository.existsByTaskKey(item.getTaskKey())) {
            throw new AppException(
                    ErrorCode.ONBOARDING_TEMPLATE_IN_USE,
                    "Uppgiften används av befintliga anställda",
                    HttpStatus.CONFLICT);
        }

        templateItemRepository.delete(item);
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private OnboardingTemplateItemDto toDto(OnboardingTemplateItem item) {
        return new OnboardingTemplateItemDto(
                item.getId(),
                item.getTaskKey(),
                item.getLabelSv(),
                item.getSortOrder(),
                item.isActive()
        );
    }
}
