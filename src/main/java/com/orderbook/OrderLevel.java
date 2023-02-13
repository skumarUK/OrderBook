package com.orderbook;

import java.util.ArrayList;
import java.util.List;

public class OrderLevel {

    private double price;

    private int orderCount;

    private OrderBookEntry head;

    private OrderBookEntry tail;

    private double averagePrice;

    OrderLevel(double price){
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public OrderBookEntry getHead() {
        return head;
    }

    public OrderBookEntry getTail() {
        return tail;
    }

    public int getOrderCount(){
        return this.getLevelOrderRecords().size();
    }

    public List<Order> getLevelOrderRecords ()
    {
        List<Order> orders = new ArrayList<>();
        OrderBookEntry headPointer = head;
        while (headPointer!=null){
            Order currentOrder = headPointer.getCurrentOrder();
            if (currentOrder.getQuantity() != 0) {
                orders.add(currentOrder);
            }
            headPointer = headPointer.getNext();
        }
        return orders;
    }

    public int getLevelOrderQuantity () {
        int quantity =0;
        OrderBookEntry headPointer = head;
        while (headPointer!=null){
            quantity+= headPointer.getCurrentOrder().getQuantity();
            headPointer = headPointer.getNext();
        }
        return quantity;
    }

    public void setHead(OrderBookEntry head) {
        this.head = head;
    }

    public void setTail(OrderBookEntry tail) {
        this.tail = tail;
    }

}
