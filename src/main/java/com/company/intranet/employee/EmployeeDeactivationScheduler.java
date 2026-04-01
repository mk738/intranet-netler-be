package com.company.intranet.employee;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeDeactivationScheduler {

    private final EmployeeRepository employeeRepository;
    private final FirebaseAuth       firebaseAuth;

    /**
     * Runs every night at midnight.
     * Deactivates all employees whose employmentEndDate has arrived and disables
     * their Firebase accounts.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deactivateExpiredEmployees() {
        LocalDate today = LocalDate.now();

        List<Employee> due = employeeRepository
                .findByEmploymentEndDateBeforeAndIsActiveTrue(today);

        if (due.isEmpty()) return;

        log.info("EmployeeDeactivationScheduler: deactivating {} employee(s)", due.size());

        for (Employee employee : due) {
            employee.setActive(false);
            employee.setEmploymentEndDate(null);
            employeeRepository.save(employee);

            try {
                firebaseAuth.updateUser(
                        new UserRecord.UpdateRequest(employee.getFirebaseUid()).setDisabled(true));
            } catch (FirebaseAuthException e) {
                log.warn("EmployeeDeactivationScheduler: failed to disable Firebase user for employee {}: {}",
                        employee.getId(), e.getMessage());
            }
        }
    }
}
