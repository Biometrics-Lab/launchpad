package com.biolab.integration.internal.service;

import com.biolab.TestcontainersConfiguration;
import com.biolab.common.ResourceStatusUpdatedEvent;
import com.biolab.common.UrlStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import({TestcontainersConfiguration.class, ResourceStorageServiceIntegrationTest.StatusEventCaptor.class})
@DisplayName("ResourceStorageService Integration Tests")
class ResourceStorageServiceIntegrationTest {

    @Autowired
    ResourceStorageService resourceStorageService;

    @Autowired
    @Qualifier("integrationS3Client")
    S3Client s3Client;

    @Autowired
    StatusEventCaptor statusEventCaptor;

    @Value("${aws.s3.bucket}")
    String bucket;

    @BeforeEach
    void setUp() {
        try {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        } catch (Exception ignored) {
        }
        statusEventCaptor.clear();
    }

    @Test
    @DisplayName("Binary upload -> returns internal URL and fires READY event")
    void uploadBinaryData_returnsInternalUrlAndFiresReadyEvent() {
        UUID uuid = UUID.randomUUID();
        byte[] data = "test-content".getBytes();

        String internalUrl = resourceStorageService.upload(data, "text/plain", "test.txt", uuid);

        assertNotNull(internalUrl);

        await().atMost(5, TimeUnit.SECONDS).until(() ->
                statusEventCaptor.events().stream().anyMatch(e ->
                        e.uuid().equals(uuid) && e.urlStatus() == UrlStatus.READY));

        s3Client.headObject(HeadObjectRequest.builder()
                .bucket(bucket)
                .key("resources/" + uuid + "/test.txt")
                .build());
    }

    @Test
    @DisplayName("External URL upload -> downloads, re-uploads to S3, fires READY event")
    void uploadFromExternalUrl_returnsInternalUrlAndFiresReadyEvent() throws Exception {
        UUID uuid = UUID.randomUUID();
        // use a local file URL as stand-in for external URL
        String externalUrl = getClass().getResource("/test-resource.txt").toURI().toString();

        String internalUrl = resourceStorageService.upload(externalUrl, uuid);

        assertNotNull(internalUrl);

        await().atMost(5, TimeUnit.SECONDS).until(() ->
                statusEventCaptor.events().stream().anyMatch(e ->
                        e.uuid().equals(uuid) && e.urlStatus() == UrlStatus.READY));
    }

    @Component
    static class StatusEventCaptor {
        private final List<ResourceStatusUpdatedEvent> events = new ArrayList<>();

        @EventListener
        void capture(ResourceStatusUpdatedEvent event) {
            events.add(event);
        }

        List<ResourceStatusUpdatedEvent> events() {
            return events;
        }

        void clear() {
            events.clear();
        }
    }
}
