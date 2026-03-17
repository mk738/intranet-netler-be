package com.company.intranet.faq;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FaqRepository extends JpaRepository<Faq, UUID> {

    List<Faq> findAllByOrderBySortOrderAscCreatedAtAsc();

    @Query("SELECT COALESCE(MAX(f.sortOrder), -1) FROM Faq f")
    int findMaxSortOrder();
}
