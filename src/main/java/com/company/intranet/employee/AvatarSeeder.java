package com.company.intranet.employee;

import com.company.intranet.storage.StorageProperties;
import com.company.intranet.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Seeds avatar images from src/main/resources/db/seed/avatars/ on startup.
 *
 * Naming convention: {employee-uuid}.jpg  (or .png / .webp)
 * Example: 00000000-0000-0000-0000-000000000001.jpg
 *
 * Runs only in the default (local) and test (Railway test) profiles.
 * Silently skips files if the employee is not found in the database.
 */
@Component
@Profile({"default", "dev", "test"})
@Order(200)
@RequiredArgsConstructor
@Slf4j
public class AvatarSeeder implements ApplicationRunner {

    private final EmployeeRepository        employeeRepository;
    private final EmployeeAvatarRepository  avatarRepository;
    private final ResourcePatternResolver   resourceResolver;
    private final StorageService            storageService;
    private final StorageProperties         storageProps;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        Resource[] resources;
        try {
            resources = resourceResolver.getResources("classpath:db/seed/avatars/*");
        } catch (Exception e) {
            log.debug("Avatar seeder: no avatar folder found, skipping");
            return;
        }

        int seeded = 0;
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename == null || filename.startsWith(".")) continue;

            int dotIdx = filename.lastIndexOf('.');
            String uuidStr = dotIdx >= 0 ? filename.substring(0, dotIdx) : filename;

            UUID employeeId;
            try {
                employeeId = UUID.fromString(uuidStr);
            } catch (IllegalArgumentException e) {
                log.warn("Avatar seeder: skipping '{}' — filename is not a valid UUID", filename);
                continue;
            }

            Employee employee = employeeRepository.findById(employeeId).orElse(null);
            if (employee == null) {
                log.debug("Avatar seeder: skipping '{}' — employee not found", employeeId);
                continue;
            }

            // Skip only if the MinIO-style path (plain UUID) is already stored —
            // old Firebase paths like "avatars/{uuid}" must be re-seeded.
            String expectedPath = employeeId.toString();
            if (avatarRepository.findByEmployee(employee)
                    .map(a -> expectedPath.equals(a.getStoragePath()))
                    .orElse(false)) {
                log.debug("Avatar seeder: skipping '{}' — already seeded", employeeId);
                continue;
            }

            byte[] data        = resource.getInputStream().readAllBytes();
            String contentType = contentTypeFor(filename);
            String path        = employeeId.toString();

            storageService.upload(storageProps.getBucket().getAvatars(), path, data, contentType);

            EmployeeAvatar avatar = avatarRepository.findByEmployee(employee)
                    .orElseGet(() -> EmployeeAvatar.builder().employee(employee).build());
            avatar.setContentType(contentType);
            avatar.setStoragePath(path);
            avatarRepository.save(avatar);

            if (employee.getProfile() != null) {
                employee.getProfile().setAvatarUrl("/api/employees/" + employeeId + "/avatar");
                employeeRepository.save(employee);
            }

            log.info("Avatar seeder: loaded avatar for {} ({})", employee.getFullName(), employeeId);
            seeded++;
        }

        if (seeded > 0) {
            log.info("Avatar seeder: seeded {} avatar(s)", seeded);
        }
    }

    private String contentTypeFor(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png"))  return "image/png";
        if (lower.endsWith(".webp")) return "image/webp";
        return "application/octet-stream";
    }
}
