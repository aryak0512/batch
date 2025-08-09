package org.aryak.batch.service;

import lombok.extern.slf4j.Slf4j;
import org.aryak.batch.annotations.EventSource;
import org.aryak.batch.annotations.LogTimeTaken;
import org.aryak.batch.annotations.Retry;
import org.aryak.batch.model.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static org.aryak.batch.util.Constants.ORDER_CREATED;

@Slf4j
@Service
public class JobService {

    @LogTimeTaken
    public void m1() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Retry(times = 2, delay = 1000)
    public void m2() {

        log.info("Trying to make external API call");

        // simulate an external API call failure
        throw new RuntimeException("External API call failed");
    }

    @EventSource(name = ORDER_CREATED)
    @Retry(times = 3, delay = 1000)
    public Order m3() {

        return Order.builder()
                .id(1L)
                .name("Order 1")
                .orderDate(LocalDateTime.now())
                .build();

    }
}
