package com.orderbook;


public interface OrderHandler {
    void addOrder(Order order);

    void modifyOrder(OrderModification orderModification);

    void removeOrder(long orderId);

    double getCurrentPrice(String symbol, int quantity, Side side);

    static OrderHandler createInstance() {
        return new OrderHandlerImpl(Instrument.MSFT);
    }
}
