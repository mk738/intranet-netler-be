package com.company.intranet.crm;

import com.company.intranet.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {

    @Query("""
        SELECT a FROM Assignment a
        JOIN FETCH a.employee e
        JOIN FETCH e.profile
        JOIN FETCH a.client
        WHERE a.status = 'ACTIVE'
        ORDER BY a.client.companyName, e.profile.lastName
    """)
    List<Assignment> findAllActiveWithEmployeeAndClient();

    boolean existsByEmployeeAndStatus(Employee employee, Assignment.AssignmentStatus status);

    List<Assignment> findByEmployeeOrderByStartDateDesc(Employee employee);

    List<Assignment> findByClientOrderByStartDateDesc(Client client);
}
