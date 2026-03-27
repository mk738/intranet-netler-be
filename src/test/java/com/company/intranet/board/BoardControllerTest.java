package com.company.intranet.board;

import com.company.intranet.board.dto.*;
import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import com.company.intranet.security.RolePermissions;
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

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired MockMvc      mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean BoardService               boardService;
    @MockBean FirebaseAuth               firebaseAuth;
    @MockBean EmployeeRepository         employeeRepository;
    @MockBean JpaMetamodelMappingContext jpaMetamodelMappingContext;

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

    private BoardDto sampleBoard(UUID id) {
        return new BoardDto(id, "Sprint 1", "admin@x.com", "2026-01-01T00:00:00Z", List.of());
    }

    private BoardColumnDto sampleColumn(UUID id) {
        return new BoardColumnDto(id, "To Do", 0, 0, List.of());
    }

    private BoardCardDto sampleCard(UUID id) {
        return new BoardCardDto(id, "Fix bug", "Details", "Backend", null, 0,
                "2026-01-01T00:00:00Z", List.of());
    }

    private BoardCommentDto sampleComment(UUID id) {
        return new BoardCommentDto(id, "LGTM", "Admin User", "2026-01-01T00:00:00Z");
    }

    // ── GET /api/boards ───────────────────────────────────────────────────────

    @Test
    void getAll_asAdmin_returns200WithNestedStructure() throws Exception {
        UUID id = UUID.randomUUID();
        when(boardService.getAllBoards()).thenReturn(List.of(sampleBoard(id)));

        mockMvc.perform(get("/api/boards").with(authentication(auth(admin()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Sprint 1"))
                .andExpect(jsonPath("$.data[0].columns").isArray());
    }

    @Test
    void getAll_asEmployee_returns403() throws Exception {
        mockMvc.perform(get("/api/boards").with(authentication(auth(employee()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAll_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/boards"))
                .andExpect(status().isUnauthorized());
    }

    // ── POST /api/boards ──────────────────────────────────────────────────────

    @Test
    void createBoard_asAdmin_returns201() throws Exception {
        UUID id = UUID.randomUUID();
        when(boardService.createBoard(any(CreateBoardRequest.class))).thenReturn(sampleBoard(id));

        mockMvc.perform(post("/api/boards")
                        .with(authentication(auth(admin())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sprint 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Sprint 1"));
    }

    @Test
    void createBoard_blankName_returns422() throws Exception {
        mockMvc.perform(post("/api/boards")
                        .with(authentication(auth(admin())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isUnprocessableEntity());
    }

    // ── PUT /api/boards/{id} ──────────────────────────────────────────────────

    @Test
    void updateBoard_asAdmin_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        when(boardService.updateBoard(eq(id), any(UpdateBoardRequest.class)))
                .thenReturn(new BoardDto(id, "Renamed", "admin@x.com", null, List.of()));

        mockMvc.perform(put("/api/boards/" + id)
                        .with(authentication(auth(admin())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Renamed\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Renamed"));
    }

    @Test
    void updateBoard_notFound_returns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(boardService.updateBoard(any(), any()))
                .thenThrow(new ResourceNotFoundException("Board not found"));

        mockMvc.perform(put("/api/boards/" + id)
                        .with(authentication(auth(admin())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\"}"))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/boards/{id} ───────────────────────────────────────────────

    @Test
    void deleteBoard_asAdmin_returns204() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(boardService).deleteBoard(id);

        mockMvc.perform(delete("/api/boards/" + id)
                        .with(authentication(auth(admin())))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    // ── POST /api/boards/{boardId}/columns ────────────────────────────────────

    @Test
    void createColumn_asAdmin_returns201() throws Exception {
        UUID boardId = UUID.randomUUID();
        UUID colId   = UUID.randomUUID();
        when(boardService.createColumn(eq(boardId), any(CreateColumnRequest.class)))
                .thenReturn(sampleColumn(colId));

        String body = "{\"title\":\"To Do\",\"colorIndex\":0,\"position\":0}";

        mockMvc.perform(post("/api/boards/" + boardId + "/columns")
                        .with(authentication(auth(admin())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("To Do"));
    }

    @Test
    void createColumn_blankTitle_returns422() throws Exception {
        UUID boardId = UUID.randomUUID();

        mockMvc.perform(post("/api/boards/" + boardId + "/columns")
                        .with(authentication(auth(admin())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"colorIndex\":0,\"position\":0}"))
                .andExpect(status().isUnprocessableEntity());
    }

    // ── PUT /api/boards/{boardId}/columns/{id} ────────────────────────────────

    @Test
    void updateColumn_asAdmin_returns200() throws Exception {
        UUID boardId = UUID.randomUUID();
        UUID colId   = UUID.randomUUID();
        when(boardService.updateColumn(eq(boardId), eq(colId), any(UpdateColumnRequest.class)))
                .thenReturn(new BoardColumnDto(colId, "Done", 1, 1, List.of()));

        mockMvc.perform(put("/api/boards/" + boardId + "/columns/" + colId)
                        .with(authentication(auth(admin())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Done\",\"colorIndex\":1,\"position\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Done"));
    }

    // ── DELETE /api/boards/{boardId}/columns/{id} ─────────────────────────────

    @Test
    void deleteColumn_asAdmin_returns204() throws Exception {
        UUID boardId = UUID.randomUUID();
        UUID colId   = UUID.randomUUID();
        doNothing().when(boardService).deleteColumn(boardId, colId);

        mockMvc.perform(delete("/api/boards/" + boardId + "/columns/" + colId)
                        .with(authentication(auth(admin())))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    // ── POST /api/columns/{columnId}/cards ────────────────────────────────────

    @Test
    void createCard_asAdmin_returns201() throws Exception {
        UUID colId  = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        when(boardService.createCard(eq(colId), any(CreateCardRequest.class)))
                .thenReturn(sampleCard(cardId));

        String body = "{\"title\":\"Fix bug\",\"text\":\"Details\",\"category\":\"Backend\",\"position\":0}";

        mockMvc.perform(post("/api/columns/" + colId + "/cards")
                        .with(authentication(auth(admin())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Fix bug"))
                .andExpect(jsonPath("$.data.category").value("Backend"));
    }

    // ── PUT /api/columns/{columnId}/cards/{id} ────────────────────────────────

    @Test
    void updateCard_asAdmin_returns200() throws Exception {
        UUID colId  = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        when(boardService.updateCard(eq(colId), eq(cardId), any(UpdateCardRequest.class)))
                .thenReturn(sampleCard(cardId));

        String body = objectMapper.writeValueAsString(
                new UpdateCardRequest("Fix bug", "Details", "Backend", null, 0, colId));

        mockMvc.perform(put("/api/columns/" + colId + "/cards/" + cardId)
                        .with(authentication(auth(admin())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    // ── DELETE /api/columns/{columnId}/cards/{id} ─────────────────────────────

    @Test
    void deleteCard_asAdmin_returns204() throws Exception {
        UUID colId  = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        doNothing().when(boardService).deleteCard(colId, cardId);

        mockMvc.perform(delete("/api/columns/" + colId + "/cards/" + cardId)
                        .with(authentication(auth(admin())))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    // ── POST /api/cards/{cardId}/comments ─────────────────────────────────────

    @Test
    void createComment_asAdmin_returns201() throws Exception {
        UUID cardId    = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        when(boardService.createComment(eq(cardId), any(CreateCommentRequest.class), any(Employee.class)))
                .thenReturn(sampleComment(commentId));

        mockMvc.perform(post("/api/cards/" + cardId + "/comments")
                        .with(authentication(auth(admin())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"LGTM\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.text").value("LGTM"))
                .andExpect(jsonPath("$.data.authorName").value("Admin User"));
    }

    @Test
    void createComment_blankText_returns422() throws Exception {
        UUID cardId = UUID.randomUUID();

        mockMvc.perform(post("/api/cards/" + cardId + "/comments")
                        .with(authentication(auth(admin())))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"\"}"))
                .andExpect(status().isUnprocessableEntity());
    }

    // ── DELETE /api/cards/{cardId}/comments/{id} ──────────────────────────────

    @Test
    void deleteComment_asAdmin_returns204() throws Exception {
        UUID cardId    = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        doNothing().when(boardService).deleteComment(cardId, commentId);

        mockMvc.perform(delete("/api/cards/" + cardId + "/comments/" + commentId)
                        .with(authentication(auth(admin())))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
