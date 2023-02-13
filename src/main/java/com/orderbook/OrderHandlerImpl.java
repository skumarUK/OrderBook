package com.orderbook;

import java.util.*;

public class OrderHandlerImpl implements OrderHandler {

    private final SortedMap<Double, OrderLevel> askOrderLevels = new TreeMap<>();
    private final SortedMap<Double, OrderLevel> bidOrderLevels = new TreeMap<>(Collections.reverseOrder());
    private final Map<Long, OrderBookEntry> orderBookEntries = new HashMap<>();
    private final Instrument instrument;

    public OrderHandlerImpl(Instrument instrument){
        this.instrument = instrument;
    }

    public Map<Double, OrderLevel> getAskOrderLevels(){
        return askOrderLevels;
    }

    public Map<Double, OrderLevel> getBidOrderLevels(){
        return bidOrderLevels;
    }

    private boolean isValidOrder(Order order){
        return instrument.toString().equals(order.getSymbol());
    }

    @Override
    public void addOrder(Order order) {
        if(!isValidOrder(order)) throw new IllegalArgumentException("Invalid order for this order book " + order.getSymbol() +order.getOrderId());
        OrderLevel orderLevel = new OrderLevel(order.getPrice());
        addOrder(order, orderLevel, order.isBuySide() ? bidOrderLevels : askOrderLevels);
    }

    private void addOrder(Order order, OrderLevel baseLevel, SortedMap<Double, OrderLevel> orderLevels) {
        OrderBookEntry orderBookEntry = new OrderBookEntry(order, baseLevel);
        OrderLevel orderLevel = orderLevels.get(baseLevel.getPrice());
        if (orderLevel != null) {
            if (orderLevel.getHead() == null) {
                orderLevel.setHead(orderBookEntry);
                orderLevel.setTail(orderBookEntry);
            } else {
                OrderBookEntry tailPointer = orderLevel.getTail();
                tailPointer.setNext(orderBookEntry);
                orderBookEntry.setPrevious(tailPointer);
                orderLevel.setTail(orderBookEntry);
            }
            orderLevels.put(baseLevel.getPrice(), orderLevel);
        } else {
            baseLevel.setHead(orderBookEntry);
            baseLevel.setTail(orderBookEntry);
            orderLevels.put(baseLevel.getPrice(), baseLevel);
        }
        orderBookEntries.put(order.getOrderId(), orderBookEntry);
    }

    @Override
    public void modifyOrder(OrderModification orderModification) {
        final OrderBookEntry orderBookEntry = orderBookEntries.get(orderModification.getOrderId());
        if (orderBookEntry != null) {
            final Order order = orderBookEntry.getCurrentOrder();
            this.removeOrder(order.getOrderId());
            this.addOrder(orderModification.toOrder(order));
        }
    }

    @Override
    public void removeOrder(long orderId) {
        final OrderBookEntry orderBookEntry = orderBookEntries.get(orderId);
        if (orderBookEntry != null) {
            // Deal with the location of OrderBookEntry
            if (orderBookEntry.getPrevious() != null && orderBookEntry.getNext() != null) {
                orderBookEntry.getNext().setPrevious(orderBookEntry.getPrevious());
                orderBookEntry.getPrevious().setNext(orderBookEntry.getNext());
            } else if (orderBookEntry.getPrevious() != null) {
                orderBookEntry.getPrevious().setNext(null);
            } else if (orderBookEntry.getNext() != null) {
                orderBookEntry.getNext().setPrevious(null);
            }

            //Deal with OrderBookEntry on Limit-Level
            if (orderBookEntry.getOrderLevel().getHead() == orderBookEntry
                    && orderBookEntry.getOrderLevel().getTail() == orderBookEntry) {
                // One order on this level.
                orderBookEntry.getOrderLevel().setHead(null);
                orderBookEntry.getOrderLevel().setTail(null);
            } else if (orderBookEntry.getOrderLevel().getHead() == orderBookEntry) {
                // More than one order but this order entry is first on level.
                orderBookEntry.getOrderLevel().setHead(orderBookEntry.getNext());
            } else if (orderBookEntry.getOrderLevel().getTail() == orderBookEntry) {
                // More than one order but this order entry is last on level.
                orderBookEntry.getOrderLevel().setTail(orderBookEntry.getPrevious());
            }
            orderBookEntries.remove(orderId);
            SortedMap<Double, OrderLevel> orderLevels = orderBookEntry.getCurrentOrder().isBuySide()?bidOrderLevels:askOrderLevels;
            OrderLevel orderLevel = orderLevels.get(orderBookEntry.getOrderLevel().getPrice());
            if(orderLevel.getHead()==null && orderLevel.getTail()==null)orderLevels.remove(orderLevel.getPrice());
        }
    }

    @Override
    public double getCurrentPrice(String symbol, int quantity, Side side) {
        final SortedMap<Double, OrderLevel> orderLevels = side==Side.BUY? bidOrderLevels : askOrderLevels;
        final Iterator<Map.Entry<Double,OrderLevel> > iterator = orderLevels.entrySet().iterator();
        double totalPrice =0.0d;
        int remQuantity = quantity;
        while(iterator.hasNext()){
            OrderLevel orderLevel = iterator.next().getValue();
            int orderLevelQuantity = orderLevel.getLevelOrderQuantity();
            if(remQuantity>orderLevelQuantity){
               remQuantity -= orderLevelQuantity;
               totalPrice += orderLevel.getPrice()*orderLevelQuantity;
            }else{
               totalPrice += orderLevel.getPrice()*remQuantity;
               break;
            }
        }
        return totalPrice/quantity;
    }

}
