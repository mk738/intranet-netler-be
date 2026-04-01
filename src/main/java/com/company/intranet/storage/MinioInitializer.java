package com.company.intranet.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

import java.util.List;

@Component
@Order(10)
@RequiredArgsConstructor
@Slf4j
public class MinioInitializer implements ApplicationRunner {

    private final S3Client          s3;
    private final StorageProperties props;

    @Override
    public void run(ApplicationArguments args) {
        log.info("MinIO endpoint: {}", props.getEndpoint());
        log.info("MinIO access key: '{}'", props.getAccessKey());
        log.info("MinIO region: {}", props.getRegion());

        List<String> buckets = List.of(
                props.getBucket().getAvatars(),
                props.getBucket().getContracts(),
                props.getBucket().getCvs(),
                props.getBucket().getNewsCovers(),
                props.getBucket().getBoardAttachments());

        for (String name : buckets) {
            try {
                s3.headBucket(r -> r.bucket(name));
                log.debug("MinIO bucket already exists: {}", name);
            } catch (NoSuchBucketException e) {
                s3.createBucket(r -> r.bucket(name));
                log.info("MinIO bucket created: {}", name);
            }
        }
    }
}
