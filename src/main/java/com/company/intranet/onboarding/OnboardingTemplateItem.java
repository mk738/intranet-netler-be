package com.company.intranet.onboarding;

import com.company.intranet.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "onboarding_template_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingTemplateItem extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "task_key", nullable = false, unique = true, length = 60)
    private String taskKey;

    @Column(name = "label_sv", nullable = false, length = 120)
    private String labelSv;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
