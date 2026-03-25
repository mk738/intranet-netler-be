package com.company.intranet.candidate;

import com.company.intranet.candidate.dto.CandidateDto;
import com.company.intranet.candidate.dto.CreateCandidateRequest;
import com.company.intranet.candidate.dto.PatchCandidateRequest;
import com.company.intranet.common.response.ApiResponse;
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
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
@Slf4j
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CandidateDto>>> getAll() {
        log.info("GET /api/candidates");
        return ResponseEntity.ok(ApiResponse.success(candidateService.getAll()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CandidateDto>> create(
            @RequestBody @Valid CreateCandidateRequest request) {
        log.info("POST /api/candidates name={}", request.name());
        CandidateDto result = candidateService.create(request);
        log.info("Candidate created id={}", result.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CandidateDto>> patch(
            @PathVariable UUID id,
            @RequestBody @Valid PatchCandidateRequest request) {
        log.info("PATCH /api/candidates/{}", id);
        CandidateDto result = candidateService.patch(id, request);
        log.info("Candidate updated id={}", id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        log.info("DELETE /api/candidates/{}", id);
        candidateService.delete(id);
        log.info("Candidate deleted id={}", id);
        return ResponseEntity.noContent().build();
    }
}
