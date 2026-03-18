package com.company.intranet.employee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeCvRepository extends JpaRepository<EmployeeCv, UUID> {

    Optional<EmployeeCv> findByEmployee(Employee employee);
}
