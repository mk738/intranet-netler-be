package com.company.intranet.employee;

import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.employee.dto.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.company.intranet.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // ── Admin ─────────────────────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EmployeeDto>>> getAllEmployees() {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getAllEmployees()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDto>> inviteEmployee(
            @RequestBody @Valid InviteEmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(employeeService.inviteEmployee(request)));
    }

    // ── Self-service — MUST come before /{id} to avoid "me" being parsed as UUID ──

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EmployeeDetailDto>> getMyProfile(
            @CurrentUser Employee me) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getMyProfile(me)));
    }

    @PutMapping("/me/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EmployeeDto>> updateMyProfile(
            @RequestBody @Valid UpdateProfileRequest request,
            @CurrentUser Employee me) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.updateMyProfile(request, me)));
    }

    @PutMapping("/me/bank")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> updateMyBank(
            @RequestBody @Valid UpdateBankRequest request,
            @CurrentUser Employee me) {
        employeeService.updateMyBank(request, me);
        return ResponseEntity.ok(ApiResponse.success(null, "Bank info updated"));
    }

    @PostMapping("/me/education")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EducationDto>> addEducation(
            @RequestBody @Valid AddEducationRequest request,
            @CurrentUser Employee me) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(employeeService.addEducation(request, me)));
    }

    @DeleteMapping("/me/education/{educationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(
            @PathVariable UUID educationId,
            @CurrentUser Employee me) {
        employeeService.deleteEducation(educationId, me);
        return ResponseEntity.noContent().build();
    }

    // ── Admin — by id ─────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDetailDto>> getEmployeeById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getEmployeeById(id)));
    }

    @PutMapping("/{id}/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDto>> updateEmployeeProfile(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.updateEmployeeProfile(id, request)));
    }

    @PutMapping("/{id}/terminate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDto>> terminateEmployee(
            @PathVariable UUID id,
            @RequestBody @Valid TerminateEmployeeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.terminateEmployee(id, request)));
    }

    @PutMapping("/{id}/skills")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDto>> updateSkills(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateSkillsRequest request) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.updateSkills(id, request)));
    }

    // ── Avatar ────────────────────────────────────────────────────────────────

    @PostMapping(value = "/{id}/avatar", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN') or #me.id == #id")
    public ResponseEntity<ApiResponse<EmployeeDto>> uploadAvatar(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            @CurrentUser Employee me) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.uploadAvatar(id, file)));
    }

    @GetMapping("/{id}/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> getAvatar(@PathVariable UUID id) {
        EmployeeAvatar avatar = employeeService.getAvatar(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, avatar.getContentType())
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                .body(avatar.getData());
    }

    // ── Contract ──────────────────────────────────────────────────────────────

    @GetMapping("/{id}/contract")
    @PreAuthorize("hasRole('ADMIN') or #me.id == #id")
    public ResponseEntity<ApiResponse<ContractDto>> getContract(
            @PathVariable UUID id,
            @CurrentUser Employee me) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getContract(id)));
    }

    @PostMapping(value = "/{id}/contract", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> uploadContract(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        employeeService.uploadContract(id, file);
        return ResponseEntity.ok(ApiResponse.success(null, "Contract uploaded"));
    }

    // ── CV ────────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/cv")
    @PreAuthorize("hasRole('ADMIN') or #me.id == #id")
    public ResponseEntity<ApiResponse<ContractDto>> getCv(
            @PathVariable UUID id,
            @CurrentUser Employee me) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getCv(id)));
    }

    @PostMapping(value = "/{id}/cv", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> uploadCv(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        employeeService.uploadCv(id, file);
        return ResponseEntity.ok(ApiResponse.success(null, "CV uploaded"));
    }

    // ── Benefits ──────────────────────────────────────────────────────────────

    @GetMapping("/{id}/benefits")
    @PreAuthorize("hasRole('ADMIN') or #me.id == #id")
    public ResponseEntity<ApiResponse<List<BenefitDto>>> getBenefits(
            @PathVariable UUID id,
            @CurrentUser Employee me) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getBenefits(id)));
    }

    @PutMapping("/{id}/benefits")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BenefitDto>>> replaceBenefits(
            @PathVariable UUID id,
            @RequestBody @Valid List<BenefitRequest> requests) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.replaceBenefits(id, requests)));
    }
}
