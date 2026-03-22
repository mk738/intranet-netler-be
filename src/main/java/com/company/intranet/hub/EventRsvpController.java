package com.company.intranet.hub;

import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.employee.Employee;
import com.company.intranet.hub.dto.EventRsvpDto;
import com.company.intranet.hub.dto.RsvpRequest;
import com.company.intranet.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Slf4j
public class EventRsvpController {

    private final EventRsvpService rsvpService;

    @GetMapping("/{id}/rsvp")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EventRsvpDto>> getRsvp(
            @PathVariable UUID id,
            @CurrentUser Employee me) {
        log.info("GET /api/events/{}/rsvp employeeId={}", id, me.getId());
        return ResponseEntity.ok(ApiResponse.success(rsvpService.getRsvp(id, me)));
    }

    @PostMapping("/{id}/rsvp")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EventRsvpDto>> submitRsvp(
            @PathVariable UUID id,
            @RequestBody @Valid RsvpRequest request,
            @CurrentUser Employee me) {
        log.info("POST /api/events/{}/rsvp employeeId={}", id, me.getId());
        EventRsvpDto result = rsvpService.submitRsvp(id, request, me);
        log.info("RSVP submitted eventId={} employeeId={}", id, me.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
