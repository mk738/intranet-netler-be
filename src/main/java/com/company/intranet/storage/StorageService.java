package com.company.intranet.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    /**
     * Upload a multipart file. IOException from the stream is wrapped in AppException internally.
     */
    void upload(String bucket, String path, MultipartFile file);

    /**
     * Upload raw bytes with an explicit content type.
     */
    void upload(String bucket, String path, byte[] bytes, String contentType);

    /**
     * Download the object as a byte array.
     *
     * @throws com.company.intranet.common.exception.AppException with ErrorCode.FILE_NOT_FOUND (404)
     *         if the object does not exist.
     */
    byte[] download(String bucket, String path);

    /**
     * Delete the object. No-op if the object does not exist (S3 delete is idempotent).
     */
    void delete(String bucket, String path);

    /**
     * Generate a presigned GET URL valid for the configured expiry duration.
     */
    String getSignedUrl(String bucket, String path);
}
