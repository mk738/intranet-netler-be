package com.company.intranet.candidate;

import com.company.intranet.candidate.dto.CandidateDto;
import com.company.intranet.candidate.dto.CreateCandidateRequest;
import com.company.intranet.candidate.dto.PatchCandidateRequest;
import com.company.intranet.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock CandidateRepository candidateRepository;

    @InjectMocks CandidateService candidateService;

    private Candidate candidate(UUID id, String name, int stage) {
        Candidate c = Candidate.builder()
                .id(id).name(name).role("Developer")
                .email("anna@example.com").phone("070-000 00 00")
                .notes("Some notes.").stage(stage)
                .build();
        return c;
    }

    // ── getAll ────────────────────────────────────────────────────────────────

    @Test
    void getAll_returnsCandidatesOrderedByCreatedAtDesc() {
        Candidate a = candidate(UUID.randomUUID(), "Anna", 0);
        Candidate b = candidate(UUID.randomUUID(), "Björn", 2);
        when(candidateRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(a, b));

        List<CandidateDto> result = candidateService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Anna");
        assertThat(result.get(1).name()).isEqualTo("Björn");
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    void create_savesAndReturnsDto() {
        CreateCandidateRequest req = new CreateCandidateRequest(
                "Anna Andersson", "Fullstack-utvecklare",
                "anna@example.com", "070-123 45 67", "Stark React-bakgrund.", 0);

        UUID id = UUID.randomUUID();
        Candidate saved = candidate(id, "Anna Andersson", 0);
        when(candidateRepository.save(any())).thenReturn(saved);

        CandidateDto result = candidateService.create(req);

        assertThat(result.name()).isEqualTo("Anna Andersson");
        assertThat(result.stage()).isEqualTo(0);
        verify(candidateRepository).save(any());
    }

    @Test
    void create_persistsAllFields() {
        CreateCandidateRequest req = new CreateCandidateRequest(
                "Anna", "Backend Dev", "a@b.com", "070", "Notes", 3);

        when(candidateRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        candidateService.create(req);

        ArgumentCaptor<Candidate> captor = ArgumentCaptor.forClass(Candidate.class);
        verify(candidateRepository).save(captor.capture());
        Candidate c = captor.getValue();
        assertThat(c.getName()).isEqualTo("Anna");
        assertThat(c.getRole()).isEqualTo("Backend Dev");
        assertThat(c.getEmail()).isEqualTo("a@b.com");
        assertThat(c.getPhone()).isEqualTo("070");
        assertThat(c.getNotes()).isEqualTo("Notes");
        assertThat(c.getStage()).isEqualTo(3);
    }

    // ── patch ─────────────────────────────────────────────────────────────────

    @Test
    void patch_updatesOnlyProvidedFields() {
        UUID id = UUID.randomUUID();
        Candidate existing = candidate(id, "Anna", 1);
        when(candidateRepository.findById(id)).thenReturn(Optional.of(existing));
        when(candidateRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PatchCandidateRequest req = new PatchCandidateRequest(null, null, null, null, null, 3);
        CandidateDto result = candidateService.patch(id, req);

        assertThat(result.stage()).isEqualTo(3);
        assertThat(result.name()).isEqualTo("Anna"); // unchanged
    }

    @Test
    void patch_updatesName() {
        UUID id = UUID.randomUUID();
        Candidate existing = candidate(id, "Anna", 0);
        when(candidateRepository.findById(id)).thenReturn(Optional.of(existing));
        when(candidateRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PatchCandidateRequest req = new PatchCandidateRequest("Björn", null, null, null, null, null);
        CandidateDto result = candidateService.patch(id, req);

        assertThat(result.name()).isEqualTo("Björn");
        assertThat(result.stage()).isEqualTo(0); // unchanged
    }

    @Test
    void patch_notFound_throwsResourceNotFound() {
        UUID id = UUID.randomUUID();
        when(candidateRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> candidateService.patch(id,
                new PatchCandidateRequest(null, null, null, null, null, null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    void delete_callsRepository() {
        UUID id = UUID.randomUUID();
        when(candidateRepository.existsById(id)).thenReturn(true);

        candidateService.delete(id);

        verify(candidateRepository).deleteById(id);
    }

    @Test
    void delete_notFound_throwsResourceNotFound() {
        UUID id = UUID.randomUUID();
        when(candidateRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> candidateService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(candidateRepository, never()).deleteById(any());
    }
}
