package com.company.intranet.crm;

import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.crm.dto.ClientDto;
import com.company.intranet.crm.dto.NewClientDto;
import com.company.intranet.crm.dto.UpdateClientRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final CrmService crmService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ClientDto>>> getAllClients() {
        return ResponseEntity.ok(ApiResponse.success(crmService.getAllClients()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClientDto>> createClient(
            @RequestBody @Valid NewClientDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(crmService.createClient(request)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClientDto>> getClientById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(crmService.getClientById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClientDto>> updateClient(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateClientRequest request) {
        return ResponseEntity.ok(ApiResponse.success(crmService.updateClient(id, request)));
    }
}
