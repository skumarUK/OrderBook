package com.orderbook;


public class OrderModification {
    private final long orderId;
    private final int newPrice;
    private final int newQuantity;

    public OrderModification(long orderId, int newPrice, int newQuantity) {
        this.orderId = orderId;
        this.newPrice = newPrice;
        this.newQuantity = newQuantity;
    }

    public long getOrderId() {
        return orderId;
    }

    public int getNewPrice() {
        return newPrice;
    }

    public int getNewQuantity() {
        return newQuantity;
    }

    public Order toOrder(Order order){
        return new Order(order.getOrderId(),
                order.getSymbol(),order.getSide(),
                this.getNewPrice(),this.getNewQuantity());
    }
}
