package com.biolab.launchpad.internal.service;

public interface SessionNotificationService {
    void broadcastRep(Integer sessionId, Object payload);
}
