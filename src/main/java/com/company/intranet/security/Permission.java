package com.company.intranet.security;

public enum Permission {

    // ── Employee management ───────────────────────────────────────────────────
    EMPLOYEE_INVITE,
    EMPLOYEE_TERMINATE,
    EMPLOYEE_DEACTIVATE,
    EMPLOYEE_VIEW_ALL,
    EMPLOYEE_EDIT_ANY,
    EMPLOYEE_CHANGE_ROLE,
    EMPLOYEE_TOGGLE_ACTIVE,

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

    // ── Categories ────────────────────────────────────────────────────────────
    CATEGORY_MANAGE,
}
