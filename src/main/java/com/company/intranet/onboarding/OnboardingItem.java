package com.company.intranet.onboarding;

import com.company.intranet.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "onboarding_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "task_key"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingItem extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "task_key", nullable = false, length = 60)
    private String taskKey;

    @Column(name = "label_sv", nullable = false, length = 120)
    private String labelSv;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(nullable = false)
    @Builder.Default
    private boolean completed = false;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "completed_by")
    private UUID completedBy;
}
