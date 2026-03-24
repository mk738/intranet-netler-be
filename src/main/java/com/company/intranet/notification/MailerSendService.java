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
public class MailerSendService {

    private static final String INVITE_TEMPLATE_ID    = "vywj2lpy71jl7oqz";
    private static final String NEWS_TEMPLATE_ID      = "k68zxl2613egj905";
    private static final String VACATION_TEMPLATE_ID  = "pxkjn41wpn5lz781";
    private static final String MAILERSEND_API_URL  = "https://api.mailersend.com/v1";

    private final String apiToken;
    private final String fromEmail;
    private final String fromName;
    private final String loginBaseUrl;
    private final String siteBaseUrl;
    private final String vacationNotifyEmail;
    private final String newsNotifyEmail;
    private final String vacationReviewedTemplateId;
    private final String eventCreatedTemplateId;
    private final RestClient restClient;

    public MailerSendService(
            @Value("${mailersend.api-token}") String apiToken,
            @Value("${mailersend.from-email}") String fromEmail,
            @Value("${mailersend.from-name:Intranet}") String fromName,
            @Value("${mailersend.login-url:https://intranet.yourcompany.com/login}") String loginBaseUrl,
            @Value("${mailersend.site-url:https://intranet.yourcompany.com}") String siteBaseUrl,
            @Value("${mailersend.vacation-notify-email:marcus.karlsson@netler.com}") String vacationNotifyEmail,
            @Value("${mailersend.news-notify-email:marcus.karlsson@netler.com}") String newsNotifyEmail,
            @Value("${mailersend.vacation-reviewed-template-id:}") String vacationReviewedTemplateId,
            @Value("${mailersend.event-created-template-id:}") String eventCreatedTemplateId) {
        this.apiToken            = apiToken;
        this.fromEmail           = fromEmail;
        this.fromName            = fromName;
        this.loginBaseUrl        = loginBaseUrl;
        this.siteBaseUrl         = siteBaseUrl;
        this.vacationNotifyEmail        = vacationNotifyEmail;
        this.newsNotifyEmail            = newsNotifyEmail;
        this.vacationReviewedTemplateId = vacationReviewedTemplateId;
        this.eventCreatedTemplateId     = eventCreatedTemplateId;
        this.restClient   = RestClient.builder()
                .baseUrl(MAILERSEND_API_URL)
                .build();
    }

    public void sendInvite(String recipientEmail, String recipientName,
                           String inviteLink, String invitedByName) {
        var payload = Map.of(
                "from",            Map.of("email", fromEmail, "name", fromName),
                "to",              List.of(Map.of("email", recipientEmail, "name", recipientName)),
                "subject",         "You've been invited to the intranet",
                "template_id",     INVITE_TEMPLATE_ID,
                "personalization", List.of(Map.of(
                        "email", recipientEmail,
                        "data",  Map.of(
                                "year",           String.valueOf(Year.now().getValue()),
                                "login_url",      inviteLink.isBlank() ? loginBaseUrl : inviteLink,
                                "invited_by",     invitedByName,
                                "employee_name",  recipientName,
                                "employee_email", recipientEmail
                        )
                ))
        );

        restClient.post()
                .uri("/email")
                .header("Authorization", "Bearer " + apiToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

        log.info("Invite email sent via MailerSend to={}", recipientEmail);
    }

    public void sendNewsPublished(UUID postId, String postTitle, String authorName,
                                  String publishedDate, String excerpt,
                                  List<String> recipientEmails) {
        var personalizationData = Map.of(
                "year",           String.valueOf(Year.now().getValue()),
                "post_title",     postTitle,
                "author_name",    authorName,
                "published_date", publishedDate,
                "excerpt",        excerpt,
                "post_url",       siteBaseUrl + "/news/" + postId,
                "teaser_text",    excerpt,
                "category",       ""
        );

        var recipients = List.of(newsNotifyEmail, "mackke90@gmail.com");

        for (String recipient : recipients) {
            var payload = Map.of(
                    "from",            Map.of("email", fromEmail, "name", fromName),
                    "to",              List.of(Map.of("email", recipient)),
                    "subject",         postTitle,
                    "template_id",     NEWS_TEMPLATE_ID,
                    "personalization", List.of(Map.of("email", recipient, "data", personalizationData))
            );

            restClient.post()
                    .uri("/email")
                    .header("Authorization", "Bearer " + apiToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.info("News published email sent via MailerSend to {} for post '{}'", recipient, postTitle);
        }
    }

    public void sendVacationRequested(String employeeName, String jobTitle,
                                      String startDate, String endDate,
                                      int daysCount, String submittedAt,
                                      List<String> adminEmails) {
        var personalizationData = Map.of(
                "year",               String.valueOf(Year.now().getValue()),
                "employee_name",      employeeName,
                "employee_job_title", jobTitle,
                "start_date",         startDate,
                "end_date",           endDate,
                "days_count",         String.valueOf(daysCount),
                "submitted_at",       submittedAt,
                "portal_url",         siteBaseUrl + "/vacation"
        );

        var payload = Map.of(
                "from",            Map.of("email", fromEmail, "name", fromName),
                "to",              List.of(Map.of("email", vacationNotifyEmail)),
                "subject",         employeeName + " has submitted a vacation request",
                "template_id",     VACATION_TEMPLATE_ID,
                "personalization", List.of(Map.of("email", vacationNotifyEmail, "data", personalizationData))
        );

        restClient.post()
                .uri("/email")
                .header("Authorization", "Bearer " + apiToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

        log.info("Vacation request email sent via MailerSend to {} for '{}'",
                vacationNotifyEmail, employeeName);
    }

    public void sendVacationReviewed(String employeeEmail, String employeeName,
                                     String dateRange, String status) {
        if (vacationReviewedTemplateId.isBlank()) {
            log.warn("sendVacationReviewed skipped — mailersend.vacation-reviewed-template-id not configured");
            return;
        }

        var personalizationData = Map.of(
                "year",          String.valueOf(Year.now().getValue()),
                "employee_name", employeeName,
                "date_range",    dateRange,
                "status",        status,
                "portal_url",    siteBaseUrl + "/vacation"
        );

        var payload = Map.of(
                "from",            Map.of("email", fromEmail, "name", fromName),
                "to",              List.of(Map.of("email", employeeEmail)),
                "subject",         "Your vacation request has been " + status,
                "template_id",     vacationReviewedTemplateId,
                "personalization", List.of(Map.of("email", employeeEmail, "data", personalizationData))
        );

        restClient.post()
                .uri("/email")
                .header("Authorization", "Bearer " + apiToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

        log.info("Vacation reviewed email sent via MailerSend to {} ({})", employeeEmail, status);
    }

    public void sendEventCreated(String eventTitle, String eventDate,
                                 String location, List<String> recipientEmails) {
        if (eventCreatedTemplateId.isBlank()) {
            log.warn("sendEventCreated skipped — mailersend.event-created-template-id not configured");
            return;
        }
        if (recipientEmails == null || recipientEmails.isEmpty()) return;

        var personalizationData = Map.of(
                "year",        String.valueOf(Year.now().getValue()),
                "event_title", eventTitle,
                "event_date",  eventDate,
                "location",    location != null ? location : "",
                "portal_url",  siteBaseUrl + "/events"
        );

        var to = recipientEmails.stream()
                .map(email -> Map.of("email", email))
                .toList();

        var personalizations = recipientEmails.stream()
                .map(email -> Map.of("email", email, "data", personalizationData))
                .toList();

        var payload = Map.of(
                "from",            Map.of("email", fromEmail, "name", fromName),
                "to",              to,
                "subject",         "New event: " + eventTitle,
                "template_id",     eventCreatedTemplateId,
                "personalization", personalizations
        );

        restClient.post()
                .uri("/email")
                .header("Authorization", "Bearer " + apiToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

        log.info("Event created email sent via MailerSend to {} recipients for '{}'",
                recipientEmails.size(), eventTitle);
    }
}
