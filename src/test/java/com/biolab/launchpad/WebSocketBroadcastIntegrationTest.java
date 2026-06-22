package com.biolab.launchpad;

import com.biolab.TestcontainersConfiguration;
import com.biolab.common.RepDataReceivedEvent;
import com.biolab.launchpad.internal.repository.RepMetricRepository;
import com.biolab.launchpad.internal.repository.RepRepository;
import com.biolab.launchpad.internal.repository.RepResourceRepository;
import com.biolab.launchpad.internal.repository.model.Session;
import com.biolab.launchpad.internal.web.controller.EntityFactory;
import com.biolab.launchpad.internal.web.dto.RepBroadcastDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestcontainersConfiguration.class, EntityFactory.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("WebSocket Broadcast Integration Tests")
class WebSocketBroadcastIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired ApplicationEventPublisher eventPublisher;
    @Autowired TransactionTemplate transactionTemplate;
    @Autowired RepRepository repRepository;
    @Autowired RepMetricRepository repMetricRepository;
    @Autowired RepResourceRepository repResourceRepository;
    @Autowired EntityFactory entityFactory;
    @Autowired ObjectMapper objectMapper;

    Session session;
    WebSocketStompClient stompClient;

    @BeforeEach
    void setUp() {
        session = entityFactory.createActiveSession();

        SockJsClient sockJsClient = new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient())));
        stompClient = new WebSocketStompClient(sockJsClient);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        stompClient.setMessageConverter(converter);
    }

    @AfterEach
    void tearDown() {
        repResourceRepository.deleteAll();
        repMetricRepository.deleteAll();
        repRepository.deleteAll();
        entityFactory.cleanup();
        stompClient.stop();
    }

    @Test
    @DisplayName("RepDataReceivedEvent broadcasts RepBroadcastDto to STOMP /topic/session/{id}/reps")
    void broadcastFiresToStompTopic() throws Exception {
        CompletableFuture<RepBroadcastDto> received = new CompletableFuture<>();

        StompSession stompSession = stompClient
                .connectAsync("http://localhost:" + port + "/ws", new StompSessionHandlerAdapter() {})
                .get(5, TimeUnit.SECONDS);

        stompSession.subscribe("/topic/session/" + session.getId() + "/reps",
                new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return RepBroadcastDto.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        received.complete((RepBroadcastDto) payload);
                    }
                });

        var event = new RepDataReceivedEvent(
                session.getId(), 1,
                Timestamp.valueOf(LocalDateTime.now()),
                List.of(), List.of());
        transactionTemplate.executeWithoutResult(tx -> eventPublisher.publishEvent(event));

        RepBroadcastDto dto = received.get(10, TimeUnit.SECONDS);
        assertEquals(session.getId(), dto.sessionId());
        assertEquals(1, dto.repNumber());
        assertNotNull(dto.id());
        assertTrue(dto.metrics().isEmpty());
        assertTrue(dto.resources().isEmpty());
    }
}
