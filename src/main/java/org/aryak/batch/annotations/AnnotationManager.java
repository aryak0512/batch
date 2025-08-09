package org.aryak.batch.annotations;

import lombok.extern.slf4j.Slf4j;
import org.aryak.batch.model.DomainEvent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AnnotationManager {

    private final ApplicationEventPublisher publisher;

    public AnnotationManager(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Around("@annotation(logTimeTaken)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogTimeTaken logTimeTaken) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            String methodName = joinPoint.getSignature().getName();
            log.info("Time taken for method {} : {} ms", methodName, duration);
        }
    }

    @Around("@annotation(retry)")
    public Object onRetry(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {
        int attempts = retry.times();
        Throwable lastException = null;

        for (int i = 0; i < attempts; i++) {
            try {
                Thread.sleep(retry.delay());
                return joinPoint.proceed();
            } catch (Exception e) {
                lastException = e;
                log.warn("Attempt {} failed for method {}: {}", i + 1, joinPoint.getSignature().getName(), e.getMessage());
            }
        }

        log.error("All {} attempts failed for method {}", attempts, joinPoint.getSignature().getName());
        throw lastException; // rethrow the last exception
    }

    @Around("@annotation(eventSource)")
    public Object onEventSource(ProceedingJoinPoint joinPoint, EventSource eventSource) throws Throwable {

        String eventName = eventSource.name();
        Object payload = joinPoint.proceed();
        DomainEvent event = DomainEvent.builder().eventName(eventName).payload(payload).timestamp(LocalDateTime.now()).build();

        log.info("Publishing domain event : {} on queue : {}", event, eventSource.name());
        publisher.publishEvent(event);
        return payload;
    }
}
