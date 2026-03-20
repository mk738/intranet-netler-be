package com.company.intranet.vacation;

import com.company.intranet.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface VacationRepository extends JpaRepository<VacationRequest, UUID> {

    List<VacationRequest> findByEmployeeOrderByCreatedAtDesc(Employee employee);

    @Query("""
        SELECT v FROM VacationRequest v
        JOIN FETCH v.employee e
        JOIN FETCH e.profile
        WHERE (:status IS NULL OR v.status = :status)
        ORDER BY v.createdAt DESC
    """)
    List<VacationRequest> findAllWithEmployee(
            @Param("status") VacationRequest.VacationStatus status);

    boolean existsByEmployeeAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusNot(
            Employee employee,
            LocalDate endDate,
            LocalDate startDate,
            VacationRequest.VacationStatus status);

    long countByStatus(VacationRequest.VacationStatus status);

    @Query("""
        SELECT COALESCE(SUM(v.daysCount), 0)
        FROM VacationRequest v
        WHERE v.employee = :employee
          AND v.status   = com.company.intranet.vacation.VacationRequest.VacationStatus.APPROVED
          AND YEAR(v.startDate) = :year
    """)
    int sumApprovedDaysForYear(@Param("employee") Employee employee,
                               @Param("year")     int year);
}
