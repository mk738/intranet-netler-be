package com.company.intranet.faq;

import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.employee.Employee;
import com.company.intranet.faq.dto.CreateFaqRequest;
import com.company.intranet.faq.dto.FaqItemDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FaqServiceTest {

    @Mock FaqRepository faqRepository;

    @InjectMocks FaqService faqService;

    private Employee admin() {
        return Employee.builder()
                .id(UUID.randomUUID()).email("admin@x.com").role(Employee.Role.ADMIN).build();
    }

    private Faq faq(UUID id, String question, int order) {
        return Faq.builder()
                .id(id).question(question).answer("Answer")
                .category("General").sortOrder(order).author(admin()).build();
    }

    // ── getAll ────────────────────────────────────────────────────────────────

    @Test
    void getAll_returnsItemsInOrder() {
        Faq a = faq(UUID.randomUUID(), "Q1", 0);
        Faq b = faq(UUID.randomUUID(), "Q2", 1);
        when(faqRepository.findAllByOrderBySortOrderAscCreatedAtAsc()).thenReturn(List.of(a, b));

        List<FaqItemDto> result = faqService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).question()).isEqualTo("Q1");
        assertThat(result.get(1).question()).isEqualTo("Q2");
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    void create_assignsNextSortOrder() {
        Employee author = admin();
        when(faqRepository.findMaxSortOrder()).thenReturn(3);

        Faq saved = faq(UUID.randomUUID(), "New Q", 4);
        when(faqRepository.save(any())).thenReturn(saved);

        faqService.create(new CreateFaqRequest("New Q", "Ans", null), author);

        ArgumentCaptor<Faq> captor = ArgumentCaptor.forClass(Faq.class);
        verify(faqRepository).save(captor.capture());
        assertThat(captor.getValue().getSortOrder()).isEqualTo(4);
    }

    @Test
    void create_firstItem_getsSortOrderZero() {
        Employee author = admin();
        when(faqRepository.findMaxSortOrder()).thenReturn(-1); // COALESCE default when empty

        Faq saved = faq(UUID.randomUUID(), "First Q", 0);
        when(faqRepository.save(any())).thenReturn(saved);

        faqService.create(new CreateFaqRequest("First Q", "Ans", null), author);

        ArgumentCaptor<Faq> captor = ArgumentCaptor.forClass(Faq.class);
        verify(faqRepository).save(captor.capture());
        assertThat(captor.getValue().getSortOrder()).isEqualTo(0);
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    void update_updatesFields() {
        UUID id  = UUID.randomUUID();
        Faq existing = faq(id, "Old Q", 1);
        when(faqRepository.findById(id)).thenReturn(Optional.of(existing));
        when(faqRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FaqItemDto result = faqService.update(id,
                new CreateFaqRequest("New Q", "New Ans", "HR"));

        assertThat(result.question()).isEqualTo("New Q");
        assertThat(result.answer()).isEqualTo("New Ans");
        assertThat(result.category()).isEqualTo("HR");
        assertThat(result.sortOrder()).isEqualTo(1); // sortOrder unchanged
    }

    @Test
    void update_notFound_throwsResourceNotFound() {
        UUID id = UUID.randomUUID();
        when(faqRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> faqService.update(id, new CreateFaqRequest("Q", "A", null)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    void delete_callsRepository() {
        UUID id = UUID.randomUUID();
        when(faqRepository.existsById(id)).thenReturn(true);

        faqService.delete(id);

        verify(faqRepository).deleteById(id);
    }

    @Test
    void delete_notFound_throwsResourceNotFound() {
        UUID id = UUID.randomUUID();
        when(faqRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> faqService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(faqRepository, never()).deleteById(any());
    }
}
