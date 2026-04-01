package com.company.intranet.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByFirebaseUid(String firebaseUid);

    Optional<Employee> findByEmail(String email);

    @Query("""
        SELECT e FROM Employee e JOIN FETCH e.profile
        WHERE e.isActive = true
           OR (e.isActive = false AND e.terminationDate >= CURRENT_DATE)
        ORDER BY e.profile.lastName
    """)
    List<Employee> findAllActiveWithProfile();

    @Query("SELECT e.email FROM Employee e WHERE e.role = 'ADMIN' AND e.isActive = true")
    List<String> findAllAdminEmails();

    @Query("SELECT e.email FROM Employee e WHERE e.isActive = true")
    List<String> findAllActiveEmails();

    @Query("""
        SELECT e FROM Employee e
        JOIN FETCH e.profile
        WHERE e.isActive = true
        AND NOT EXISTS (
            SELECT a FROM Assignment a
            WHERE a.employee = e
            AND a.status = 'ACTIVE'
        )
        ORDER BY e.profile.lastName
    """)
    List<Employee> findAllWithNoActiveAssignment();

    List<Employee> findByEmploymentEndDateBeforeAndIsActiveTrue(java.time.LocalDate date);
}
