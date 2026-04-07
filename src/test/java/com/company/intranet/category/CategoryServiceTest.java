package com.company.intranet.category;

import com.company.intranet.category.dto.CategoryDto;
import com.company.intranet.category.dto.CreateCategoryRequest;
import com.company.intranet.category.dto.UpdateCategoryRequest;
import com.company.intranet.common.exception.AppException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock CategoryRepository categoryRepository;

    @InjectMocks CategoryService categoryService;

    private Category category(UUID id, String name, Category.CategoryType type) {
        return Category.builder().id(id).name(name).type(type).build();
    }

    // ── getByType ─────────────────────────────────────────────────────────────

    @Test
    void getByType_returnsAlphabeticallySortedList() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        when(categoryRepository.findByTypeOrderByNameAsc(Category.CategoryType.NEWS))
                .thenReturn(List.of(category(a, "HR", Category.CategoryType.NEWS),
                                    category(b, "IT", Category.CategoryType.NEWS)));

        List<CategoryDto> result = categoryService.getByType(Category.CategoryType.NEWS);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("HR");
        assertThat(result.get(1).name()).isEqualTo("IT");
        assertThat(result.get(0).type()).isEqualTo("NEWS");
    }

    @Test
    void getByType_emptyList_returnsEmpty() {
        when(categoryRepository.findByTypeOrderByNameAsc(Category.CategoryType.FAQ))
                .thenReturn(List.of());

        assertThat(categoryService.getByType(Category.CategoryType.FAQ)).isEmpty();
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    void create_savesAndReturnsDto() {
        CreateCategoryRequest req = new CreateCategoryRequest("Ekonomi", Category.CategoryType.NEWS);
        UUID id = UUID.randomUUID();
        Category saved = category(id, "Ekonomi", Category.CategoryType.NEWS);

        when(categoryRepository.existsByNameIgnoreCaseAndType("Ekonomi", Category.CategoryType.NEWS))
                .thenReturn(false);
        when(categoryRepository.save(any())).thenReturn(saved);

        CategoryDto result = categoryService.create(req);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.name()).isEqualTo("Ekonomi");
        assertThat(result.type()).isEqualTo("NEWS");
    }

    @Test
    void create_trimsWhitespaceFromName() {
        CreateCategoryRequest req = new CreateCategoryRequest("  HR  ", Category.CategoryType.FAQ);
        Category saved = category(UUID.randomUUID(), "HR", Category.CategoryType.FAQ);

        when(categoryRepository.existsByNameIgnoreCaseAndType("HR", Category.CategoryType.FAQ))
                .thenReturn(false);
        when(categoryRepository.save(any())).thenReturn(saved);

        categoryService.create(req);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("HR");
    }

    @Test
    void create_duplicateName_throwsConflict() {
        CreateCategoryRequest req = new CreateCategoryRequest("HR", Category.CategoryType.NEWS);
        when(categoryRepository.existsByNameIgnoreCaseAndType("HR", Category.CategoryType.NEWS))
                .thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(req))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void create_duplicateNameCaseInsensitive_throwsConflict() {
        CreateCategoryRequest req = new CreateCategoryRequest("hr", Category.CategoryType.NEWS);
        when(categoryRepository.existsByNameIgnoreCaseAndType("hr", Category.CategoryType.NEWS))
                .thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(req))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    void update_renamesCategory() {
        UUID id = UUID.randomUUID();
        Category existing = category(id, "Old Name", Category.CategoryType.NEWS);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByNameIgnoreCaseAndTypeAndIdNot("New Name", Category.CategoryType.NEWS, id))
                .thenReturn(false);
        when(categoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CategoryDto result = categoryService.update(id, new UpdateCategoryRequest("New Name"));

        assertThat(result.name()).isEqualTo("New Name");
    }

    @Test
    void update_trimsWhitespace() {
        UUID id = UUID.randomUUID();
        Category existing = category(id, "Old", Category.CategoryType.FAQ);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByNameIgnoreCaseAndTypeAndIdNot("New", Category.CategoryType.FAQ, id))
                .thenReturn(false);
        when(categoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        categoryService.update(id, new UpdateCategoryRequest("  New  "));

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("New");
    }

    @Test
    void update_notFound_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(id, new UpdateCategoryRequest("X")))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void update_nameClashWithOther_throwsConflict() {
        UUID id = UUID.randomUUID();
        Category existing = category(id, "HR", Category.CategoryType.NEWS);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByNameIgnoreCaseAndTypeAndIdNot("IT", Category.CategoryType.NEWS, id))
                .thenReturn(true);

        assertThatThrownBy(() -> categoryService.update(id, new UpdateCategoryRequest("IT")))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    void delete_callsRepository() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.existsById(id)).thenReturn(true);

        categoryService.delete(id);

        verify(categoryRepository).deleteById(id);
    }

    @Test
    void delete_notFound_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.delete(id))
                .isInstanceOf(AppException.class)
                .satisfies(ex -> assertThat(((AppException) ex).getStatus()).isEqualTo(HttpStatus.NOT_FOUND));

        verify(categoryRepository, never()).deleteById(any());
    }
}
