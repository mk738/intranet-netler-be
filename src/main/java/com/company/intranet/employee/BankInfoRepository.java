package com.company.intranet.employee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BankInfoRepository extends JpaRepository<BankInfo, UUID> {

    Optional<BankInfo> findByEmployee(Employee employee);
}
