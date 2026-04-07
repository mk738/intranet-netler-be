package com.company.intranet.category;

import com.company.intranet.category.dto.CategoryDto;
import com.company.intranet.category.dto.CreateCategoryRequest;
import com.company.intranet.category.dto.UpdateCategoryRequest;
import com.company.intranet.common.exception.AppException;
import com.company.intranet.common.exception.ErrorCode;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeRepository;
import com.company.intranet.security.RolePermissions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired MockMvc      mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean CategoryService               categoryService;
    @MockBean FirebaseAuth                  firebaseAuth;
    @MockBean EmployeeRepository            employeeRepository;
    @MockBean JpaMetamodelMappingContext    jpaMetamodelMappingContext;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Employee admin() {
        return Employee.builder()
                .id(UUID.randomUUID()).email("admin@x.com").role(Employee.Role.ADMIN).build();
    }

    private Employee employee() {
        return Employee.builder()
                .id(UUID.randomUUID()).email("emp@x.com").role(Employee.Role.EMPLOYEE).build();
    }

    private Authentication auth(Employee emp) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + emp.getRole().name()));
        RolePermissions.of(emp.getRole())
                .forEach(p -> authorities.add(new SimpleGrantedAuthority(p.name())));
        return new UsernamePasswordAuthenticationToken(emp, null, authorities);
    }

    private CategoryDto sampleDto(UUID id) {
        return new CategoryDto(id, "HR", "NEWS", "2026-01-01T00:00:00Z");
    }

    // ── GET /api/categories?type=NEWS ─────────────────────────────────────────

    @Test
    void getByType_asEmployee_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(categoryService.getByType(Category.CategoryType.NEWS))
                .thenReturn(List.of(sampleDto(id)));

        mockMvc.perform(get("/api/categories?type=NEWS")
                        .with(authentication(auth(employee()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("HR"))
                .andExpect(jsonPath("$.data[0].type").value("NEWS"));
    }

    @Test
    void getByType_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/categories?type=NEWS"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getByType_emptyList_returns200WithEmptyArray() throws Exception {
        when(categoryService.getByType(Category.CategoryType.FAQ)).thenReturn(List.of());

        mockMvc.perform(get("/api/categories?type=FAQ")
                        .with(authentication(auth(employee()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // ── POST /api/categories ──────────────────────────────────────────────────

    @Test
    void create_asAdmin_returns201() throws Exception {
        UUID id = UUID.randomUUID();
        CreateCategoryRequest req = new CreateCategoryRequest("Ekonomi", Category.CategoryType.NEWS);

        when(categoryService.create(any(CreateCategoryRequest.class))).thenReturn(sampleDto(id));

        mockMvc.perform(post("/api/categories")
                        .with(authentication(auth(admin())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").isNotEmpty());
    }

    @Test
    void create_asEmployee_returns403() throws Exception {
        CreateCategoryRequest req = new CreateCategoryRequest("HR", Category.CategoryType.NEWS);

        mockMvc.perform(post("/api/categories")
                        .with(authentication(auth(employee())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_blankName_returns422() throws Exception {
        CreateCategoryRequest req = new CreateCategoryRequest("", Category.CategoryType.NEWS);

        mockMvc.perform(post("/api/categories")
                        .with(authentication(auth(admin())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void create_duplicateName_returns409() throws Exception {
        CreateCategoryRequest req = new CreateCategoryRequest("HR", Category.CategoryType.NEWS);
        when(categoryService.create(any())).thenThrow(
                new AppException(ErrorCode.CATEGORY_NAME_TAKEN,
                        "A category with that name already exists for type NEWS",
                        HttpStatus.CONFLICT));

        mockMvc.perform(post("/api/categories")
                        .with(authentication(auth(admin())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    // ── PUT /api/categories/{id} ──────────────────────────────────────────────

    @Test
    void update_asAdmin_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateCategoryRequest req = new UpdateCategoryRequest("Nytt namn");

        when(categoryService.update(eq(id), any(UpdateCategoryRequest.class))).thenReturn(sampleDto(id));

        mockMvc.perform(put("/api/categories/" + id)
                        .with(authentication(auth(admin())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void update_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateCategoryRequest req = new UpdateCategoryRequest("Nytt namn");

        when(categoryService.update(any(), any())).thenThrow(
                new AppException(ErrorCode.CATEGORY_NOT_FOUND, "Category not found", HttpStatus.NOT_FOUND));

        mockMvc.perform(put("/api/categories/" + id)
                        .with(authentication(auth(admin())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found"));
    }

    @Test
    void update_asEmployee_returns403() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateCategoryRequest req = new UpdateCategoryRequest("X");

        mockMvc.perform(put("/api/categories/" + id)
                        .with(authentication(auth(employee())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    // ── DELETE /api/categories/{id} ───────────────────────────────────────────

    @Test
    void delete_asAdmin_returns204() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(categoryService).delete(id);

        mockMvc.perform(delete("/api/categories/" + id)
                        .with(authentication(auth(admin())))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new AppException(ErrorCode.CATEGORY_NOT_FOUND, "Category not found", HttpStatus.NOT_FOUND))
                .when(categoryService).delete(any());

        mockMvc.perform(delete("/api/categories/" + id)
                        .with(authentication(auth(admin())))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found"));
    }

    @Test
    void delete_asEmployee_returns403() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/categories/" + id)
                        .with(authentication(auth(employee())))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
