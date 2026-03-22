package com.company.intranet.faq;

import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.employee.Employee;
import com.company.intranet.faq.dto.CreateFaqRequest;
import com.company.intranet.faq.dto.FaqItemDto;
import com.company.intranet.security.CurrentUser;
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
@RequestMapping("/api/faq")
@RequiredArgsConstructor
@Slf4j
public class FaqController {

    private final FaqService faqService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<FaqItemDto>>> getAll() {
        log.info("GET /api/faq");
        return ResponseEntity.ok(ApiResponse.success(faqService.getAll()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FaqItemDto>> create(
            @RequestBody @Valid CreateFaqRequest request,
            @CurrentUser Employee me) {
        log.info("POST /api/faq employeeId={}", me.getId());
        FaqItemDto result = faqService.create(request, me);
        log.info("FAQ item created id={}", result.id());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FaqItemDto>> update(
            @PathVariable UUID id,
            @RequestBody @Valid CreateFaqRequest request) {
        log.info("PUT /api/faq/{}", id);
        FaqItemDto result = faqService.update(id, request);
        log.info("FAQ item updated id={}", id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        log.info("DELETE /api/faq/{}", id);
        faqService.delete(id);
        log.info("FAQ item deleted id={}", id);
        return ResponseEntity.noContent().build();
    }
}
