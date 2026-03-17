package com.company.intranet.hub;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NewsPostRepository extends JpaRepository<NewsPost, UUID> {

    // Published posts only — for employees
    Page<NewsPost> findByPublishedAtIsNotNull(Pageable pageable);

    // All posts — for admins
    Page<NewsPost> findAll(Pageable pageable);
}
