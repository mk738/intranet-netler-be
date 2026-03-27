package com.company.intranet.board;

import com.company.intranet.board.dto.*;
import com.company.intranet.common.response.ApiResponse;
import com.company.intranet.employee.Employee;
import com.company.intranet.security.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('BOARD_MANAGE')")
public class BoardController {

    private final BoardService boardService;

    // ── Boards ────────────────────────────────────────────────────────────────

    @GetMapping("/boards")
    public ResponseEntity<ApiResponse<List<BoardDto>>> getAllBoards() {
        log.info("GET /api/boards");
        return ResponseEntity.ok(ApiResponse.success(boardService.getAllBoards()));
    }

    @PostMapping("/boards")
    public ResponseEntity<ApiResponse<BoardDto>> createBoard(
            @RequestBody @Valid CreateBoardRequest request) {
        log.info("POST /api/boards name={}", request.name());
        BoardDto result = boardService.createBoard(request);
        log.info("Board created id={}", result.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @PutMapping("/boards/{id}")
    public ResponseEntity<ApiResponse<BoardDto>> updateBoard(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateBoardRequest request) {
        log.info("PUT /api/boards/{}", id);
        return ResponseEntity.ok(ApiResponse.success(boardService.updateBoard(id, request)));
    }

    @DeleteMapping("/boards/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(@PathVariable UUID id) {
        log.info("DELETE /api/boards/{}", id);
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }

    // ── Columns ───────────────────────────────────────────────────────────────

    @PostMapping("/boards/{boardId}/columns")
    public ResponseEntity<ApiResponse<BoardColumnDto>> createColumn(
            @PathVariable UUID boardId,
            @RequestBody @Valid CreateColumnRequest request) {
        log.info("POST /api/boards/{}/columns title={}", boardId, request.title());
        BoardColumnDto result = boardService.createColumn(boardId, request);
        log.info("Column created id={}", result.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @PutMapping("/boards/{boardId}/columns/{id}")
    public ResponseEntity<ApiResponse<BoardColumnDto>> updateColumn(
            @PathVariable UUID boardId,
            @PathVariable UUID id,
            @RequestBody @Valid UpdateColumnRequest request) {
        log.info("PUT /api/boards/{}/columns/{}", boardId, id);
        return ResponseEntity.ok(ApiResponse.success(boardService.updateColumn(boardId, id, request)));
    }

    @DeleteMapping("/boards/{boardId}/columns/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteColumn(
            @PathVariable UUID boardId,
            @PathVariable UUID id) {
        log.info("DELETE /api/boards/{}/columns/{}", boardId, id);
        boardService.deleteColumn(boardId, id);
        return ResponseEntity.noContent().build();
    }

    // ── Cards ─────────────────────────────────────────────────────────────────

    @PostMapping("/columns/{columnId}/cards")
    public ResponseEntity<ApiResponse<BoardCardDto>> createCard(
            @PathVariable UUID columnId,
            @RequestBody @Valid CreateCardRequest request) {
        log.info("POST /api/columns/{}/cards title={}", columnId, request.title());
        BoardCardDto result = boardService.createCard(columnId, request);
        log.info("Card created id={}", result.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @PutMapping("/columns/{columnId}/cards/{id}")
    public ResponseEntity<ApiResponse<BoardCardDto>> updateCard(
            @PathVariable UUID columnId,
            @PathVariable UUID id,
            @RequestBody @Valid UpdateCardRequest request) {
        log.info("PUT /api/columns/{}/cards/{}", columnId, id);
        return ResponseEntity.ok(ApiResponse.success(boardService.updateCard(columnId, id, request)));
    }

    @DeleteMapping("/columns/{columnId}/cards/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCard(
            @PathVariable UUID columnId,
            @PathVariable UUID id) {
        log.info("DELETE /api/columns/{}/cards/{}", columnId, id);
        boardService.deleteCard(columnId, id);
        return ResponseEntity.noContent().build();
    }

    // ── Comments ──────────────────────────────────────────────────────────────

    @PostMapping("/cards/{cardId}/comments")
    public ResponseEntity<ApiResponse<BoardCommentDto>> createComment(
            @PathVariable UUID cardId,
            @RequestBody @Valid CreateCommentRequest request,
            @CurrentUser Employee me) {
        log.info("POST /api/cards/{}/comments author={}", cardId, me.getId());
        BoardCommentDto result = boardService.createComment(cardId, request, me);
        log.info("Comment created id={}", result.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @DeleteMapping("/cards/{cardId}/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable UUID cardId,
            @PathVariable UUID id) {
        log.info("DELETE /api/cards/{}/comments/{}", cardId, id);
        boardService.deleteComment(cardId, id);
        return ResponseEntity.noContent().build();
    }
}
