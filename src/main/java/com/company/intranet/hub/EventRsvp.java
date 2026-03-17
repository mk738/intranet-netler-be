package com.company.intranet.hub;

import com.company.intranet.common.audit.Auditable;
import com.company.intranet.employee.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
    name = "event_rsvp",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_rsvp_event_employee",
        columnNames = {"event_id", "employee_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRsvp extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RsvpStatus status;

    public enum RsvpStatus { GOING, NOT_GOING, MAYBE }
}
