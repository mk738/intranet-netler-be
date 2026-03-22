package com.company.intranet.skill;

import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.employee.Employee;
import com.company.intranet.security.CurrentUser;
import com.company.intranet.skill.dto.AddSkillsRequest;
import com.company.intranet.skill.dto.SkillDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
@Slf4j
public class SkillController {

    private final SkillService skillService;

    // ── Catalog ───────────────────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<SkillDto>>> getAllSkills() {
        log.info("GET /api/skills");
        return ResponseEntity.ok(ApiResponse.success(skillService.getAllSkills()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SkillDto>>> addSkills(
            @RequestBody @Valid AddSkillsRequest request) {
        log.info("POST /api/skills count={}", request.names().size());
        List<SkillDto> result = skillService.addSkills(request);
        log.info("Skills upserted count={}", result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSkill(@PathVariable UUID id) {
        log.info("DELETE /api/skills/{}", id);
        skillService.deleteSkill(id);
        log.info("Skill deleted id={}", id);
        return ResponseEntity.noContent().build();
    }

    // ── Employee skills ───────────────────────────────────────────────────────

    @GetMapping("/employees/{employeeId}")
    @PreAuthorize("hasRole('ADMIN') or #me.id == #employeeId")
    public ResponseEntity<ApiResponse<List<SkillDto>>> getEmployeeSkills(
            @PathVariable UUID employeeId,
            @CurrentUser Employee me) {
        log.info("GET /api/skills/employees/{}", employeeId);
        return ResponseEntity.ok(ApiResponse.success(skillService.getEmployeeSkills(employeeId)));
    }

    @PostMapping("/employees/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SkillDto>>> setEmployeeSkills(
            @PathVariable UUID employeeId,
            @RequestBody @Valid AddSkillsRequest request) {
        log.info("POST /api/skills/employees/{} count={}", employeeId, request.names().size());
        List<SkillDto> result = skillService.setEmployeeSkills(employeeId, request);
        log.info("Employee skills set employeeId={} count={}", employeeId, result.size());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
