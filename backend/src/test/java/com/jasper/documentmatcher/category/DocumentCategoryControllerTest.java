package com.jasper.documentmatcher.category;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DocumentCategoryController.class)
class DocumentCategoryControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private DocumentCategoryService documentCategoryService;

    @Test
    void listReturnsAllCategories() throws Exception {
        var response = new DocumentCategoryResponse(UUID.randomUUID(), "CONTRACT", "Verträge", CategoryOrigin.STANDARD);
        when(documentCategoryService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/document-categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code").value("CONTRACT"))
                .andExpect(jsonPath("$[0].origin").value("STANDARD"));
    }
}
