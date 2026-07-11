package com.jasper.documentmatcher.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DocumentCategoryServiceTest {

    @Mock private DocumentCategoryRepository documentCategoryRepository;

    private DocumentCategoryService service() {
        return new DocumentCategoryService(documentCategoryRepository);
    }

    @Test
    void findByIdReturnsMatchingCategory() {
        var category = new DocumentCategory(UUID.randomUUID(), "CONTRACT", "Verträge", CategoryOrigin.STANDARD, Instant.now());
        when(documentCategoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        var response = service().findById(category.getId());

        assertThat(response.displayName()).isEqualTo("Verträge");
    }

    @Test
    void findByIdThrowsForUnknownCategory() {
        var categoryId = UUID.randomUUID();
        when(documentCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().findById(categoryId)).isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void reusesAnExistingCategoryWithTheSameNormalizedDisplayName() {
        var existing = new DocumentCategory(
                UUID.randomUUID(), "TERMINATION", "Kündigungen", CategoryOrigin.LLM_SUGGESTED, Instant.now());
        when(documentCategoryRepository.findAll()).thenReturn(List.of(existing));

        var resolvedId = service().resolveOrCreateByDisplayName("  kündigungen ", CategoryOrigin.MANUAL);

        assertThat(resolvedId).isEqualTo(existing.getId());
        verify(documentCategoryRepository, never()).save(any());
    }

    @Test
    void createsANewCategoryWithATransliteratedUniqueCodeWhenNoMatchExists() {
        when(documentCategoryRepository.findAll()).thenReturn(List.of());
        when(documentCategoryRepository.findByCode(any())).thenReturn(Optional.empty());
        when(documentCategoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var resolvedId = service().resolveOrCreateByDisplayName("Kündigungen", CategoryOrigin.LLM_SUGGESTED);

        assertThat(resolvedId).isNotNull();
        verify(documentCategoryRepository).findByCode("KUENDIGUNGEN");
    }

    @Test
    void appendsASuffixWhenTheGeneratedCodeAlreadyExists() {
        when(documentCategoryRepository.findAll()).thenReturn(List.of());
        when(documentCategoryRepository.findByCode("SONSTIGES"))
                .thenReturn(Optional.of(new DocumentCategory(
                        UUID.randomUUID(), "SONSTIGES", "Sonstiges", CategoryOrigin.STANDARD, Instant.now())));
        when(documentCategoryRepository.findByCode("SONSTIGES_2")).thenReturn(Optional.empty());
        when(documentCategoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service().resolveOrCreateByDisplayName("Sonstiges", CategoryOrigin.MANUAL);

        verify(documentCategoryRepository).findByCode(eq("SONSTIGES_2"));
    }
}
