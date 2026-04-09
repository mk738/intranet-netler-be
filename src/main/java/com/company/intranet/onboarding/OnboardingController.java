package com.company.intranet.onboarding;

import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.employee.Employee;
import com.company.intranet.onboarding.dto.OnboardingItemDto;
import com.company.intranet.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees/{employeeId}/onboarding")
@RequiredArgsConstructor
@Slf4j
public class OnboardingController {

    private final OnboardingService onboardingService;

    @GetMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW_ALL')")
    public ResponseEntity<ApiResponse<List<OnboardingItemDto>>> getChecklist(
            @PathVariable UUID employeeId) {
        log.info("GET /api/employees/{}/onboarding", employeeId);
        return ResponseEntity.ok(ApiResponse.success(onboardingService.getChecklist(employeeId)));
    }

    @PatchMapping("/complete")
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW_ALL')")
    public ResponseEntity<ApiResponse<Boolean>> completeOnboarding(
            @PathVariable UUID employeeId,
            @CurrentUser Employee admin) {
        log.info("PATCH /api/employees/{}/onboarding/complete adminId={}", employeeId, admin.getId());
        return ResponseEntity.ok(ApiResponse.success(onboardingService.completeOnboarding(employeeId, admin)));
    }

    @PatchMapping("/{task}/toggle")
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW_ALL')")
    public ResponseEntity<ApiResponse<OnboardingItemDto>> toggleItem(
            @PathVariable UUID employeeId,
            @PathVariable String task,
            @CurrentUser Employee admin) {
        log.info("PATCH /api/employees/{}/onboarding/{}/toggle adminId={}", employeeId, task, admin.getId());
        OnboardingItemDto result = onboardingService.toggleItem(employeeId, task, admin);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
