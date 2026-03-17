package com.company.intranet.notification;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailTemplatesTest {

    // ── Subjects ───────────────────────────────────────────────────────────────

    @Test
    void inviteSubject_returnsNonBlank() {
        assertThat(EmailTemplates.inviteSubject()).isNotBlank();
    }

    @Test
    void vacationRequestedSubject_containsName() {
        assertThat(EmailTemplates.vacationRequestedSubject("Jane Doe"))
                .contains("Jane Doe");
    }

    @Test
    void vacationReviewedSubject_approved_containsApproved() {
        assertThat(EmailTemplates.vacationReviewedSubject("APPROVED"))
                .containsIgnoringCase("approved");
    }

    @Test
    void vacationReviewedSubject_rejected_containsDeclined() {
        assertThat(EmailTemplates.vacationReviewedSubject("REJECTED"))
                .containsIgnoringCase("declined");
    }

    @Test
    void newsPublishedSubject_containsTitle() {
        assertThat(EmailTemplates.newsPublishedSubject("Q1 Update"))
                .contains("Q1 Update");
    }

    @Test
    void eventCreatedSubject_containsTitle() {
        assertThat(EmailTemplates.eventCreatedSubject("Summer Party"))
                .contains("Summer Party");
    }

    // ── HTML bodies ────────────────────────────────────────────────────────────

    @Test
    void invite_containsNameAndLink() {
        String html = EmailTemplates.invite("Alice", "https://example.com/invite");
        assertThat(html)
                .contains("Alice")
                .contains("https://example.com/invite")
                .contains("<!DOCTYPE html>")
                .contains("Company Intranet");
    }

    @Test
    void invite_escapesHtmlInName() {
        String html = EmailTemplates.invite("<script>alert(1)</script>", "https://x.com");
        assertThat(html)
                .doesNotContain("<script>")
                .contains("&lt;script&gt;");
    }

    @Test
    void vacationRequested_containsEmployeeNameAndDateRange() {
        String html = EmailTemplates.vacationRequested("Bob Smith", "2026-04-01 – 2026-04-07");
        assertThat(html)
                .contains("Bob Smith")
                .contains("2026-04-01");
    }

    @Test
    void vacationReviewed_approved_containsApprovedStatus() {
        String html = EmailTemplates.vacationReviewed("Carol", "2026-05-01 – 2026-05-05", "APPROVED");
        assertThat(html)
                .contains("Carol")
                .contains("Approved")
                .contains("2026-05-01");
    }

    @Test
    void vacationReviewed_rejected_containsDeclinedStatus() {
        String html = EmailTemplates.vacationReviewed("Dave", "2026-06-01 – 2026-06-03", "REJECTED");
        assertThat(html).contains("Declined");
    }

    @Test
    void newsPublished_containsTitle() {
        String html = EmailTemplates.newsPublished("Important Update");
        assertThat(html)
                .contains("Important Update")
                .contains("<!DOCTYPE html>");
    }

    @Test
    void eventCreated_containsTitleDateAndLocation() {
        String html = EmailTemplates.eventCreated("Team Lunch", "April 15, 2026", "Rooftop Terrace");
        assertThat(html)
                .contains("Team Lunch")
                .contains("April 15, 2026")
                .contains("Rooftop Terrace");
    }

    @Test
    void eventCreated_nullLocation_doesNotThrow() {
        String html = EmailTemplates.eventCreated("Remote Kickoff", "May 1, 2026", null);
        assertThat(html)
                .contains("Remote Kickoff")
                .contains("May 1, 2026");
    }

    @Test
    void escape_handlesAllSpecialChars() {
        assertThat(EmailTemplates.escape("a & b < c > d \" e ' f"))
                .isEqualTo("a &amp; b &lt; c &gt; d &quot; e &#x27; f");
    }

    @Test
    void escape_nullReturnsEmptyString() {
        assertThat(EmailTemplates.escape(null)).isEmpty();
    }
}
