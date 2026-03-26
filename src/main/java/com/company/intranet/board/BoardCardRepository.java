package com.company.intranet.board;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BoardCardRepository extends JpaRepository<BoardCard, UUID> {
}
