package com.company.intranet.faq;

import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.employee.Employee;
import com.company.intranet.faq.dto.CreateFaqRequest;
import com.company.intranet.faq.dto.FaqItemDto;
import com.company.intranet.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/faq")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<FaqItemDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(faqService.getAll()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FaqItemDto>> create(
            @RequestBody @Valid CreateFaqRequest request,
            @CurrentUser Employee me) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(faqService.create(request, me)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FaqItemDto>> update(
            @PathVariable UUID id,
            @RequestBody @Valid CreateFaqRequest request) {
        return ResponseEntity.ok(ApiResponse.success(faqService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        faqService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
