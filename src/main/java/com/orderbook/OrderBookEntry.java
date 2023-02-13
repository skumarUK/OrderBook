package com.orderbook;

import java.time.Instant;

public class OrderBookEntry {

    private OrderBookEntry previous;

    private OrderBookEntry next;

    private Order currentOrder;

    private Instant creationTime;

    private  OrderLevel orderLevel;

    public OrderBookEntry (Order currentOrder, OrderLevel parentOrderLevel) {
        this.currentOrder = currentOrder;
        this.orderLevel = parentOrderLevel;
        this.creationTime = Instant.now();
    }

    public OrderBookEntry getPrevious() {
        return previous;
    }

    public OrderBookEntry getNext() {
        return next;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public OrderLevel getOrderLevel() {
        return orderLevel;
    }

    public void setPrevious(OrderBookEntry previous) {
        this.previous = previous;
    }

    public void setNext(OrderBookEntry next) {
        this.next = next;
    }
}
