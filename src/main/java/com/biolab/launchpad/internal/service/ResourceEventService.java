package com.biolab.launchpad.internal.service;

import com.biolab.common.Dictionary;
import com.biolab.common.ResourceCreatedEvent;
import com.biolab.common.ResourceStatusUpdatedEvent;
import com.biolab.launchpad.internal.repository.AssessmentResourceRepository;
import com.biolab.launchpad.internal.repository.RepResourceRepository;
import com.biolab.launchpad.internal.repository.SessionResourceRepository;
import com.biolab.launchpad.internal.repository.model.AssessmentResource;
import com.biolab.launchpad.internal.repository.model.RepResource;
import com.biolab.launchpad.internal.repository.model.SessionResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ResourceEventService {

    private final RepResourceRepository repResourceRepository;
    private final SessionResourceRepository sessionResourceRepository;
    private final AssessmentResourceRepository assessmentResourceRepository;

    @ApplicationModuleListener(condition = "'" + Dictionary.Service.launchpad + "'.equals(#event.targetModule)")
    public void onResourceCreated(ResourceCreatedEvent event) {
        log.info("Launchpad saving resource uuid={} type={}", event.uuid(), event.resourceType());
        switch (event.resourceType()) {
            case REP -> repResourceRepository.save(RepResource.builder()
                    .repId(event.contextId())
                    .url(event.internalUrl())
                    .uuid(event.uuid())
                    .externalUrl(event.externalUrl())
                    .urlStatus(event.urlStatus())
                    .type(event.contentType())
                    .build());
            case SESSION -> sessionResourceRepository.save(SessionResource.builder()
                    .sessionId(event.contextId())
                    .url(event.internalUrl())
                    .uuid(event.uuid())
                    .externalUrl(event.externalUrl())
                    .urlStatus(event.urlStatus())
                    .type(event.contentType())
                    .build());
            case ASSESSMENT -> assessmentResourceRepository.save(AssessmentResource.builder()
                    .assessmentId(event.contextId())
                    .url(event.internalUrl())
                    .uuid(event.uuid())
                    .externalUrl(event.externalUrl())
                    .urlStatus(event.urlStatus())
                    .type(event.contentType())
                    .build());
        }
    }

    @ApplicationModuleListener(condition = "'" + Dictionary.Service.launchpad + "'.equals(#event.targetModule)")
    public void onResourceStatusUpdated(ResourceStatusUpdatedEvent event) {
        log.info("Launchpad updating resource status uuid={} status={}", event.uuid(), event.urlStatus());

        repResourceRepository.findByUuid(event.uuid()).ifPresent(r -> {
            r.setUrlStatus(event.urlStatus());
            if (event.internalUrl() != null) r.setUrl(event.internalUrl());
            repResourceRepository.save(r);
        });
        sessionResourceRepository.findByUuid(event.uuid()).ifPresent(r -> {
            r.setUrlStatus(event.urlStatus());
            if (event.internalUrl() != null) r.setUrl(event.internalUrl());
            sessionResourceRepository.save(r);
        });
        assessmentResourceRepository.findByUuid(event.uuid()).ifPresent(r -> {
            r.setUrlStatus(event.urlStatus());
            if (event.internalUrl() != null) r.setUrl(event.internalUrl());
            assessmentResourceRepository.save(r);
        });
    }
}
