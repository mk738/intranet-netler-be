package com.company.intranet.employee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EducationRepository extends JpaRepository<Education, UUID> {

    List<Education> findByEmployeeOrderByStartYearDesc(Employee employee);

    void deleteByIdAndEmployee(UUID id, Employee employee);
}
