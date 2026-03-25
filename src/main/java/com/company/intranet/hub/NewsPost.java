package com.company.intranet.hub;

import com.company.intranet.common.audit.Auditable;
import com.company.intranet.employee.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "news_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsPost extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "cover_image_data", columnDefinition = "TEXT")
    private String coverImageData;

    @Column(name = "cover_image_type", length = 50)
    private String coverImageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Employee author;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean pinned = false;

    @Column(length = 100)
    private String category;
}
