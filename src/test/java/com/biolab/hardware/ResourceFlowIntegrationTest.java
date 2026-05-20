package com.biolab.hardware;

import com.biolab.TestcontainersConfiguration;
import com.biolab.common.ResourceContentType;
import com.biolab.common.ResourceType;
import com.biolab.common.UrlStatus;
import com.biolab.launchpad.internal.repository.RepResourceRepository;
import com.biolab.launchpad.internal.repository.model.Rep;
import com.biolab.launchpad.internal.repository.model.RepResource;
import com.biolab.launchpad.internal.web.controller.EntityFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import({TestcontainersConfiguration.class, EntityFactory.class})
@DisplayName("Resource Event Flow Integration Tests")
class ResourceFlowIntegrationTest {

    @Autowired HardwareServiceGateway hardwareServiceGateway;
    @Autowired RepResourceRepository repResourceRepository;
    @Autowired EntityFactory entityFactory;

    Rep rep;

    @BeforeEach
    void setUp() {
        rep = entityFactory.createRep();
    }

    @AfterEach
    void tearDown() {
        repResourceRepository.deleteAll();
        entityFactory.cleanup();
    }

    @Test
    @DisplayName("Hardware publishes resource -> event flows hardware -> integration -> launchpad -> DB row with correct URL, status, and type")
    void hardwarePublishesResource_flowsToLaunchpadDatabase() {
        UUID uuid = UUID.randomUUID();
        String internalUrl = "http://s3/biolab-resources/resources/" + uuid + "/video.mp4";

        hardwareServiceGateway.publishResourceCreated(uuid, rep.getId(), ResourceType.REP, internalUrl);

        await().atMost(10, TimeUnit.SECONDS).until(() ->
                repResourceRepository.findByUuid(uuid).isPresent());

        RepResource saved = repResourceRepository.findByUuid(uuid).orElseThrow();
        assertEquals(uuid, saved.getUuid());
        assertEquals(internalUrl, saved.getUrl());
        assertEquals(UrlStatus.READY, saved.getUrlStatus());
        assertEquals(rep.getId(), saved.getRepId());
        assertEquals(ResourceContentType.VIDEO.getValue(), saved.getType());
    }
}
