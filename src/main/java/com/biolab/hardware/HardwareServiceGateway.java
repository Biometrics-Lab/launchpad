package com.biolab.hardware;

import com.biolab.common.AsyncRequest;
import com.biolab.common.Dictionary;
import com.biolab.common.HealthCheckRequest;
import com.biolab.common.HealthCheckResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class HardwareServiceGateway {

    public HealthCheckResponse healthCheck(HealthCheckRequest request) {
        log.info("{} - health check - OK: {}", this.getClass().getSimpleName(), request);
        return HealthCheckResponse.builder()
                .requestId(request.requestId())
                .status("OK")
                .build();
    }

    @ApplicationModuleListener(condition = "'hardware-service'.equals(#event.consumerServiceName)")
    public void asyncEvent(AsyncRequest event) {
        log.info("{} - async event received - OK: {}", this.getClass().getSimpleName(), event);
    }
}
