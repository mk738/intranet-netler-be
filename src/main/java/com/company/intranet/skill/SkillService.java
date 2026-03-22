package com.company.intranet.skill;

import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.skill.dto.AddSkillsRequest;
import com.company.intranet.skill.dto.SkillDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public List<SkillDto> getAllSkills() {
        return skillRepository.findAllByOrderByNameAsc().stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Upserts a list of skill names — creates skills that don't exist yet,
     * silently skips those that do. Returns the full resulting list sorted by name.
     */
    @Transactional
    public List<SkillDto> addSkills(AddSkillsRequest request) {
        List<String> normalized = request.skills().stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .distinct()
                .toList();

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

    @Transactional
    public void deleteSkill(UUID id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
        skillRepository.delete(skill);
    }

    /**
     * Finds or creates each skill by name. Used by EmployeeService when assigning
     * skills to an employee — unknown names are added to the catalog automatically.
     */
    @Transactional
    public List<Skill> resolveSkills(List<String> names) {
        List<String> normalized = names.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .distinct()
                .toList();

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
}
