package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.web.dto.RepBroadcastDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionNotificationServiceImpl implements SessionNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void broadcastRep(Integer sessionId, RepBroadcastDto payload) {
        messagingTemplate.convertAndSend("/topic/session/" + sessionId + "/reps", payload);
    }
}
