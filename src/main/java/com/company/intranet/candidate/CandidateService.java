package com.company.intranet.candidate;

import com.company.intranet.candidate.dto.CandidateDto;
import com.company.intranet.candidate.dto.CreateCandidateRequest;
import com.company.intranet.candidate.dto.PatchCandidateRequest;
import com.company.intranet.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;

    @Transactional(readOnly = true)
    public List<CandidateDto> getAll() {
        return candidateRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public CandidateDto create(CreateCandidateRequest request) {
        Candidate candidate = Candidate.builder()
                .name(request.name())
                .role(request.role())
                .email(request.email())
                .phone(request.phone())
                .notes(request.notes())
                .stage(request.stage())
                .build();

        return toDto(candidateRepository.save(candidate));
    }

    @Transactional
    public CandidateDto patch(UUID id, PatchCandidateRequest request) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        if (request.name()  != null) candidate.setName(request.name());
        if (request.role()  != null) candidate.setRole(request.role());
        if (request.email() != null) candidate.setEmail(request.email());
        if (request.phone() != null) candidate.setPhone(request.phone());
        if (request.notes() != null) candidate.setNotes(request.notes());
        if (request.stage() != null) candidate.setStage(request.stage());

        return toDto(candidateRepository.save(candidate));
    }

    @Transactional
    public void delete(UUID id) {
        if (!candidateRepository.existsById(id)) {
            throw new ResourceNotFoundException("Candidate not found");
        }
        candidateRepository.deleteById(id);
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private CandidateDto toDto(Candidate c) {
        return new CandidateDto(
                c.getId(),
                c.getName(),
                c.getRole(),
                c.getEmail(),
                c.getPhone(),
                c.getNotes(),
                c.getStage(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}
