package com.company.intranet.board;

import com.company.intranet.board.dto.*;
import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.employee.Employee;
import com.company.intranet.employee.EmployeeProfile;
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
class BoardServiceTest {

    @Mock BoardRepository            boardRepository;
    @Mock BoardColumnRepository      columnRepository;
    @Mock BoardCardRepository        cardRepository;
    @Mock BoardCommentRepository     commentRepository;
    @Mock CardAttachmentRepository   attachmentRepository;
    @Mock com.company.intranet.config.FirebaseStorageService storageService;

    @InjectMocks BoardService boardService;

    // ── helpers ───────────────────────────────────────────────────────────────

    private Board board(UUID id, String name) {
        Board b = Board.builder().id(id).name(name).build();
        return b;
    }

    private BoardColumn column(UUID id, Board board, String title, int position) {
        return BoardColumn.builder()
                .id(id).board(board).title(title).colorIndex(0).position(position).build();
    }

    private BoardCard card(UUID id, BoardColumn column, String title, int position) {
        return BoardCard.builder()
                .id(id).boardColumn(column).title(title).position(position).build();
    }

    private Employee admin() {
        EmployeeProfile profile = EmployeeProfile.builder()
                .firstName("Admin").lastName("User").build();
        Employee emp = Employee.builder()
                .id(UUID.randomUUID()).email("admin@x.com").role(Employee.Role.ADMIN).build();
        emp.setProfile(profile);
        profile.setEmployee(emp);
        return emp;
    }

    // ── getAllBoards ───────────────────────────────────────────────────────────

    @Test
    void getAllBoards_returnsNestedStructure() {
        UUID boardId = UUID.randomUUID();
        Board b = board(boardId, "Sprint 1");
        when(boardRepository.findAllByOrderByCreatedAtAsc()).thenReturn(List.of(b));

        List<BoardDto> result = boardService.getAllBoards();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Sprint 1");
        assertThat(result.get(0).columns()).isEmpty();
    }

    // ── createBoard ───────────────────────────────────────────────────────────

    @Test
    void createBoard_savesAndReturnsDto() {
        UUID id = UUID.randomUUID();
        when(boardRepository.save(any())).thenAnswer(inv -> {
            Board b = inv.getArgument(0);
            Board saved = Board.builder().id(id).name(b.getName()).build();
            return saved;
        });

        BoardDto result = boardService.createBoard(new CreateBoardRequest("Sprint 1"));

        assertThat(result.name()).isEqualTo("Sprint 1");
        assertThat(result.id()).isEqualTo(id);
    }

    @Test
    void createBoard_persistsName() {
        when(boardRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        boardService.createBoard(new CreateBoardRequest("My Board"));

        ArgumentCaptor<Board> captor = ArgumentCaptor.forClass(Board.class);
        verify(boardRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("My Board");
    }

    // ── updateBoard ───────────────────────────────────────────────────────────

    @Test
    void updateBoard_updatesName() {
        UUID id = UUID.randomUUID();
        Board existing = board(id, "Old Name");
        when(boardRepository.findById(id)).thenReturn(Optional.of(existing));
        when(boardRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BoardDto result = boardService.updateBoard(id, new UpdateBoardRequest("New Name"));

        assertThat(result.name()).isEqualTo("New Name");
    }

    @Test
    void updateBoard_notFound_throwsResourceNotFound() {
        UUID id = UUID.randomUUID();
        when(boardRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.updateBoard(id, new UpdateBoardRequest("X")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── deleteBoard ───────────────────────────────────────────────────────────

    @Test
    void deleteBoard_callsRepository() {
        UUID id = UUID.randomUUID();
        when(boardRepository.existsById(id)).thenReturn(true);

        boardService.deleteBoard(id);

        verify(boardRepository).deleteById(id);
    }

    @Test
    void deleteBoard_notFound_throwsResourceNotFound() {
        UUID id = UUID.randomUUID();
        when(boardRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> boardService.deleteBoard(id))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(boardRepository, never()).deleteById(any());
    }

    // ── createColumn ──────────────────────────────────────────────────────────

    @Test
    void createColumn_boardNotFound_throwsResourceNotFound() {
        UUID boardId = UUID.randomUUID();
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.createColumn(boardId,
                new CreateColumnRequest("To Do", 0, 0)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createColumn_savesAndReturnsDto() {
        UUID boardId = UUID.randomUUID();
        Board b = board(boardId, "Sprint 1");
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(b));

        UUID colId = UUID.randomUUID();
        when(columnRepository.save(any())).thenAnswer(inv -> {
            BoardColumn col = inv.getArgument(0);
            return BoardColumn.builder()
                    .id(colId).board(col.getBoard())
                    .title(col.getTitle()).colorIndex(col.getColorIndex())
                    .position(col.getPosition()).build();
        });

        BoardColumnDto result = boardService.createColumn(boardId,
                new CreateColumnRequest("To Do", 2, 0));

        assertThat(result.title()).isEqualTo("To Do");
        assertThat(result.colorIndex()).isEqualTo(2);
        assertThat(result.id()).isEqualTo(colId);
    }

    // ── updateColumn ──────────────────────────────────────────────────────────

    @Test
    void updateColumn_wrongBoard_throwsResourceNotFound() {
        UUID boardId = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();
        UUID colId   = UUID.randomUUID();
        Board other = board(otherId, "Other");
        BoardColumn col = column(colId, other, "To Do", 0);
        when(columnRepository.findById(colId)).thenReturn(Optional.of(col));

        assertThatThrownBy(() -> boardService.updateColumn(boardId, colId,
                new UpdateColumnRequest("Done", 1, 1)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateColumn_updatesFields() {
        UUID boardId = UUID.randomUUID();
        UUID colId   = UUID.randomUUID();
        Board b   = board(boardId, "Sprint");
        BoardColumn col = column(colId, b, "To Do", 0);
        when(columnRepository.findById(colId)).thenReturn(Optional.of(col));
        when(columnRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BoardColumnDto result = boardService.updateColumn(boardId, colId,
                new UpdateColumnRequest("Done", 3, 2));

        assertThat(result.title()).isEqualTo("Done");
        assertThat(result.colorIndex()).isEqualTo(3);
        assertThat(result.position()).isEqualTo(2);
    }

    // ── createCard ────────────────────────────────────────────────────────────

    @Test
    void createCard_columnNotFound_throwsResourceNotFound() {
        UUID colId = UUID.randomUUID();
        when(columnRepository.findById(colId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.createCard(colId,
                new CreateCardRequest("Task", null, null, null, 0)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createCard_savesAndReturnsDto() {
        UUID boardId = UUID.randomUUID();
        UUID colId   = UUID.randomUUID();
        Board b   = board(boardId, "Sprint");
        BoardColumn col = column(colId, b, "To Do", 0);
        when(columnRepository.findById(colId)).thenReturn(Optional.of(col));

        UUID cardId = UUID.randomUUID();
        when(cardRepository.save(any())).thenAnswer(inv -> {
            BoardCard c = inv.getArgument(0);
            return BoardCard.builder()
                    .id(cardId).boardColumn(c.getBoardColumn())
                    .title(c.getTitle()).text(c.getText())
                    .category(c.getCategory()).position(c.getPosition()).build();
        });

        BoardCardDto result = boardService.createCard(colId,
                new CreateCardRequest("Fix bug", "Details", "Backend", null, 0));

        assertThat(result.title()).isEqualTo("Fix bug");
        assertThat(result.category()).isEqualTo("Backend");
        assertThat(result.id()).isEqualTo(cardId);
    }

    // ── updateCard – move between columns ────────────────────────────────────

    @Test
    void updateCard_movesToDifferentColumn() {
        UUID boardId    = UUID.randomUUID();
        UUID colId      = UUID.randomUUID();
        UUID targetColId = UUID.randomUUID();
        UUID cardId     = UUID.randomUUID();

        Board b            = board(boardId, "Sprint");
        BoardColumn col    = column(colId, b, "To Do", 0);
        BoardColumn target = column(targetColId, b, "Done", 1);
        BoardCard c        = card(cardId, col, "Task", 0);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(c));
        when(columnRepository.findById(targetColId)).thenReturn(Optional.of(target));
        when(cardRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        boardService.updateCard(colId, cardId,
                new UpdateCardRequest("Task", null, null, null, 1, targetColId));

        assertThat(c.getBoardColumn().getId()).isEqualTo(targetColId);
    }

    // ── createComment ─────────────────────────────────────────────────────────

    @Test
    void createComment_persistsAuthorName() {
        UUID cardId = UUID.randomUUID();
        UUID colId  = UUID.randomUUID();
        Board b  = board(UUID.randomUUID(), "Sprint");
        BoardColumn col = column(colId, b, "To Do", 0);
        BoardCard c = card(cardId, col, "Task", 0);
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(c));

        when(commentRepository.save(any())).thenAnswer(inv -> {
            BoardComment cm = inv.getArgument(0);
            return BoardComment.builder()
                    .id(UUID.randomUUID()).card(cm.getCard())
                    .text(cm.getText()).authorName(cm.getAuthorName()).build();
        });

        Employee emp = admin();
        BoardCommentDto result = boardService.createComment(cardId,
                new CreateCommentRequest("LGTM"), emp);

        assertThat(result.text()).isEqualTo("LGTM");
        assertThat(result.authorName()).isEqualTo("Admin User");
    }

    @Test
    void createComment_cardNotFound_throwsResourceNotFound() {
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardService.createComment(cardId,
                new CreateCommentRequest("Hi"), admin()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── deleteComment ─────────────────────────────────────────────────────────

    @Test
    void deleteComment_wrongCard_throwsResourceNotFound() {
        UUID cardId    = UUID.randomUUID();
        UUID otherCard = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();

        Board b  = board(UUID.randomUUID(), "Sprint");
        BoardColumn col = column(UUID.randomUUID(), b, "To Do", 0);
        BoardCard other = card(otherCard, col, "Other", 0);
        BoardComment cm = BoardComment.builder()
                .id(commentId).card(other).text("Hi").authorName("Test").build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(cm));

        assertThatThrownBy(() -> boardService.deleteComment(cardId, commentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
