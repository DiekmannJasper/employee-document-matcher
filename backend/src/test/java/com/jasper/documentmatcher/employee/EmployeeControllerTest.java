package com.jasper.documentmatcher.employee;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jasper.documentmatcher.common.GlobalExceptionHandler;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeController.class)
@Import(GlobalExceptionHandler.class)
class EmployeeControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private EmployeeService employeeService;

    @Test
    void listReturnsAllEmployees() throws Exception {
        var response = new EmployeeResponse(
                UUID.randomUUID(), "EMP-1001", "Anna", "Müller", "Produktentwicklung");
        when(employeeService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].personnelNumber").value("EMP-1001"));
    }

    @Test
    void detailReturnsMatchingEmployee() throws Exception {
        var id = UUID.randomUUID();
        var response = new EmployeeResponse(id, "EMP-1001", "Anna", "Müller", "Produktentwicklung");
        when(employeeService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Anna"));
    }

    @Test
    void detailReturnsNotFoundForUnknownId() throws Exception {
        var id = UUID.randomUUID();
        when(employeeService.findById(id)).thenThrow(new EmployeeNotFoundException(id));

        mockMvc.perform(get("/api/employees/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void detailReturnsBadRequestForInvalidId() throws Exception {
        mockMvc.perform(get("/api/employees/{id}", "not-a-uuid")).andExpect(status().isBadRequest());
    }
}
