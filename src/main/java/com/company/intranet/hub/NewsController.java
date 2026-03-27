package com.company.intranet.hub;

import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.employee.Employee;
import com.company.intranet.hub.dto.*;
import com.company.intranet.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@Slf4j
public class NewsController {

    private final HubService hubService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<NewsListDto>> getNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @CurrentUser Employee me) {
        boolean isAdmin = me.getRole() == Employee.Role.ADMIN;
        log.info("GET /api/news page={} size={} isAdmin={}", page, size, isAdmin);
        return ResponseEntity.ok(ApiResponse.success(hubService.getNews(page, size, isAdmin)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<NewsPostDetailDto>> getNewsById(
            @PathVariable UUID id,
            @CurrentUser Employee me) {
        boolean isAdmin = me.getRole() == Employee.Role.ADMIN;
        log.info("GET /api/news/{} isAdmin={}", id, isAdmin);
        return ResponseEntity.ok(ApiResponse.success(hubService.getNewsById(id, isAdmin)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('NEWS_MANAGE')")
    public ResponseEntity<ApiResponse<NewsPostDetailDto>> createNews(
            @RequestBody @Valid CreateNewsRequest request,
            @CurrentUser Employee me) {
        log.info("POST /api/news employeeId={}", me.getId());
        NewsPostDetailDto result = hubService.createNews(request, me);
        log.info("News created id={}", result.id());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('NEWS_MANAGE')")
    public ResponseEntity<ApiResponse<NewsPostDetailDto>> updateNews(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateNewsRequest request) {
        log.info("PUT /api/news/{}", id);
        NewsPostDetailDto result = hubService.updateNews(id, request);
        log.info("News updated id={}", id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('NEWS_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> deleteNews(@PathVariable UUID id) {
        log.info("DELETE /api/news/{}", id);
        hubService.deleteNews(id);
        log.info("News deleted id={}", id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/published")
    @PreAuthorize("hasAuthority('NEWS_MANAGE')")
    public ResponseEntity<ApiResponse<NewsPostDetailDto>> publishNews(
            @PathVariable UUID id,
            @RequestBody PublishNewsRequest request) {
        log.info("PUT /api/news/{}/published published={}", id, request.publish());
        NewsPostDetailDto result = hubService.publishNews(id, request.publish());
        log.info("News published status updated id={} published={}", id, request.publish());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
