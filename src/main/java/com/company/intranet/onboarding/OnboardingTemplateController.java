package com.company.intranet.onboarding;

import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.onboarding.dto.CreateTemplateItemRequest;
import com.company.intranet.onboarding.dto.OnboardingTemplateItemDto;
import com.company.intranet.onboarding.dto.UpdateTemplateItemRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/onboarding/template")
@RequiredArgsConstructor
@Slf4j
public class OnboardingTemplateController {

    private final OnboardingTemplateService templateService;

    @GetMapping
    @PreAuthorize("hasAuthority('ONBOARDING_MANAGE')")
    public ResponseEntity<ApiResponse<List<OnboardingTemplateItemDto>>> getTemplate() {
        log.info("GET /api/onboarding/template");
        return ResponseEntity.ok(ApiResponse.success(templateService.getTemplate()));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ONBOARDING_MANAGE')")
    public ResponseEntity<ApiResponse<OnboardingTemplateItemDto>> create(
            @RequestBody CreateTemplateItemRequest request) {
        log.info("POST /api/onboarding/template taskKey={}", request.taskKey());
        OnboardingTemplateItemDto result = templateService.createTemplateItem(
                request.taskKey(), request.labelSv());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ONBOARDING_MANAGE')")
    public ResponseEntity<ApiResponse<OnboardingTemplateItemDto>> update(
            @PathVariable UUID id,
            @RequestBody UpdateTemplateItemRequest request) {
        log.info("PATCH /api/onboarding/template/{}", id);
        OnboardingTemplateItemDto result = templateService.updateTemplateItem(
                id, request.labelSv(), request.sortOrder(), request.active());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ONBOARDING_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        log.info("DELETE /api/onboarding/template/{}", id);
        templateService.deleteTemplateItem(id);
        return ResponseEntity.noContent().build();
    }
}
