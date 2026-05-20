package com.biolab.integration.internal.service;

import com.biolab.common.UrlStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

@Service
@Log4j2
public class ResourceStorageService {

    private final S3AsyncClient s3AsyncClient;
    private final ResourceStatusPublisher statusPublisher;
    private final String bucket;
    private final String endpoint;

    public ResourceStorageService(
            @Qualifier("integrationS3AsyncClient") S3AsyncClient s3AsyncClient,
            ResourceStatusPublisher statusPublisher,
            @Value("${aws.s3.bucket}") String bucket,
            @Value("${aws.s3.endpoint}") String endpoint) {
        this.s3AsyncClient = s3AsyncClient;
        this.statusPublisher = statusPublisher;
        this.bucket = bucket;
        this.endpoint = endpoint;
    }

    @PostConstruct
    void ensureBucketExists() {
        try {
            s3AsyncClient.headBucket(HeadBucketRequest.builder().bucket(bucket).build()).join();
        } catch (Exception e) {
            if (e.getCause() instanceof NoSuchBucketException) {
                s3AsyncClient.createBucket(CreateBucketRequest.builder().bucket(bucket).build()).join();
                log.info("Created S3 bucket: {}", bucket);
            }
        }
    }

    public String upload(byte[] data, String contentType, String filename, UUID uuid) {
        String key = buildKey(uuid, filename);
        String internalUrl = buildUrl(key);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .contentLength((long) data.length)
                .build();

        s3AsyncClient.putObject(request, AsyncRequestBody.fromBytes(data))
                .thenRun(() -> {
                    log.info("Upload complete for uuid={}", uuid);
                    statusPublisher.publish(uuid, UrlStatus.READY, internalUrl);
                })
                .exceptionally(ex -> {
                    log.error("Upload failed for uuid={}: {}", uuid, ex.getMessage());
                    statusPublisher.publish(uuid, UrlStatus.FAILED, internalUrl);
                    return null;
                });

        return internalUrl;
    }

    public String upload(String externalUrl, UUID uuid) {
        String filename = extractFilename(externalUrl);
        String key = buildKey(uuid, filename);
        String internalUrl = buildUrl(key);

        downloadAndUploadAsync(externalUrl, key, uuid);

        return internalUrl;
    }

    private void downloadAndUploadAsync(String externalUrl, String key, UUID uuid) {
        s3AsyncClient.utilities()
                .getUrl(b -> b.bucket(bucket).key(key));

        try (InputStream stream = URI.create(externalUrl).toURL().openStream()) {
            byte[] data = stream.readAllBytes();

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentLength((long) data.length)
                    .build();

            s3AsyncClient.putObject(request, AsyncRequestBody.fromBytes(data))
                    .thenRun(() -> {
                        log.info("Re-upload complete for uuid={}", uuid);
                        statusPublisher.publish(uuid, UrlStatus.READY, buildUrl(key));
                    })
                    .exceptionally(ex -> {
                        log.error("Re-upload failed for uuid={}: {}", uuid, ex.getMessage());
                        statusPublisher.publish(uuid, UrlStatus.FAILED, buildUrl(key));
                        return null;
                    });
        } catch (IOException e) {
            log.error("Download failed for uuid={} url={}: {}", uuid, externalUrl, e.getMessage());
            statusPublisher.publish(uuid, UrlStatus.FAILED, buildUrl(key));
        }
    }

    private String buildKey(UUID uuid, String filename) {
        return "resources/" + uuid + "/" + filename;
    }

    private String buildUrl(String key) {
        return endpoint + "/" + bucket + "/" + key;
    }

    private String extractFilename(String url) {
        String path = URI.create(url).getPath();
        int lastSlash = path.lastIndexOf('/');
        String name = lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
        return name.isBlank() ? "resource" : name;
    }
}
