package com.company.intranet.hub;

import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.employee.Employee;
import com.company.intranet.hub.dto.EventRsvpDto;
import com.company.intranet.hub.dto.RsvpRequest;
import com.company.intranet.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventRsvpController {

    private final EventRsvpService rsvpService;

    @GetMapping("/{id}/rsvp")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EventRsvpDto>> getRsvp(
            @PathVariable UUID id,
            @CurrentUser Employee me) {
        return ResponseEntity.ok(ApiResponse.success(rsvpService.getRsvp(id, me)));
    }

    @PostMapping("/{id}/rsvp")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EventRsvpDto>> submitRsvp(
            @PathVariable UUID id,
            @RequestBody @Valid RsvpRequest request,
            @CurrentUser Employee me) {
        return ResponseEntity.ok(ApiResponse.success(rsvpService.submitRsvp(id, request, me)));
    }
}
