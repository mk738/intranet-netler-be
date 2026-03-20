package com.company.intranet.common.exception;

public enum ErrorCode {

    // ── Auth ──────────────────────────────────────────────────────────────────
    AUTH_ACCOUNT_NOT_FOUND,
    AUTH_ACCOUNT_INACTIVE,

    // ── Employee ──────────────────────────────────────────────────────────────
    EMPLOYEE_NOT_FOUND,
    EMPLOYEE_EMAIL_TAKEN,

    // ── Vacation ──────────────────────────────────────────────────────────────
    VACATION_PAST_DATE,
    VACATION_DATE_INVALID,
    VACATION_OVERLAP,
    VACATION_INSUFFICIENT_DAYS,

    // ── Bank info ─────────────────────────────────────────────────────────────
    BANK_INVALID_CLEARING,
    BANK_INVALID_ACCOUNT,

    // ── File upload ───────────────────────────────────────────────────────────
    FILE_TOO_LARGE,
    FILE_INVALID_TYPE,

    // ── Assignment ────────────────────────────────────────────────────────────
    ASSIGNMENT_ALREADY_ACTIVE,
    ASSIGNMENT_DATE_INVALID,

    // ── CRM / Client ─────────────────────────────────────────────────────────
    CLIENT_ORG_NUMBER_TAKEN,

    // ── Generic fallbacks (used by GlobalExceptionHandler for untyped errors) ─
    VALIDATION_ERROR,
    ACCESS_DENIED,
    NOT_FOUND,
    BAD_REQUEST,
    INTERNAL_ERROR
}
