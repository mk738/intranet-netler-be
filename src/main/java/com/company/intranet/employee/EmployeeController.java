package com.company.intranet.employee;

import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.employee.dto.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.company.intranet.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    // ── Admin ─────────────────────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EmployeeDto>>> getAllEmployees() {
        log.info("GET /api/employees");
        return ResponseEntity.ok(ApiResponse.success(employeeService.getAllEmployees()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDto>> inviteEmployee(
            @RequestBody @Valid InviteEmployeeRequest request) {
        log.info("POST /api/employees email={}", request.email());
        EmployeeDto result = employeeService.inviteEmployee(request);
        log.info("Employee invited id={} email={}", result.id(), result.email());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result));
    }

    // ── Self-service — MUST come before /{id} to avoid "me" being parsed as UUID ──

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EmployeeDetailDto>> getMyProfile(
            @CurrentUser Employee me) {
        log.info("GET /api/employees/me employeeId={}", me.getId());
        return ResponseEntity.ok(ApiResponse.success(employeeService.getMyProfile(me)));
    }

    @PutMapping("/me/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EmployeeDto>> updateMyProfile(
            @RequestBody @Valid UpdateProfileRequest request,
            @CurrentUser Employee me) {
        log.info("PUT /api/employees/me/profile employeeId={}", me.getId());
        EmployeeDto result = employeeService.updateMyProfile(request, me);
        log.info("Profile updated employeeId={}", me.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/me/bank")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> updateMyBank(
            @RequestBody @Valid UpdateBankRequest request,
            @CurrentUser Employee me) {
        log.info("PUT /api/employees/me/bank employeeId={}", me.getId());
        employeeService.updateMyBank(request, me);
        log.info("Bank info updated employeeId={}", me.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Bank info updated"));
    }

    @PostMapping("/me/education")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EducationDto>> addEducation(
            @RequestBody @Valid AddEducationRequest request,
            @CurrentUser Employee me) {
        log.info("POST /api/employees/me/education employeeId={} institution={}", me.getId(), request.institution());
        EducationDto result = employeeService.addEducation(request, me);
        log.info("Education added id={} employeeId={}", result.id(), me.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result));
    }

    @DeleteMapping("/me/education/{educationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(
            @PathVariable UUID educationId,
            @CurrentUser Employee me) {
        log.info("DELETE /api/employees/me/education/{} employeeId={}", educationId, me.getId());
        employeeService.deleteEducation(educationId, me);
        log.info("Education deleted id={} employeeId={}", educationId, me.getId());
        return ResponseEntity.noContent().build();
    }

    // ── Admin — by id ─────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDetailDto>> getEmployeeById(
            @PathVariable UUID id) {
        log.info("GET /api/employees/{}", id);
        return ResponseEntity.ok(ApiResponse.success(employeeService.getEmployeeById(id)));
    }

    @PutMapping("/{id}/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDto>> updateEmployeeProfile(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateProfileRequest request) {
        log.info("PUT /api/employees/{}/profile", id);
        EmployeeDto result = employeeService.updateEmployeeProfile(id, request);
        log.info("Employee profile updated id={}", id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/{id}/terminate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDto>> terminateEmployee(
            @PathVariable UUID id,
            @RequestBody @Valid TerminateEmployeeRequest request) {
        log.info("PUT /api/employees/{}/terminate terminationDate={}", id, request.terminationDate());
        EmployeeDto result = employeeService.terminateEmployee(id, request);
        log.info("Employee terminated id={} terminationDate={}", id, request.terminationDate());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/{id}/skills")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDto>> updateSkills(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateSkillsRequest request) {
        log.info("PUT /api/employees/{}/skills count={}", id, request.names().size());
        EmployeeDto result = employeeService.updateSkills(id, request);
        log.info("Employee skills updated id={}", id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // ── Avatar ────────────────────────────────────────────────────────────────

    @PostMapping(value = "/{id}/avatar", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN') or #me.id == #id")
    public ResponseEntity<ApiResponse<EmployeeDto>> uploadAvatar(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            @CurrentUser Employee me) {
        log.info("POST /api/employees/{}/avatar contentType={} size={}", id, file.getContentType(), file.getSize());
        EmployeeDto result = employeeService.uploadAvatar(id, file);
        log.info("Avatar uploaded employeeId={}", id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> getAvatar(@PathVariable UUID id) {
        log.info("GET /api/employees/{}/avatar", id);
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
        log.info("GET /api/employees/{}/contract", id);
        return ResponseEntity.ok(ApiResponse.success(employeeService.getContract(id)));
    }

    @PostMapping(value = "/{id}/contract", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> uploadContract(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        log.info("POST /api/employees/{}/contract contentType={} size={}", id, file.getContentType(), file.getSize());
        employeeService.uploadContract(id, file);
        log.info("Contract uploaded employeeId={}", id);
        return ResponseEntity.ok(ApiResponse.success(null, "Contract uploaded"));
    }

    // ── CV ────────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/cv")
    @PreAuthorize("hasRole('ADMIN') or #me.id == #id")
    public ResponseEntity<ApiResponse<ContractDto>> getCv(
            @PathVariable UUID id,
            @CurrentUser Employee me) {
        log.info("GET /api/employees/{}/cv", id);
        return ResponseEntity.ok(ApiResponse.success(employeeService.getCv(id)));
    }

    @PostMapping(value = "/{id}/cv", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> uploadCv(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        log.info("POST /api/employees/{}/cv contentType={} size={}", id, file.getContentType(), file.getSize());
        employeeService.uploadCv(id, file);
        log.info("CV uploaded employeeId={}", id);
        return ResponseEntity.ok(ApiResponse.success(null, "CV uploaded"));
    }

    // ── Benefits ──────────────────────────────────────────────────────────────

    @GetMapping("/{id}/benefits")
    @PreAuthorize("hasRole('ADMIN') or #me.id == #id")
    public ResponseEntity<ApiResponse<List<BenefitDto>>> getBenefits(
            @PathVariable UUID id,
            @CurrentUser Employee me) {
        log.info("GET /api/employees/{}/benefits", id);
        return ResponseEntity.ok(ApiResponse.success(employeeService.getBenefits(id)));
    }

    @PutMapping("/{id}/benefits")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BenefitDto>>> replaceBenefits(
            @PathVariable UUID id,
            @RequestBody @Valid List<BenefitRequest> requests) {
        log.info("PUT /api/employees/{}/benefits count={}", id, requests.size());
        List<BenefitDto> result = employeeService.replaceBenefits(id, requests);
        log.info("Benefits replaced employeeId={} count={}", id, result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
