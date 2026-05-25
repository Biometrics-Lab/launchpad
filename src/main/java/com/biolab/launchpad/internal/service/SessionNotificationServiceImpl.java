package com.biolab.launchpad.internal.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class SessionNotificationServiceImpl implements SessionNotificationService {

    @Override
    public void broadcastRep(Integer sessionId, Object payload) {
        log.info("Rep broadcast [session={}]: {}", sessionId, payload);
    }
}
