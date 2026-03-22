package com.company.intranet.notification;

import com.mailersend.sdk.Email;
import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.Recipient;
import com.mailersend.sdk.exceptions.MailerSendException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
@Slf4j
public class MailerSendService {

    private static final String INVITE_TEMPLATE_ID = "vywj2lpy71jl7oqz";

    private final String apiToken;
    private final String fromEmail;
    private final String fromName;
    private final String loginBaseUrl;

    public MailerSendService(
            @Value("${mailersend.api-token}") String apiToken,
            @Value("${mailersend.from-email}") String fromEmail,
            @Value("${mailersend.from-name:Intranet}") String fromName,
            @Value("${resend.base-url:https://intranet.yourcompany.com}") String loginBaseUrl) {
        this.apiToken     = apiToken;
        this.fromEmail    = fromEmail;
        this.fromName     = fromName;
        this.loginBaseUrl = loginBaseUrl;
    }

    public void sendInvite(String recipientEmail, String recipientName,
                           String inviteLink, String invitedByName) {
        Email email = new Email();
        email.setFrom(fromName, fromEmail);

        Recipient recipient = new Recipient(recipientName, recipientEmail);
        email.addRecipient(recipient);

        email.setTemplateId(INVITE_TEMPLATE_ID);

        email.addPersonalization(recipient, "year",           String.valueOf(Year.now().getValue()));
        email.addPersonalization(recipient, "login_url",      inviteLink.isBlank() ? loginBaseUrl : inviteLink);
        email.addPersonalization(recipient, "invited_by",     invitedByName);
        email.addPersonalization(recipient, "employee_name",  recipientName);
        email.addPersonalization(recipient, "employee_email", recipientEmail);

        MailerSend ms = new MailerSend();
        ms.setToken(apiToken);

        try {
            var response = ms.send(email);
            log.info("Invite email sent via MailerSend to={} messageId={}", recipientEmail, response.messageId);
        } catch (MailerSendException e) {
            log.error("MailerSend failed to send invite to={}: {}", recipientEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send invite email", e);
        }
    }
}
