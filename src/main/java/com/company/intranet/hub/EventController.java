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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final HubService hubService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<EventDto>>> getEvents() {
        log.info("GET /api/events");
        return ResponseEntity.ok(ApiResponse.success(hubService.getUpcomingEvents()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EventDto>> getEventById(@PathVariable UUID id) {
        log.info("GET /api/events/{}", id);
        return ResponseEntity.ok(ApiResponse.success(hubService.getEventById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventDto>> createEvent(
            @RequestBody @Valid CreateEventRequest request,
            @CurrentUser Employee me) {
        log.info("POST /api/events employeeId={}", me.getId());
        EventDto result = hubService.createEvent(request, me);
        log.info("Event created id={}", result.id());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventDto>> updateEvent(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateEventRequest request) {
        log.info("PUT /api/events/{}", id);
        EventDto result = hubService.updateEvent(id, request);
        log.info("Event updated id={}", id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable UUID id) {
        log.info("Deleting event with id={}", id);
        hubService.deleteEvent(id);
        log.info("Event deleted successfully id={}", id);
        return ResponseEntity.noContent().build();
    }
}
