package org.aryak.batch.util;

import lombok.extern.slf4j.Slf4j;
import org.aryak.batch.model.DomainEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventLogListener {

    @EventListener
    public void onDomainEvent(DomainEvent event) {
        // send the event to a DB or msg queue
        log.info("Received domain event: {}, payload: {}", event.eventName(), event.payload());
    }
}
