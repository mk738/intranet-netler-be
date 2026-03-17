package com.company.intranet.vacation;

import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.employee.Employee;
import com.company.intranet.security.CurrentUser;
import com.company.intranet.vacation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vacations")
@RequiredArgsConstructor
public class VacationController {

    private final VacationService vacationService;

    // ── /me and /summary MUST appear before /{id} ────────────────────────────

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<VacationDto>>> getMyVacations(
            @CurrentUser Employee me) {
        return ResponseEntity.ok(ApiResponse.success(vacationService.getMyVacations(me)));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VacationSummaryDto>> getSummary() {
        return ResponseEntity.ok(ApiResponse.success(vacationService.getSummary()));
    }

    // ── Collection endpoints ──────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<VacationDto>> submitVacation(
            @RequestBody @Valid SubmitVacationRequest request,
            @CurrentUser Employee me) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(vacationService.submitVacation(request, me)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<VacationDto>>> getAllVacations(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(vacationService.getAllVacations(status)));
    }

    // ── Item endpoints ────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> cancelVacation(
            @PathVariable UUID id,
            @CurrentUser Employee me) {
        vacationService.cancelVacation(id, me);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VacationDto>> reviewVacation(
            @PathVariable UUID id,
            @RequestBody @Valid ReviewVacationRequest request,
            @CurrentUser Employee admin) {
        return ResponseEntity.ok(ApiResponse.success(
                vacationService.reviewVacation(id, request, admin)));
    }
}
