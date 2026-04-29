package com.company.intranet.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ResendService {

    private static final String RESEND_API_URL = "https://api.resend.com";

    private final String apiToken;
    private final String fromEmail;
    private final String fromName;
    private final String loginBaseUrl;
    private final String siteBaseUrl;
    private final String inviteTemplateId;
    private final String newsTemplateId;
    private final String vacationRequestedTemplateId;
    private final String vacationReviewedTemplateId;
    private final String eventCreatedTemplateId;
    private final RestClient restClient;

    public ResendService(
            @Value("${resend.api-token}") String apiToken,
            @Value("${resend.from-email}") String fromEmail,
            @Value("${resend.from-name:Intranet}") String fromName,
            @Value("${resend.login-url}") String loginBaseUrl,
            @Value("${resend.site-url}") String siteBaseUrl,
            @Value("${resend.templates.invite:}") String inviteTemplateId,
            @Value("${resend.templates.news:}") String newsTemplateId,
            @Value("${resend.templates.vacation-requested:}") String vacationRequestedTemplateId,
            @Value("${resend.templates.vacation-reviewed:}") String vacationReviewedTemplateId,
            @Value("${resend.templates.event-created:}") String eventCreatedTemplateId) {
        this.apiToken = apiToken;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.loginBaseUrl = loginBaseUrl;
        this.siteBaseUrl = siteBaseUrl;
        this.inviteTemplateId = inviteTemplateId;
        this.newsTemplateId = newsTemplateId;
        this.vacationRequestedTemplateId = vacationRequestedTemplateId;
        this.vacationReviewedTemplateId = vacationReviewedTemplateId;
        this.eventCreatedTemplateId = eventCreatedTemplateId;
        this.restClient = RestClient.builder()
                .baseUrl(RESEND_API_URL)
                .defaultHeader("Authorization", "Bearer " + apiToken)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private Map<String, Object> buildPayload(String to, String subject,
                                             String templateId, Map<String, Object> variables) {
        return Map.of(
                "from", fromName + " <" + fromEmail + ">",
                "to", List.of(to),
                "subject", subject,
                "template", Map.of(
                        "id", templateId,
                        "variables", variables
                )
        );
    }

    private void send(Map<String, Object> payload) {
        log.debug("Resend payload: {}", payload);
        restClient.post()
                .uri("/emails")
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    public void sendInvite(String recipientEmail, String recipientName,
                           String inviteLink, String invitedByName) {
        var variables = Map.<String, Object>of(
                "year", String.valueOf(Year.now().getValue()),
                "login_url", inviteLink.isBlank() ? loginBaseUrl : inviteLink,
                "invited_by", invitedByName,
                "employee_name", recipientName,
                "employee_email", recipientEmail
        );

        send(buildPayload(recipientEmail, "You've been invited to the intranet",
                inviteTemplateId, variables));

        log.info("Invite email sent via Resend to={}", recipientEmail);
    }

    public void sendNewsPublished(UUID postId, String postTitle, String authorName,
                                  String authorInitials, String coverImageUrl,
                                  String publishedDate, String excerpt,
                                  List<String> recipientEmails) {
        if (newsTemplateId.isBlank()) {
            log.warn("sendNewsPublished skipped — resend.templates.news not configured");
            return;
        }
        if (recipientEmails == null || recipientEmails.isEmpty()) return;

        var variables = Map.<String, Object>ofEntries(
                Map.entry("year", String.valueOf(Year.now().getValue())),
                Map.entry("post_title", postTitle),
                Map.entry("author_name", authorName),
                Map.entry("author_initials", authorInitials),
                Map.entry("cover_image_url", coverImageUrl),
                Map.entry("published_date", publishedDate),
                Map.entry("excerpt", excerpt),
                Map.entry("post_url", siteBaseUrl + "/news/" + postId),
                Map.entry("teaser_text", excerpt),
                Map.entry("category", "")
        );

        for (String recipient : recipientEmails) {
            send(buildPayload(recipient, postTitle, newsTemplateId, variables));
        }

        log.info("News published email sent via Resend to {} recipients for post '{}'",
                recipientEmails.size(), postTitle);
    }

    public void sendVacationRequested(String employeeName, String jobTitle,
                                      String startDate, String endDate,
                                      int daysCount, String submittedAt,
                                      List<String> adminEmails) {
        if (vacationRequestedTemplateId.isBlank()) {
            log.warn("sendVacationRequested skipped — resend.templates.vacation-requested not configured");
            return;
        }

        var variables = Map.<String, Object>of(
                "year", String.valueOf(Year.now().getValue()),
                "employee_name", employeeName,
                "employee_job_title", jobTitle,
                "start_date", startDate,
                "end_date", endDate,
                "days_count", String.valueOf(daysCount),
                "submitted_at", submittedAt,
                "portal_url", siteBaseUrl + "/vacation"
        );

        for (String admin : adminEmails) {
            send(buildPayload(admin,
                    employeeName + " has submitted a vacation request",
                    vacationRequestedTemplateId, variables));
        }

        log.info("Vacation request email sent via Resend to {} admins for '{}'",
                adminEmails.size(), employeeName);
    }

    public void sendVacationReviewed(String employeeEmail, String employeeName,
                                     String dateRange, String status) {
        if (vacationReviewedTemplateId.isBlank()) {
            log.warn("sendVacationReviewed skipped — resend.templates.vacation-reviewed not configured");
            return;
        }

        var variables = Map.<String, Object>of(
                "year", String.valueOf(Year.now().getValue()),
                "employee_name", employeeName,
                "date_range", dateRange,
                "status", status,
                "portal_url", siteBaseUrl + "/vacation"
        );

        send(buildPayload(employeeEmail,
                "Your vacation request has been " + status,
                vacationReviewedTemplateId, variables));

        log.info("Vacation reviewed email sent via Resend to {} ({})", employeeEmail, status);
    }

    public void sendEventCreated(String eventTitle, String eventDate,
                                 String startTime, String endTime,
                                 String location, String description,
                                 String authorName, List<String> recipientEmails) {
        if (eventCreatedTemplateId.isBlank()) {
            log.warn("sendEventCreated skipped — resend.templates.event-created not configured");
            return;
        }
        if (recipientEmails == null || recipientEmails.isEmpty()) return;

        var variables = Map.<String, Object>ofEntries(
                Map.entry("year", String.valueOf(Year.now().getValue())),
                Map.entry("event_title", eventTitle),
                Map.entry("event_date", eventDate),
                Map.entry("start_time", startTime),
                Map.entry("end_time", endTime),
                Map.entry("location", location),
                Map.entry("description", description),
                Map.entry("author_name", authorName),
                Map.entry("portal_url", siteBaseUrl + "/events")
        );

        for (String recipient : recipientEmails) {
            send(buildPayload(recipient, "New event: " + eventTitle,
                    eventCreatedTemplateId, variables));
        }

        log.info("Event created email sent via Resend to {} recipients for '{}'",
                recipientEmails.size(), eventTitle);
    }
}