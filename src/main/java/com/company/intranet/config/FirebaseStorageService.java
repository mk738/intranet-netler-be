package com.company.intranet.config;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class FirebaseStorageService {

    private final FirebaseApp firebaseApp;

    @Value("${firebase.storage.bucket}")
    private String bucketName;

    public String upload(String objectPath, MultipartFile file) throws IOException {
        StorageClient.getInstance(firebaseApp)
                .bucket(bucketName)
                .create(objectPath, file.getInputStream(), file.getContentType());
        return objectPath;
    }

    public String upload(String objectPath, byte[] data, String contentType) {
        StorageClient.getInstance(firebaseApp)
                .bucket(bucketName)
                .create(objectPath, data, contentType);
        return objectPath;
    }

    public void delete(String objectPath) {
        Blob blob = StorageClient.getInstance(firebaseApp)
                .bucket(bucketName)
                .get(objectPath);
        if (blob != null) {
            blob.delete();
        }
    }

    public String getSignedUrl(String objectPath) {
        Storage storage = StorageClient.getInstance(firebaseApp)
                .bucket(bucketName)
                .getStorage();
        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectPath)).build();
        URL url = storage.signUrl(blobInfo, 1, TimeUnit.HOURS,
                Storage.SignUrlOption.withV4Signature());
        return url.toString();
    }
}
