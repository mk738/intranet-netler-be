package com.company.intranet.crm;

import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.crm.dto.AssignmentDto;
import com.company.intranet.crm.dto.CreateAssignmentRequest;
import com.company.intranet.crm.dto.EndAssignmentRequest;
import com.company.intranet.crm.dto.PlacementViewDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PlacementController {

    private final CrmService crmService;

    @GetMapping("/api/placements")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PlacementViewDto>> getPlacements() {
        log.info("GET /api/placements");
        return ResponseEntity.ok(ApiResponse.success(crmService.getPlacementView()));
    }

    @PostMapping("/api/assignments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AssignmentDto>> createAssignment(
            @RequestBody @Valid CreateAssignmentRequest request) {
        log.info("POST /api/assignments employeeId={} clientId={}", request.employeeId(), request.clientId());
        AssignmentDto result = crmService.createAssignment(request);
        log.info("Assignment created id={}", result.id());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result));
    }

    @PutMapping("/api/assignments/{id}/end")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AssignmentDto>> endAssignment(
            @PathVariable UUID id,
            @RequestBody(required = false) EndAssignmentRequest request) {
        log.info("PUT /api/assignments/{}/end endDate={}", id,
                request != null ? request.endDate() : null);
        AssignmentDto result = crmService.endAssignment(id, request);
        log.info("Assignment updated id={}", id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
