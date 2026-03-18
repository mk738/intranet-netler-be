package com.company.intranet.employee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeContractRepository extends JpaRepository<EmployeeContract, UUID> {

    Optional<EmployeeContract> findByEmployee(Employee employee);
}
