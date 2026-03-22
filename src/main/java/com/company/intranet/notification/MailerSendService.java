package com.company.intranet.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Year;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MailerSendService {

    private static final String INVITE_TEMPLATE_ID = "vywj2lpy71jl7oqz";
    private static final String MAILERSEND_API_URL  = "https://api.mailersend.com/v1";

    private final String apiToken;
    private final String fromEmail;
    private final String fromName;
    private final String loginBaseUrl;
    private final RestClient restClient;

    public MailerSendService(
            @Value("${mailersend.api-token}") String apiToken,
            @Value("${mailersend.from-email}") String fromEmail,
            @Value("${mailersend.from-name:Intranet}") String fromName,
            @Value("${resend.base-url:https://intranet.yourcompany.com}") String loginBaseUrl) {
        this.apiToken     = apiToken;
        this.fromEmail    = fromEmail;
        this.fromName     = fromName;
        this.loginBaseUrl = loginBaseUrl;
        this.restClient   = RestClient.builder()
                .baseUrl(MAILERSEND_API_URL)
                .build();
    }

    public void sendInvite(String recipientEmail, String recipientName,
                           String inviteLink, String invitedByName) {
        var payload = Map.of(
                "from",            Map.of("email", fromEmail, "name", fromName),
                "to",              List.of(Map.of("email", recipientEmail, "name", recipientName)),
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
}
