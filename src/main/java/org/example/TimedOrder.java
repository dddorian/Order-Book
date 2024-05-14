package org.example;

import java.time.LocalDateTime;
import java.util.Objects;

public class TimedOrder {

    private final Order order;
    private final LocalDateTime creationTime;

    public TimedOrder(Order order, LocalDateTime creationTime) {
        Objects.requireNonNull(order, "The order must not be null");
        Objects.requireNonNull(creationTime, "The order creation time must not be null");

        this.order = order;
        this.creationTime = creationTime;
    }

    public Order getOrder() {
        return order;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }
}
