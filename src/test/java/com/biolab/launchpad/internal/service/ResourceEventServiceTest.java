package com.biolab.launchpad.internal.service;

import com.biolab.common.*;
import com.biolab.common.ResourceContentType;
import com.biolab.launchpad.internal.repository.AssessmentResourceRepository;
import com.biolab.launchpad.internal.repository.RepResourceRepository;
import com.biolab.launchpad.internal.repository.SessionResourceRepository;
import com.biolab.launchpad.internal.repository.model.RepResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResourceEventService Tests")
class ResourceEventServiceTest {

    @Mock RepResourceRepository repResourceRepository;
    @Mock SessionResourceRepository sessionResourceRepository;
    @Mock AssessmentResourceRepository assessmentResourceRepository;

    @InjectMocks ResourceEventService resourceEventService;

    @Test
    @DisplayName("REP resource created -> saved with uuid, repId, status, and content type")
    void onResourceCreated_rep_savesRepResourceWithContentType() {
        UUID uuid = UUID.randomUUID();
        ResourceCreatedEvent event = ResourceCreatedEvent.builder()
                .uuid(uuid)
                .contextId(42)
                .resourceType(ResourceType.REP)
                .internalUrl("http://localhost:4566/bucket/resources/" + uuid + "/file.mp4")
                .externalUrl(null)
                .urlType(UrlType.INTERNAL)
                .urlStatus(UrlStatus.PENDING)
                .targetModule(Dictionary.Service.launchpad)
                .contentType(ResourceContentType.VIDEO.getValue())
                .build();

        resourceEventService.onResourceCreated(event);

        ArgumentCaptor<RepResource> captor = ArgumentCaptor.forClass(RepResource.class);
        verify(repResourceRepository).save(captor.capture());
        RepResource saved = captor.getValue();
        assertEquals(uuid, saved.getUuid());
        assertEquals(42, saved.getRepId());
        assertEquals(UrlStatus.PENDING, saved.getUrlStatus());
        assertEquals(ResourceContentType.VIDEO.getValue(), saved.getType());
        verifyNoInteractions(sessionResourceRepository, assessmentResourceRepository);
    }

    @Test
    @DisplayName("Status updated -> URL and status overwritten in existing resource")
    void onResourceStatusUpdated_updatesUrlAndStatus() {
        UUID uuid = UUID.randomUUID();
        RepResource existing = RepResource.builder()
                .repId(1)
                .url("http://url")
                .uuid(uuid)
                .urlStatus(UrlStatus.PENDING)
                .type(ResourceContentType.VIDEO.getValue())
                .build();

        when(repResourceRepository.findByUuid(uuid)).thenReturn(Optional.of(existing));
        when(sessionResourceRepository.findByUuid(uuid)).thenReturn(Optional.empty());
        when(assessmentResourceRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        resourceEventService.onResourceStatusUpdated(ResourceStatusUpdatedEvent.builder()
                .uuid(uuid)
                .urlStatus(UrlStatus.READY)
                .internalUrl("http://s3/updated/url.mp4")
                .targetModule(Dictionary.Service.launchpad)
                .build());

        assertEquals(UrlStatus.READY, existing.getUrlStatus());
        assertEquals("http://s3/updated/url.mp4", existing.getUrl());
        verify(repResourceRepository).save(existing);
    }

    @Test
    @DisplayName("SESSION resource with external URL created -> external URL and PENDING status stored")
    void onResourceCreated_externalUrl_storesExternalUrlAsPending() {
        UUID uuid = UUID.randomUUID();
        ResourceCreatedEvent event = ResourceCreatedEvent.builder()
                .uuid(uuid)
                .contextId(10)
                .resourceType(ResourceType.SESSION)
                .internalUrl("http://localhost:4566/bucket/resources/" + uuid + "/video.mp4")
                .externalUrl("https://external.api/video.mp4")
                .urlType(UrlType.EXTERNAL)
                .urlStatus(UrlStatus.PENDING)
                .targetModule(Dictionary.Service.launchpad)
                .contentType(ResourceContentType.VIDEO.getValue())
                .build();

        resourceEventService.onResourceCreated(event);

        verify(sessionResourceRepository).save(argThat(r ->
                r.getExternalUrl().equals("https://external.api/video.mp4") &&
                r.getUrlStatus() == UrlStatus.PENDING &&
                ResourceContentType.VIDEO.getValue().equals(r.getType())));
    }
}
