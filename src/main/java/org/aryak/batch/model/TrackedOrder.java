package org.aryak.batch.model;

import org.aryak.batch.file.Order;
import org.springframework.beans.BeanUtils;


public class TrackedOrder extends Order {

    private String trackingNumber;
    private boolean freeShipping;

    public TrackedOrder(Order order) {
        BeanUtils.copyProperties(order, this);
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public void setFreeShipping(boolean freeShipping) {
        this.freeShipping = freeShipping;
    }

    public boolean isFreeShipping() {
        return freeShipping;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }
}
