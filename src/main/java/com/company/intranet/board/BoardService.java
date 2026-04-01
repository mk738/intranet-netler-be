package com.company.intranet.board;

import com.company.intranet.board.dto.*;
import com.company.intranet.common.exception.AppException;
import com.company.intranet.common.exception.ErrorCode;
import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.storage.StorageProperties;
import com.company.intranet.storage.StorageService;
import com.company.intranet.employee.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {

    private static final long MAX_FILE_BYTES = 10 * 1024 * 1024; // 10 MB

    private final BoardRepository            boardRepository;
    private final BoardColumnRepository      columnRepository;
    private final BoardCardRepository        cardRepository;
    private final BoardCommentRepository     commentRepository;
    private final CardAttachmentRepository   attachmentRepository;
    private final StorageService             storageService;
    private final StorageProperties          storageProps;

    // ── Boards ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<BoardDto> getAllBoards() {
        return boardRepository.findAllByOrderByCreatedAtAsc()
                .stream()
                .map(this::toBoardDto)
                .toList();
    }

    @Transactional
    public BoardDto createBoard(CreateBoardRequest request) {
        Board board = Board.builder()
                .name(request.name())
                .build();
        return toBoardDto(boardRepository.save(board));
    }

    @Transactional
    public BoardDto updateBoard(UUID id, UpdateBoardRequest request) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found"));
        board.setName(request.name());
        return toBoardDto(boardRepository.save(board));
    }

    @Transactional
    public void deleteBoard(UUID id) {
        if (!boardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Board not found");
        }
        boardRepository.deleteById(id);
    }

    // ── Columns ───────────────────────────────────────────────────────────────

    @Transactional
    public BoardColumnDto createColumn(UUID boardId, CreateColumnRequest request) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found"));
        BoardColumn column = BoardColumn.builder()
                .board(board)
                .title(request.title())
                .colorIndex(request.colorIndex())
                .position(request.position())
                .build();
        return toColumnDto(columnRepository.save(column));
    }

    @Transactional
    public BoardColumnDto updateColumn(UUID boardId, UUID id, UpdateColumnRequest request) {
        BoardColumn column = columnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Column not found"));
        if (!column.getBoard().getId().equals(boardId)) {
            throw new ResourceNotFoundException("Column not found in this board");
        }
        column.setTitle(request.title());
        column.setColorIndex(request.colorIndex());
        column.setPosition(request.position());
        return toColumnDto(columnRepository.save(column));
    }

    @Transactional
    public void deleteColumn(UUID boardId, UUID id) {
        BoardColumn column = columnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Column not found"));
        if (!column.getBoard().getId().equals(boardId)) {
            throw new ResourceNotFoundException("Column not found in this board");
        }
        columnRepository.delete(column);
    }

    // ── Cards ─────────────────────────────────────────────────────────────────

    @Transactional
    public BoardCardDto createCard(UUID columnId, CreateCardRequest request) {
        BoardColumn column = columnRepository.findById(columnId)
                .orElseThrow(() -> new ResourceNotFoundException("Column not found"));
        BoardCard card = BoardCard.builder()
                .boardColumn(column)
                .title(request.title())
                .text(request.text())
                .category(request.category())
                .assignedTo(request.assignedTo())
                .position(request.position())
                .build();
        return toCardDto(cardRepository.save(card));
    }

    @Transactional
    public BoardCardDto updateCard(UUID columnId, UUID id, UpdateCardRequest request) {
        BoardCard card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        if (!card.getBoardColumn().getId().equals(columnId)) {
            throw new ResourceNotFoundException("Card not found in this column");
        }

        card.setTitle(request.title());
        card.setText(request.text());
        card.setCategory(request.category());
        card.setAssignedTo(request.assignedTo());
        card.setPosition(request.position());

        // Support moving card to a different column
        if (request.columnId() != null && !request.columnId().equals(columnId)) {
            BoardColumn targetColumn = columnRepository.findById(request.columnId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target column not found"));
            card.setBoardColumn(targetColumn);
        }

        return toCardDto(cardRepository.save(card));
    }

    @Transactional
    public void deleteCard(UUID columnId, UUID id) {
        BoardCard card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        if (!card.getBoardColumn().getId().equals(columnId)) {
            throw new ResourceNotFoundException("Card not found in this column");
        }
        cardRepository.delete(card);
    }

    // ── Comments ──────────────────────────────────────────────────────────────

    @Transactional
    public BoardCommentDto createComment(UUID cardId, CreateCommentRequest request, Employee author) {
        BoardCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        BoardComment comment = BoardComment.builder()
                .card(card)
                .text(request.text())
                .authorName(author.getFullName())
                .build();
        return toCommentDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(UUID cardId, UUID id) {
        BoardComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        if (!comment.getCard().getId().equals(cardId)) {
            throw new ResourceNotFoundException("Comment not found on this card");
        }
        commentRepository.delete(comment);
    }

    // ── Attachments ───────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CardAttachmentDto> getAttachments(UUID cardId) {
        if (!cardRepository.existsById(cardId)) {
            throw new ResourceNotFoundException("Card not found");
        }
        return attachmentRepository.findAllByCardId(cardId)
                .stream()
                .map(this::toAttachmentDto)
                .toList();
    }

    @Transactional
    public CardAttachmentDto uploadAttachment(UUID cardId, MultipartFile file) {
        BoardCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        validateAttachmentFile(file);
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "attachment";
        UUID attachmentId = UUID.randomUUID();
        String objectPath = cardId + "/" + attachmentId + "_" + fileName;
        storageService.upload(storageProps.getBucket().getBoardAttachments(), objectPath, file);
        CardAttachment attachment = CardAttachment.builder()
                .id(attachmentId)
                .card(card)
                .fileName(fileName)
                .contentType(file.getContentType())
                .storagePath(objectPath)
                .build();
        return toAttachmentDto(attachmentRepository.save(attachment));
    }

    @Transactional
    public void deleteAttachment(UUID cardId, UUID attachmentId) {
        CardAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));
        if (!attachment.getCard().getId().equals(cardId)) {
            throw new ResourceNotFoundException("Attachment not found on this card");
        }
        storageService.delete(storageProps.getBucket().getBoardAttachments(), attachment.getStoragePath());
        attachmentRepository.delete(attachment);
    }

    private void validateAttachmentFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_BYTES) {
            throw new AppException(
                    ErrorCode.FILE_TOO_LARGE,
                    "File exceeds the maximum allowed size of 10 MB.",
                    HttpStatus.BAD_REQUEST);
        }
        String contentType = file.getContentType();
        if (contentType == null
                || (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
            throw new AppException(
                    ErrorCode.FILE_INVALID_TYPE,
                    "Only images and PDF files are accepted.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private BoardDto toBoardDto(Board board) {
        return new BoardDto(
                board.getId(),
                board.getName(),
                board.getCreatedBy(),
                board.getCreatedAt() != null ? board.getCreatedAt().toString() : null,
                board.getColumns().stream().map(this::toColumnDto).toList()
        );
    }

    private BoardColumnDto toColumnDto(BoardColumn column) {
        return new BoardColumnDto(
                column.getId(),
                column.getTitle(),
                column.getColorIndex(),
                column.getPosition(),
                column.getCards().stream().map(this::toCardDto).toList()
        );
    }

    private BoardCardDto toCardDto(BoardCard card) {
        return new BoardCardDto(
                card.getId(),
                card.getTitle(),
                card.getText(),
                card.getCategory(),
                card.getAssignedTo(),
                card.getPosition(),
                card.getCreatedAt() != null ? card.getCreatedAt().toString() : null,
                (int) attachmentRepository.countByCardId(card.getId()),
                card.getComments().stream().map(this::toCommentDto).toList()
        );
    }

    private BoardCommentDto toCommentDto(BoardComment comment) {
        return new BoardCommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthorName(),
                comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : null
        );
    }

    private CardAttachmentDto toAttachmentDto(CardAttachment attachment) {
        return new CardAttachmentDto(
                attachment.getId(),
                attachment.getFileName(),
                attachment.getContentType(),
                storageService.getSignedUrl(storageProps.getBucket().getBoardAttachments(), attachment.getStoragePath())
        );
    }
}
