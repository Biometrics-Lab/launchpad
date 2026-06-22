package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.web.dto.RepBroadcastDto;

public interface SessionNotificationService {
    void broadcastRep(Integer sessionId, RepBroadcastDto payload);
}
