package com.jasper.documentmatcher.category;

import static org.assertj.core.api.Assertions.assertThat;

import com.jasper.documentmatcher.AbstractPostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class DocumentCategoryRepositoryTest extends AbstractPostgresIntegrationTest {

    @Autowired private DocumentCategoryRepository documentCategoryRepository;

    @Test
    void loadsFiveStandardCategoriesFromMigration() {
        assertThat(documentCategoryRepository.count()).isEqualTo(5);
    }

    @Test
    void findsCategoryByStableCode() {
        var category = documentCategoryRepository.findByCode("CONTRACT");

        assertThat(category)
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getDisplayName()).isEqualTo("Verträge");
                    assertThat(found.getOrigin()).isEqualTo(CategoryOrigin.STANDARD);
                });
    }
}
