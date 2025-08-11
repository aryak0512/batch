package org.aryak.batch.listeners;

import lombok.extern.slf4j.Slf4j;
import org.aryak.batch.file.Order;
import org.aryak.batch.model.TrackedOrder;
import org.springframework.batch.core.SkipListener;

/**
 * inject logic to dump to database or log the skipped items
 */
@Slf4j
public class CustomSkipListener implements SkipListener<Order, TrackedOrder> {

    @Override
    public void onSkipInRead(Throwable t) {
        log.info("Skipped reading order: {}", t.getMessage());
    }

    @Override
    public void onSkipInWrite(TrackedOrder item, Throwable t) {
        log.info("Skipped writing order: {}, due to: {}", item.getItemId(), t.getMessage());
    }

    @Override
    public void onSkipInProcess(Order item, Throwable t) {
        log.info("Skipped processing order: {}, due to: {}", item.getItemId(), t.getMessage());
    }
}
