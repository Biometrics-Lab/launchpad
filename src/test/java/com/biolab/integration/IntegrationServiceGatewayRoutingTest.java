package com.biolab.integration;

import com.biolab.common.Dictionary;
import com.biolab.common.ResourceContentType;
import com.biolab.common.ResourceCreatedEvent;
import com.biolab.common.ResourceType;
import com.biolab.common.UrlStatus;
import com.biolab.common.UrlType;
import com.biolab.integration.internal.service.ResourceStorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("IntegrationServiceGateway Routing Tests")
class IntegrationServiceGatewayRoutingTest {

    @Mock ResourceStorageService resourceStorageService;
    @Mock ApplicationEventPublisher eventPublisher;

    @InjectMocks IntegrationServiceGateway gateway;

    @Test
    @DisplayName("EXTERNAL url -> triggers S3 re-upload and forwards enriched event to launchpad")
    void onResourceCreated_externalUrl_triggersReUploadAndForwardsToLaunchpad() {
        UUID uuid = UUID.randomUUID();
        ResourceCreatedEvent event = ResourceCreatedEvent.builder()
                .uuid(uuid).contextId(1).resourceType(ResourceType.REP)
                .internalUrl("http://s3/bucket/resources/" + uuid + "/file.mp4")
                .externalUrl("https://external.api/file.mp4")
                .urlType(UrlType.EXTERNAL).urlStatus(UrlStatus.PENDING)
                .targetModule(Dictionary.Service.integration)
                .build();

        gateway.onResourceCreated(event);

        verify(resourceStorageService).upload("https://external.api/file.mp4", uuid);

        ArgumentCaptor<ResourceCreatedEvent> captor = ArgumentCaptor.forClass(ResourceCreatedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        ResourceCreatedEvent published = captor.getValue();
        assertEquals(Dictionary.Service.launchpad, published.targetModule());
        assertEquals(ResourceContentType.VIDEO.getValue(), published.contentType());
        assertEquals(uuid, published.uuid());
    }

    @Test
    @DisplayName("INTERNAL url -> skips S3 re-upload and forwards enriched event to launchpad")
    void onResourceCreated_internalUrl_skipsReUploadAndForwardsToLaunchpad() {
        UUID uuid = UUID.randomUUID();
        ResourceCreatedEvent event = ResourceCreatedEvent.builder()
                .uuid(uuid).contextId(1).resourceType(ResourceType.REP)
                .internalUrl("http://s3/bucket/resources/" + uuid + "/file.mp4")
                .externalUrl(null)
                .urlType(UrlType.INTERNAL).urlStatus(UrlStatus.READY)
                .targetModule(Dictionary.Service.integration)
                .build();

        gateway.onResourceCreated(event);

        verifyNoInteractions(resourceStorageService);

        ArgumentCaptor<ResourceCreatedEvent> captor = ArgumentCaptor.forClass(ResourceCreatedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        ResourceCreatedEvent published = captor.getValue();
        assertEquals(Dictionary.Service.launchpad, published.targetModule());
        assertEquals(ResourceContentType.VIDEO.getValue(), published.contentType());
    }
}
