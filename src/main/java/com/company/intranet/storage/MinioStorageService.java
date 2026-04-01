package com.company.intranet.storage;

import com.company.intranet.common.exception.AppException;
import com.company.intranet.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MinioStorageService implements StorageService {

    private final S3Client          s3;
    private final S3Presigner       presigner;
    private final StorageProperties props;

    @Override
    public void upload(String bucket, String path, MultipartFile file) {
        try {
            s3.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(path)
                            .contentType(file.getContentType())
                            .contentLength(file.getSize())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new AppException(
                    ErrorCode.FILE_UPLOAD_FAILED,
                    "Failed to read upload stream",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void upload(String bucket, String path, byte[] bytes, String contentType) {
        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(path)
                        .contentType(contentType)
                        .contentLength((long) bytes.length)
                        .build(),
                RequestBody.fromBytes(bytes));
    }

    @Override
    public byte[] download(String bucket, String path) {
        try {
            return s3.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(path)
                            .build())
                    .asByteArray();
        } catch (NoSuchKeyException e) {
            throw new AppException(
                    ErrorCode.FILE_NOT_FOUND,
                    "File not found: " + path,
                    HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void delete(String bucket, String path) {
        s3.deleteObject(r -> r.bucket(bucket).key(path));
    }

    @Override
    public String getSignedUrl(String bucket, String path) {
        return presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(props.getSignedUrlExpiryMinutes()))
                        .getObjectRequest(GetObjectRequest.builder()
                                .bucket(bucket)
                                .key(path)
                                .build())
                        .build())
                .url()
                .toString();
    }
}
