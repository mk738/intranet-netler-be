package com.company.intranet.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmailServiceTest {

    @Mock RestClient                    restClient;
    @Mock RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock RestClient.RequestBodySpec    requestBodySpec;
    @Mock RestClient.ResponseSpec       responseSpec;

    EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(restClient, "test-key", "Test <test@x.com>");

        // Wire up the RestClient fluent chain
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), any(String[].class))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.body((Object) any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);
    }

    @Test
    void sendInvite_callsResendEndpoint() {
        emailService.sendInvite("user@x.com", "Alice", "https://example.com/invite");

        verify(restClient).post();
        verify(requestBodyUriSpec).uri("/emails");
        verify(requestBodySpec).header(eq("Authorization"), eq("Bearer test-key"));
    }

    @Test
    void sendVacationRequested_callsResendForAdminList() {
        emailService.sendVacationRequested("Bob", "Apr 1–5", List.of("admin1@x.com", "admin2@x.com"));

        verify(restClient).post();
        verify(requestBodySpec).body((Object) argThat(payload -> {
            String s = payload.toString();
            return s.contains("admin1@x.com") || s.contains("admin2@x.com");
        }));
    }

    @Test
    void sendVacationReviewed_callsResendEndpoint() {
        emailService.sendVacationReviewed("emp@x.com", "Carol", "May 10–12", "APPROVED");

        verify(restClient).post();
        verify(requestBodyUriSpec).uri("/emails");
    }

    @Test
    void sendNewsPublished_callsResendEndpoint() {
        emailService.sendNewsPublished("Q2 Update", List.of("a@x.com", "b@x.com"));

        verify(restClient).post();
        verify(requestBodyUriSpec).uri("/emails");
    }

    @Test
    void sendEventCreated_callsResendEndpoint() {
        emailService.sendEventCreated("Summer Party", "June 20, 2026", "Rooftop", List.of("a@x.com"));

        verify(restClient).post();
        verify(requestBodyUriSpec).uri("/emails");
    }

    @Test
    void send_emptyRecipients_doesNotCallRestClient() {
        emailService.sendNewsPublished("Title", List.of());

        verify(restClient, never()).post();
    }

    @Test
    void send_nullRecipients_doesNotCallRestClient() {
        emailService.sendNewsPublished("Title", null);

        verify(restClient, never()).post();
    }

    @Test
    void sendInvite_bodyContainsCorrectFromAddress() {
        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);

        emailService.sendInvite("user@x.com", "Dave", "https://example.com");

        verify(requestBodySpec).body((Object) bodyCaptor.capture());
        assertThat(bodyCaptor.getValue().toString()).contains("Test <test@x.com>");
    }
}
