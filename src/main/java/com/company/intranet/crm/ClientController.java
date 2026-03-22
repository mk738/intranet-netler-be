package com.company.intranet.crm;

import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.crm.dto.ClientDto;
import com.company.intranet.crm.dto.NewClientDto;
import com.company.intranet.crm.dto.UpdateClientRequest;
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
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final CrmService crmService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ClientDto>>> getAllClients() {
        log.info("GET /api/clients");
        return ResponseEntity.ok(ApiResponse.success(crmService.getAllClients()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClientDto>> createClient(
            @RequestBody @Valid NewClientDto request) {
        log.info("POST /api/clients companyName={}", request.companyName());
        ClientDto result = crmService.createClient(request);
        log.info("Client created id={}", result.id());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClientDto>> getClientById(
            @PathVariable UUID id) {
        log.info("GET /api/clients/{}", id);
        return ResponseEntity.ok(ApiResponse.success(crmService.getClientById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClientDto>> updateClient(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateClientRequest request) {
        log.info("PUT /api/clients/{}", id);
        ClientDto result = crmService.updateClient(id, request);
        log.info("Client updated id={}", id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
