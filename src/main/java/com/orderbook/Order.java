package com.orderbook;


public class Order {
    private final long orderId;
    private final String symbol;
    private final Side side;
    private final int price;
    private final int quantity;

    public Order(long orderId, String symbol, Side side, int price, int quantity) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public Side getSide() {
        return side;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isBuySide() {return getSide()==Side.BUY;}

}
