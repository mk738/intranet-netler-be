package com.company.intranet.notification;

import com.company.intranet.notification.events.EmployeeInvitedEvent;
import com.company.intranet.notification.events.EventCreatedEvent;
import com.company.intranet.notification.events.NewsPublishedEvent;
import com.company.intranet.notification.events.VacationRequestedEvent;
import com.company.intranet.notification.events.VacationReviewedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailEventListener {

    private final EmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(EmployeeInvitedEvent e) {
        try {
            emailService.sendInvite(e.recipientEmail(), e.recipientName(), e.inviteLink());
        } catch (Exception ex) {
            log.error("Failed to send invite email to {}: {}", e.recipientEmail(), ex.getMessage(), ex);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(VacationRequestedEvent e) {
        try {
            e.adminEmails().forEach(email ->
                    emailService.sendVacationRequested(
                            e.employeeName(), e.dateRange(), e.adminEmails()));
        } catch (Exception ex) {
            log.error("Failed to send vacation-requested emails for {}: {}",
                    e.employeeName(), ex.getMessage(), ex);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(VacationReviewedEvent e) {
        try {
            emailService.sendVacationReviewed(
                    e.employeeEmail(), e.employeeName(), e.dateRange(), e.status());
        } catch (Exception ex) {
            log.error("Failed to send vacation-reviewed email to {}: {}",
                    e.employeeEmail(), ex.getMessage(), ex);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(NewsPublishedEvent e) {
        try {
            emailService.sendNewsPublished(e.newsTitle(), e.recipientEmails());
        } catch (Exception ex) {
            log.error("Failed to send news-published emails for '{}': {}",
                    e.newsTitle(), ex.getMessage(), ex);
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(EventCreatedEvent e) {
        try {
            emailService.sendEventCreated(
                    e.eventTitle(), e.eventDate(), e.location(), e.recipientEmails());
        } catch (Exception ex) {
            log.error("Failed to send event-created emails for '{}': {}",
                    e.eventTitle(), ex.getMessage(), ex);
        }
    }
}
