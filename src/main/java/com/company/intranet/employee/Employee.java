package com.company.intranet.employee;

import com.company.intranet.common.audit.Auditable;
import com.company.intranet.skill.Skill;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "firebase_uid", nullable = false, unique = true)
    private String firebaseUid;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private EmployeeProfile profile;

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BankInfo bankInfo;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "employee_skills",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    @Builder.Default
    private Set<Skill> skills = new LinkedHashSet<>();

    // Helper for display
    public String getFullName() {
        if (profile == null) return email;
        return profile.getFirstName() + " " + profile.getLastName();
    }

    public String getInitials() {
        if (profile == null) return email.substring(0, 1).toUpperCase();
        return String.valueOf(profile.getFirstName().charAt(0))
             + profile.getLastName().charAt(0);
    }

    public enum Role { ADMIN, EMPLOYEE }
}
