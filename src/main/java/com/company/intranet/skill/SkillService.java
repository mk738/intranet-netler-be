package com.company.intranet.skill;

import com.company.intranet.common.exception.AppException;
import com.company.intranet.common.exception.ErrorCode;
import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeRepository;
import com.company.intranet.skill.dto.AddSkillsRequest;
import com.company.intranet.skill.dto.SkillDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository    skillRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<SkillDto> getAllSkills() {
        return skillRepository.findAllByOrderByNameAsc().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Upserts a list of skill names — creates skills that don't exist yet,
     * silently skips those that do. Returns the full catalog sorted by name.
     */
    @Transactional
    public List<SkillDto> addSkills(AddSkillsRequest request) {
        List<String> normalized = normalize(request.names());

        Map<String, Skill> existing = skillRepository.findByNameIn(normalized).stream()
                .collect(Collectors.toMap(Skill::getName, s -> s));

        normalized.stream()
                .filter(name -> !existing.containsKey(name))
                .map(name -> Skill.builder().name(name).build())
                .forEach(skillRepository::save);

        return skillRepository.findAllByOrderByNameAsc().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Upserts the given skill names into the catalog and replaces the employee's
     * skill set with the result. Unknown skills are created automatically.
     */
    @Transactional
    public List<SkillDto> setEmployeeSkills(UUID employeeId, AddSkillsRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(
                        ErrorCode.EMPLOYEE_NOT_FOUND,
                        "Employee not found",
                        HttpStatus.NOT_FOUND));

        List<Skill> resolved = resolveSkills(request.names());
        employee.getSkills().clear();
        employee.getSkills().addAll(resolved);
        employeeRepository.save(employee);

        return resolved.stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SkillDto> getEmployeeSkills(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(
                        ErrorCode.EMPLOYEE_NOT_FOUND,
                        "Employee not found",
                        HttpStatus.NOT_FOUND));

        return employee.getSkills().stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void deleteSkill(UUID id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
        skillRepository.delete(skill);
    }

    /**
     * Finds or creates each skill by name. Used internally when assigning skills
     * to an employee — unknown names are added to the catalog automatically.
     */
    @Transactional
    public List<Skill> resolveSkills(List<String> names) {
        List<String> normalized = normalize(names);

        Map<String, Skill> existing = skillRepository.findByNameIn(normalized).stream()
                .collect(Collectors.toMap(Skill::getName, s -> s));

        return normalized.stream()
                .map(name -> existing.computeIfAbsent(
                        name,
                        n -> skillRepository.save(Skill.builder().name(n).build())))
                .toList();
    }

    public SkillDto toDto(Skill skill) {
        return new SkillDto(skill.getId(), skill.getName());
    }

    private List<String> normalize(List<String> names) {
        return names.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .distinct()
                .toList();
    }
}
