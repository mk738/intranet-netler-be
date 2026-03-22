package com.company.intranet.skill;

import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.employee.Employee;
import com.company.intranet.security.CurrentUser;
import com.company.intranet.skill.dto.AddSkillsRequest;
import com.company.intranet.skill.dto.SkillDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    // ── Catalog ───────────────────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<SkillDto>>> getAllSkills() {
        return ResponseEntity.ok(ApiResponse.success(skillService.getAllSkills()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SkillDto>>> addSkills(
            @RequestBody @Valid AddSkillsRequest request) {
        return ResponseEntity.ok(ApiResponse.success(skillService.addSkills(request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSkill(@PathVariable UUID id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }

    // ── Employee skills ───────────────────────────────────────────────────────

    @GetMapping("/employees/{employeeId}")
    @PreAuthorize("hasRole('ADMIN') or #me.id == #employeeId")
    public ResponseEntity<ApiResponse<List<SkillDto>>> getEmployeeSkills(
            @PathVariable UUID employeeId,
            @CurrentUser Employee me) {
        return ResponseEntity.ok(ApiResponse.success(skillService.getEmployeeSkills(employeeId)));
    }

    @PostMapping("/employees/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SkillDto>>> setEmployeeSkills(
            @PathVariable UUID employeeId,
            @RequestBody @Valid AddSkillsRequest request) {
        return ResponseEntity.ok(ApiResponse.success(skillService.setEmployeeSkills(employeeId, request)));
    }
}
