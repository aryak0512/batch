package org.aryak.batch.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Order(
        Long id,
        String name,
        LocalDateTime orderDate
) {
}
