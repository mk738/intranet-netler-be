package com.company.intranet.employee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmployeeBenefitRepository extends JpaRepository<EmployeeBenefit, UUID> {

    List<EmployeeBenefit> findByEmployeeOrderBySortOrderAsc(Employee employee);

    void deleteByEmployee(Employee employee);
}
