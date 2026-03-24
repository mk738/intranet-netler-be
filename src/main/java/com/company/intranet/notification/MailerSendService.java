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
    private final RestClient restClient;

    public MailerSendService(
            @Value("${mailersend.api-token}") String apiToken,
            @Value("${mailersend.from-email}") String fromEmail,
            @Value("${mailersend.from-name:Intranet}") String fromName,
            @Value("${mailersend.login-url:https://intranet.yourcompany.com/login}") String loginBaseUrl,
            @Value("${mailersend.site-url:https://intranet.yourcompany.com}") String siteBaseUrl,
            @Value("${mailersend.vacation-notify-email:marcus.karlsson@netler.com}") String vacationNotifyEmail,
            @Value("${mailersend.news-notify-email:marcus.karlsson@netler.com}") String newsNotifyEmail) {
        this.apiToken            = apiToken;
        this.fromEmail           = fromEmail;
        this.fromName            = fromName;
        this.loginBaseUrl        = loginBaseUrl;
        this.siteBaseUrl         = siteBaseUrl;
        this.vacationNotifyEmail = vacationNotifyEmail;
        this.newsNotifyEmail     = newsNotifyEmail;
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

        var payload = Map.of(
                "from",            Map.of("email", fromEmail, "name", fromName),
                "to",              List.of(Map.of("email", newsNotifyEmail)),
                "subject",         postTitle,
                "template_id",     NEWS_TEMPLATE_ID,
                "personalization", List.of(Map.of("email", newsNotifyEmail, "data", personalizationData))
        );

        restClient.post()
                .uri("/email")
                .header("Authorization", "Bearer " + apiToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

        log.info("News published email sent via MailerSend to {} for post '{}'",
                newsNotifyEmail, postTitle);
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
}
