package com.company.intranet.onboarding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds the onboarding template on first startup.
 * Runs after SchemaCleanup (@Order 1) and MinioInitializer (@Order 10).
 * Only inserts if the table is empty — safe to run on every deploy.
 */
@Component
@Order(20)
@RequiredArgsConstructor
@Slf4j
public class OnboardingTemplateSeed implements ApplicationRunner {

    private final OnboardingTemplateItemRepository templateItemRepository;

    private static final List<Object[]> TEMPLATE = List.of(
            new Object[]{"CREATE_CV",             "Skapa CV",                                    1},
            new Object[]{"NETLER_MAIL",           "Netler-mail via Google Admin",                2},
            new Object[]{"BIRTHDAY_IN_SLACK",     "Lägg in födelsedag i Slack",                  3},
            new Object[]{"SLACK_INVITATION",      "Slack-inbjudan till Netler-mail",             4},
            new Object[]{"FORTNOX_ADD_USER",      "Fortnox: Lägg till i kvitton & utlägg",       5},
            new Object[]{"FORTNOX_RECEIPT_GROUP", "Fortnox: Lägg till i kvittogruppen",          6},
            new Object[]{"SLACK_WELCOME_MESSAGE", "Skriv välkommen i Slack-kanalen",             7},
            new Object[]{"SLACK_PROFILE_PHOTO",   "Be om profilbild till Slack",                 8},
            new Object[]{"SEND_WELCOME_LETTER",   "Skicka välkomstbrev till Netler-mail",        9},
            new Object[]{"REQUEST_BANK_DETAILS",  "Be om bankuppgifter",                         10},
            new Object[]{"NOTIFY_PAYROLL",        "Meddela löneavdelningen",                     11},
            new Object[]{"SETUP_PENSION",         "Lägg upp pension",                            12}
    );

    @Override
    public void run(ApplicationArguments args) {
        if (!templateItemRepository.findByActiveTrueOrderBySortOrder().isEmpty()) {
            log.debug("OnboardingTemplateSeed: template already seeded, skipping");
            return;
        }

        List<OnboardingTemplateItem> items = TEMPLATE.stream()
                .map(row -> OnboardingTemplateItem.builder()
                        .taskKey((String) row[0])
                        .labelSv((String) row[1])
                        .sortOrder((int) row[2])
                        .active(true)
                        .build())
                .toList();

        templateItemRepository.saveAll(items);
        log.info("OnboardingTemplateSeed: seeded {} template items", items.size());
    }
}
