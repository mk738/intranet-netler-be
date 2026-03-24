package com.company.intranet.crm;

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
public class AssignmentScheduler {

    private final AssignmentRepository assignmentRepository;
    private final ClientRepository     clientRepository;

    /**
     * Runs every night at 00:05.
     * Ends any ACTIVE assignments whose endDate has passed, then sets the
     * client to INACTIVE if they have no remaining active assignments.
     */
    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void expireEndedAssignments() {
        LocalDate today = LocalDate.now();

        List<Assignment> expired = assignmentRepository
                .findByStatusAndEndDateLessThan(Assignment.AssignmentStatus.ACTIVE, today);

        if (expired.isEmpty()) return;

        log.info("AssignmentScheduler: expiring {} assignment(s)", expired.size());

        for (Assignment assignment : expired) {
            assignment.setStatus(Assignment.AssignmentStatus.ENDED);
            assignmentRepository.save(assignment);

            Client client = assignment.getClient();
            boolean hasOtherActive = assignmentRepository
                    .existsByClientAndStatus(client, Assignment.AssignmentStatus.ACTIVE);
            if (!hasOtherActive) {
                log.info("AssignmentScheduler: setting client '{}' to INACTIVE", client.getCompanyName());
                client.setStatus(Client.ClientStatus.INACTIVE);
                clientRepository.save(client);
            }
        }
    }
}
