package com.company.intranet.faq;

import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.employee.Employee;
import com.company.intranet.faq.dto.CreateFaqRequest;
import com.company.intranet.faq.dto.FaqItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    @Transactional(readOnly = true)
    public List<FaqItemDto> getAll() {
        return faqRepository.findAllByOrderBySortOrderAscCreatedAtAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public FaqItemDto create(CreateFaqRequest request, Employee author) {
        int nextOrder = faqRepository.findMaxSortOrder() + 1;

        Faq faq = Faq.builder()
                .question(request.question())
                .answer(request.answer())
                .category(request.category())
                .sortOrder(nextOrder)
                .author(author)
                .build();

        return toDto(faqRepository.save(faq));
    }

    @Transactional
    public FaqItemDto update(UUID id, CreateFaqRequest request) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ item not found"));

        faq.setQuestion(request.question());
        faq.setAnswer(request.answer());
        faq.setCategory(request.category());

        return toDto(faqRepository.save(faq));
    }

    @Transactional
    public void delete(UUID id) {
        if (!faqRepository.existsById(id)) {
            throw new ResourceNotFoundException("FAQ item not found");
        }
        faqRepository.deleteById(id);
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private FaqItemDto toDto(Faq faq) {
        return new FaqItemDto(
                faq.getId(),
                faq.getQuestion(),
                faq.getAnswer(),
                faq.getCategory(),
                faq.getSortOrder(),
                faq.getCreatedAt() != null ? faq.getCreatedAt().toString() : null
        );
    }
}
