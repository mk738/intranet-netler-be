package com.company.intranet.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String region = "us-east-1";
    private Buckets bucket = new Buckets();
    private int signedUrlExpiryMinutes = 60;

    @Data
    public static class Buckets {
        private String avatars = "avatars";
        private String contracts = "contracts";
        private String cvs = "cvs";
        private String newsCovers = "news-covers";
        private String boardAttachments = "board-attachments";
    }
}
