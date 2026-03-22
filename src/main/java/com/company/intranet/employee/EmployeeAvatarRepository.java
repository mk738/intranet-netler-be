package com.company.intranet.employee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeAvatarRepository extends JpaRepository<EmployeeAvatar, UUID> {

    Optional<EmployeeAvatar> findByEmployee(Employee employee);

    boolean existsByEmployee(Employee employee);
}
