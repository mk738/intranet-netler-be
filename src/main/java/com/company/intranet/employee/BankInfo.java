package com.company.intranet.employee;

import com.company.intranet.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "bank_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankInfo extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;

    @Column(name = "bank_name")
    private String bankName;

    // Encrypted at rest via Jasypt — stored as cipher text in DB
    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "clearing_number")
    private String clearingNumber;
}
