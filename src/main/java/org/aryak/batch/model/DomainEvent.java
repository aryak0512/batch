package org.aryak.batch.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DomainEvent(String eventName, Object payload, LocalDateTime timestamp) {

}
