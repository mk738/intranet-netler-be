package com.company.intranet.security;

public enum Permission {

    // ── Employee management ───────────────────────────────────────────────────
    EMPLOYEE_INVITE,
    EMPLOYEE_TERMINATE,
    EMPLOYEE_VIEW_ALL,
    EMPLOYEE_EDIT_ANY,

    // ── Vacation ──────────────────────────────────────────────────────────────
    VACATION_VIEW_ALL,
    VACATION_APPROVE,

    // ── CRM ───────────────────────────────────────────────────────────────────
    CRM_MANAGE,

    // ── Content ───────────────────────────────────────────────────────────────
    NEWS_MANAGE,
    EVENT_MANAGE,

    // ── Board ─────────────────────────────────────────────────────────────────
    BOARD_MANAGE,

    // ── FAQ ───────────────────────────────────────────────────────────────────
    FAQ_MANAGE,

    // ── Recruitment ───────────────────────────────────────────────────────────
    CANDIDATE_MANAGE,

    // ── Skill catalogue ───────────────────────────────────────────────────────
    SKILL_CATALOG_MANAGE,
}
