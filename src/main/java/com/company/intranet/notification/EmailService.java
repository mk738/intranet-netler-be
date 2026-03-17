package com.company.intranet.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@Slf4j
public class EmailService {

    private final RestClient restClient;
    private final String     apiKey;
    private final String     from;

    public EmailService(RestClient restClient,
                        @Value("${resend.api-key}") String apiKey,
                        @Value("${resend.from}") String from) {
        this.restClient = restClient;
        this.apiKey     = apiKey;
        this.from       = from;
    }

    // ── Public send methods ────────────────────────────────────────────────────

    public void sendInvite(String to, String name, String inviteLink) {
        send(List.of(to),
             EmailTemplates.inviteSubject(),
             EmailTemplates.invite(name, inviteLink));
    }

    public void sendVacationRequested(String employeeName, String dateRange,
                                      List<String> adminEmails) {
        send(adminEmails,
             EmailTemplates.vacationRequestedSubject(employeeName),
             EmailTemplates.vacationRequested(employeeName, dateRange));
    }

    public void sendVacationReviewed(String to, String employeeName,
                                     String dateRange, String status) {
        send(List.of(to),
             EmailTemplates.vacationReviewedSubject(status),
             EmailTemplates.vacationReviewed(employeeName, dateRange, status));
    }

    public void sendNewsPublished(String newsTitle, List<String> recipientEmails) {
        send(recipientEmails,
             EmailTemplates.newsPublishedSubject(newsTitle),
             EmailTemplates.newsPublished(newsTitle));
    }

    public void sendEventCreated(String eventTitle, String eventDate,
                                 String location, List<String> recipientEmails) {
        send(recipientEmails,
             EmailTemplates.eventCreatedSubject(eventTitle),
             EmailTemplates.eventCreated(eventTitle, eventDate, location));
    }

    // ── Internal dispatch ──────────────────────────────────────────────────────

    private void send(List<String> to, String subject, String html) {
        if (to == null || to.isEmpty()) {
            log.warn("send() called with empty recipient list for subject: {}", subject);
            return;
        }

        record EmailPayload(String from, List<String> to, String subject, String html) {}

        restClient.post()
                .uri("/emails")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new EmailPayload(from, to, subject, html))
                .retrieve()
                .toBodilessEntity();

        log.debug("Email sent — subject='{}' recipients={}", subject, to.size());
    }
}
