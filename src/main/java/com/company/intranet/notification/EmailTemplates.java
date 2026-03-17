package com.company.intranet.notification;

/**
 * Pure static utility class for generating branded HTML email bodies and subject lines.
 * Table-based layout for broad client compatibility (including Outlook).
 * Theme: dark purple (#3B0764) header, white card, grey background.
 */
public final class EmailTemplates {

    private EmailTemplates() {}

    // ── Subjects ───────────────────────────────────────────────────────────────

    public static String inviteSubject() {
        return "You've been invited to the Company Intranet";
    }

    public static String vacationRequestedSubject(String employeeName) {
        return "Vacation request from " + employeeName;
    }

    public static String vacationReviewedSubject(String status) {
        boolean approved = "APPROVED".equalsIgnoreCase(status);
        return approved ? "Your vacation request has been approved"
                        : "Your vacation request has been declined";
    }

    public static String newsPublishedSubject(String newsTitle) {
        return "New post: " + newsTitle;
    }

    public static String eventCreatedSubject(String eventTitle) {
        return "New event: " + eventTitle;
    }

    // ── HTML bodies ────────────────────────────────────────────────────────────

    public static String invite(String name, String inviteLink) {
        String body =
            "<h2 style=\"margin:0 0 16px;color:#1F2937;font-size:22px;font-weight:700;\">Welcome, " + escape(name) + "!</h2>" +
            "<p style=\"margin:0 0 16px;color:#4B5563;line-height:1.6;\">You've been invited to join the Company Intranet. " +
            "Click the button below to set up your account and get started.</p>" +
            button("Set up my account", inviteLink) +
            "<p style=\"margin:16px 0 0;color:#9CA3AF;font-size:13px;\">This link will expire in 48 hours. " +
            "If you weren't expecting this invitation, you can safely ignore this email.</p>";
        return wrap("You're invited!", body);
    }

    public static String vacationRequested(String employeeName, String dateRange) {
        String body =
            "<h2 style=\"margin:0 0 16px;color:#1F2937;font-size:22px;font-weight:700;\">Vacation Request</h2>" +
            "<p style=\"margin:0 0 16px;color:#4B5563;line-height:1.6;\">" +
            "<strong>" + escape(employeeName) + "</strong> has submitted a vacation request and is awaiting your approval.</p>" +
            infoBox("Requested period", escape(dateRange)) +
            "<p style=\"margin:16px 0 0;color:#4B5563;line-height:1.6;\">Please log in to the intranet to review and approve or decline this request.</p>";
        return wrap("Vacation Request", body);
    }

    public static String vacationReviewed(String employeeName, String dateRange, String status) {
        boolean approved = "APPROVED".equalsIgnoreCase(status);
        String statusColor  = approved ? "#059669" : "#DC2626";
        String statusLabel  = approved ? "Approved" : "Declined";
        String statusEmoji  = approved ? "&#10003;" : "&#10007;";
        String body =
            "<h2 style=\"margin:0 0 16px;color:#1F2937;font-size:22px;font-weight:700;\">Vacation Request Update</h2>" +
            "<p style=\"margin:0 0 16px;color:#4B5563;line-height:1.6;\">Hi " + escape(employeeName) + ",</p>" +
            "<p style=\"margin:0 0 16px;color:#4B5563;line-height:1.6;\">Your vacation request has been reviewed.</p>" +
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin:0 0 16px;\">" +
            "<tr><td style=\"background:#F9FAFB;border:1px solid #E5E7EB;border-radius:6px;padding:16px 20px;\">" +
            "<p style=\"margin:0 0 8px;font-size:13px;color:#6B7280;text-transform:uppercase;letter-spacing:0.05em;\">Requested Period</p>" +
            "<p style=\"margin:0 0 12px;font-size:15px;color:#1F2937;font-weight:600;\">" + escape(dateRange) + "</p>" +
            "<p style=\"margin:0;font-size:13px;color:#6B7280;text-transform:uppercase;letter-spacing:0.05em;\">Status</p>" +
            "<p style=\"margin:4px 0 0;font-size:16px;font-weight:700;color:" + statusColor + ";\">" +
            statusEmoji + "&nbsp;" + statusLabel + "</p>" +
            "</td></tr></table>" +
            (approved
                ? "<p style=\"margin:0;color:#4B5563;line-height:1.6;\">Enjoy your time off! Your absence has been noted in the system.</p>"
                : "<p style=\"margin:0;color:#4B5563;line-height:1.6;\">If you have questions about this decision, please speak with your manager.</p>");
        return wrap("Vacation Request Update", body);
    }

    public static String newsPublished(String newsTitle) {
        String body =
            "<h2 style=\"margin:0 0 16px;color:#1F2937;font-size:22px;font-weight:700;\">New article published</h2>" +
            "<p style=\"margin:0 0 16px;color:#4B5563;line-height:1.6;\">A new article has been published on the intranet:</p>" +
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin:0 0 24px;\">" +
            "<tr><td style=\"background:#F5F3FF;border-left:4px solid #7C3AED;padding:16px 20px;border-radius:0 6px 6px 0;\">" +
            "<p style=\"margin:0;font-size:17px;color:#3B0764;font-weight:600;\">" + escape(newsTitle) + "</p>" +
            "</td></tr></table>" +
            "<p style=\"margin:0 0 24px;color:#4B5563;line-height:1.6;\">Log in to the intranet to read the full article.</p>";
        return wrap("New Article", body);
    }

    public static String eventCreated(String eventTitle, String eventDate, String location) {
        String locationRow = location != null && !location.isBlank()
                ? "<p style=\"margin:0 0 8px;font-size:13px;color:#6B7280;text-transform:uppercase;letter-spacing:0.05em;\">Location</p>" +
                  "<p style=\"margin:0 0 0;font-size:15px;color:#1F2937;font-weight:600;\">" + escape(location) + "</p>"
                : "";
        String body =
            "<h2 style=\"margin:0 0 16px;color:#1F2937;font-size:22px;font-weight:700;\">New event added</h2>" +
            "<p style=\"margin:0 0 16px;color:#4B5563;line-height:1.6;\">A new company event has been scheduled:</p>" +
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin:0 0 24px;\">" +
            "<tr><td style=\"background:#F9FAFB;border:1px solid #E5E7EB;border-radius:6px;padding:20px 24px;\">" +
            "<p style=\"margin:0 0 4px;font-size:19px;color:#3B0764;font-weight:700;\">" + escape(eventTitle) + "</p>" +
            "<p style=\"margin:0 0 16px;font-size:14px;color:#7C3AED;font-weight:600;\">" + escape(eventDate) + "</p>" +
            locationRow +
            "</td></tr></table>" +
            "<p style=\"margin:0 0 24px;color:#4B5563;line-height:1.6;\">Log in to the intranet to see the full details and RSVP.</p>";
        return wrap("New Event", body);
    }

    // ── Private layout helpers ─────────────────────────────────────────────────

    private static String wrap(String title, String bodyHtml) {
        return "<!DOCTYPE html>" +
            "<html lang=\"en\">" +
            "<head>" +
            "<meta charset=\"UTF-8\">" +
            "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\">" +
            "<title>" + escape(title) + "</title>" +
            "</head>" +
            "<body style=\"margin:0;padding:0;background-color:#F3F4F6;font-family:Arial,Helvetica,sans-serif;\">" +
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#F3F4F6;\">" +
            "<tr><td align=\"center\" style=\"padding:40px 20px;\">" +
            "<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" " +
            "style=\"max-width:600px;width:100%;background:#ffffff;border-radius:8px;overflow:hidden;" +
            "box-shadow:0 2px 8px rgba(0,0,0,0.08);\">" +
            // Header
            "<tr><td style=\"background:#3B0764;padding:28px 40px;\">" +
            "<h1 style=\"margin:0;color:#ffffff;font-size:20px;font-weight:700;letter-spacing:-0.3px;\">" +
            "Company Intranet</h1>" +
            "</td></tr>" +
            // Body
            "<tr><td style=\"padding:36px 40px 32px;\">" +
            bodyHtml +
            "</td></tr>" +
            // Footer
            "<tr><td style=\"background:#F9FAFB;padding:18px 40px;border-top:1px solid #E5E7EB;text-align:center;\">" +
            "<p style=\"margin:0;font-size:12px;color:#9CA3AF;\">" +
            "This email was sent by your company intranet. Please do not reply to this email." +
            "</p>" +
            "</td></tr>" +
            "</table>" +
            "</td></tr>" +
            "</table>" +
            "</body>" +
            "</html>";
    }

    private static String button(String label, String href) {
        return "<table cellpadding=\"0\" cellspacing=\"0\" style=\"margin:24px 0;\">" +
            "<tr><td style=\"background:#7C3AED;border-radius:6px;\">" +
            "<a href=\"" + href + "\" " +
            "style=\"display:inline-block;padding:12px 28px;color:#ffffff;font-size:15px;" +
            "font-weight:600;text-decoration:none;border-radius:6px;\">" +
            escape(label) + "</a>" +
            "</td></tr>" +
            "</table>";
    }

    private static String infoBox(String label, String value) {
        return "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin:0 0 16px;\">" +
            "<tr><td style=\"background:#F9FAFB;border:1px solid #E5E7EB;border-radius:6px;padding:14px 18px;\">" +
            "<p style=\"margin:0 0 4px;font-size:13px;color:#6B7280;text-transform:uppercase;letter-spacing:0.05em;\">" +
            escape(label) + "</p>" +
            "<p style=\"margin:0;font-size:15px;color:#1F2937;font-weight:600;\">" + value + "</p>" +
            "</td></tr>" +
            "</table>";
    }

    /** Escapes HTML special characters to prevent XSS in template values. */
    static String escape(String value) {
        if (value == null) return "";
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
