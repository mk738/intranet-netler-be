package com.company.intranet.board;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CardAttachmentRepository extends JpaRepository<CardAttachment, UUID> {

    List<CardAttachment> findAllByCardId(UUID cardId);

    long countByCardId(UUID cardId);
}
