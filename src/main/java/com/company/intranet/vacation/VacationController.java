package com.company.intranet.vacation;

import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.employee.Employee;
import com.company.intranet.security.CurrentUser;
import com.company.intranet.vacation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vacations")
@RequiredArgsConstructor
@Slf4j
public class VacationController {

    private final VacationService vacationService;

    // ── /me and /summary MUST appear before /{id} ────────────────────────────

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<VacationDto>>> getMyVacations(
            @CurrentUser Employee me) {
        log.info("GET /api/vacations/me employeeId={}", me.getId());
        return ResponseEntity.ok(ApiResponse.success(vacationService.getMyVacations(me)));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VacationSummaryDto>> getSummary() {
        log.info("GET /api/vacations/summary");
        return ResponseEntity.ok(ApiResponse.success(vacationService.getSummary()));
    }

    // ── Collection endpoints ──────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<VacationDto>> submitVacation(
            @RequestBody @Valid SubmitVacationRequest request,
            @CurrentUser Employee me) {
        log.info("POST /api/vacations employeeId={} startDate={} endDate={}", me.getId(), request.startDate(), request.endDate());
        VacationDto result = vacationService.submitVacation(request, me);
        log.info("Vacation submitted id={} employeeId={}", result.id(), me.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<VacationDto>>> getAllVacations(
            @RequestParam(required = false) String status) {
        log.info("GET /api/vacations status={}", status);
        return ResponseEntity.ok(ApiResponse.success(vacationService.getAllVacations(status)));
    }

    // ── Item endpoints ────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> cancelVacation(
            @PathVariable UUID id,
            @CurrentUser Employee me) {
        log.info("DELETE /api/vacations/{} employeeId={}", id, me.getId());
        vacationService.cancelVacation(id, me);
        log.info("Vacation cancelled id={} employeeId={}", id, me.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VacationDto>> reviewVacation(
            @PathVariable UUID id,
            @RequestBody @Valid ReviewVacationRequest request,
            @CurrentUser Employee admin) {
        log.info("PUT /api/vacations/{}/review adminId={}", id, admin.getId());
        VacationDto result = vacationService.reviewVacation(id, request, admin);
        log.info("Vacation reviewed id={} adminId={}", id, admin.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
