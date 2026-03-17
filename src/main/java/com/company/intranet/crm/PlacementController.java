package com.company.intranet.crm;

import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.crm.dto.AssignmentDto;
import com.company.intranet.crm.dto.CreateAssignmentRequest;
import com.company.intranet.crm.dto.PlacementViewDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PlacementController {

    private final CrmService crmService;

    @GetMapping("/api/placements")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PlacementViewDto>> getPlacements() {
        return ResponseEntity.ok(ApiResponse.success(crmService.getPlacementView()));
    }

    @PostMapping("/api/assignments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AssignmentDto>> createAssignment(
            @RequestBody @Valid CreateAssignmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(crmService.createAssignment(request)));
    }

    @PutMapping("/api/assignments/{id}/end")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AssignmentDto>> endAssignment(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(crmService.endAssignment(id)));
    }
}
