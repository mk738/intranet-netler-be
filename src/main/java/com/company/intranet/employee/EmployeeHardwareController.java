package com.company.intranet.employee;

import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.employee.dto.EmployeeHardwareDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees/{employeeId}/hardware")
@RequiredArgsConstructor
@Slf4j
public class EmployeeHardwareController {

    private final EmployeeHardwareService hardwareService;

    @GetMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_EDIT_ANY')")
    public ResponseEntity<ApiResponse<List<EmployeeHardwareDto>>> getHardware(
            @PathVariable UUID employeeId) {
        log.info("GET /api/employees/{}/hardware", employeeId);
        return ResponseEntity.ok(ApiResponse.success(hardwareService.getHardware(employeeId)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_EDIT_ANY')")
    public ResponseEntity<ApiResponse<EmployeeHardwareDto>> addHardware(
            @PathVariable UUID employeeId,
            @RequestBody @Valid AddHardwareRequest request) {
        log.info("POST /api/employees/{}/hardware name={}", employeeId, request.name());
        EmployeeHardwareDto result = hardwareService.addHardware(employeeId, request.name());
        log.info("Hardware added id={} employeeId={}", result.id(), employeeId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result));
    }

    @DeleteMapping("/{hardwareId}")
    @PreAuthorize("hasAuthority('EMPLOYEE_EDIT_ANY')")
    public ResponseEntity<ApiResponse<Void>> removeHardware(
            @PathVariable UUID employeeId,
            @PathVariable UUID hardwareId) {
        log.info("DELETE /api/employees/{}/hardware/{}", employeeId, hardwareId);
        hardwareService.removeHardware(employeeId, hardwareId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ── Request body ──────────────────────────────────────────────────────────
    record AddHardwareRequest(@NotBlank @Size(max = 255) String name) {}
}
