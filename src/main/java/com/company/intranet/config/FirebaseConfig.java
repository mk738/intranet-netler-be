package com.company.intranet.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials.json}")
    private String credentialsJson;

    // Kept as fallback for local dev without Docker
    @Value("${firebase.credentials.path:firebase-service-account.json}")
    private String credentialsPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        GoogleCredentials credentials;

        if (credentialsJson != null && !credentialsJson.isBlank()) {
            // Reads from env var — used in all environments
            log.info("Initialising Firebase from environment variable");
            credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(
                            credentialsJson.getBytes(StandardCharsets.UTF_8)
                    )
            );
        } else {
            // Fallback — reads from file for legacy local dev
            log.info("Initialising Firebase from file: {}", credentialsPath);
            credentials = GoogleCredentials.fromStream(
                    new FileInputStream(credentialsPath)
            );
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}
