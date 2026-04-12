package com.company.intranet.employee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmployeeHardwareRepository extends JpaRepository<EmployeeHardware, UUID> {

    List<EmployeeHardware> findByEmployeeIdOrderByCreatedAtAsc(UUID employeeId);

    void deleteByIdAndEmployeeId(UUID id, UUID employeeId);
}
