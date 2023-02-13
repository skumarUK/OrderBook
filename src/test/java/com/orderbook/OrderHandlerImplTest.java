package com.orderbook;


import org.junit.Before;
import org.junit.Test;

import java.text.DecimalFormat;


import static com.orderbook.Instrument.MSFT;
import static java.lang.Double.parseDouble;
import static org.junit.Assert.*;

public class OrderHandlerImplTest {

    private OrderHandlerImpl orderHandler ;
    private DecimalFormat decimalFormat = new DecimalFormat("#.###");

    @Before
    public void setup(){
        orderHandler = new OrderHandlerImpl(MSFT);
    }

    @Test
    public void testCurrentPrice_ask() {
        ExampleData.buildExampleOrderBookFromReadMe(orderHandler);
        double currentPrice_1 = orderHandler.getCurrentPrice("MSFT", 6, Side.SELL);
        double currentPrice_2 = orderHandler.getCurrentPrice("MSFT", 17, Side.SELL);
        double currentPrice_3 = orderHandler.getCurrentPrice("MSFT", 30, Side.SELL);
        assertEquals(19.0d,currentPrice_1,0.000);
        assertEquals(19.588d,parseDouble(decimalFormat.format(currentPrice_2)),0.000);
        assertEquals(20.233d,parseDouble(decimalFormat.format(currentPrice_3)),0.000);
    }

    @Test
    public void testCurrentPrice_bid() {
        ExampleData.buildExampleOrderBookFromReadMe(orderHandler);
        double currentPrice = orderHandler.getCurrentPrice("MSFT", 10, Side.BUY);
        assertEquals(15.0d,currentPrice,0.000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidOrder() {
        orderHandler.addOrder(new Order(1L, "AAPL", Side.SELL, 19, 8));
    }

    @Test
    public void testAskOrder() {
        orderHandler.addOrder(new Order(1L, "MSFT", Side.SELL, 19, 8));
        assertEquals(1,orderHandler.getAskOrderLevels().size());
    }

    @Test
    public void testMultipleAskOrdersAtSamePrice() {
        orderHandler.addOrder(new Order(6L, "MSFT", Side.SELL, 13, 5));
        orderHandler.addOrder(new Order(2L, "MSFT", Side.SELL, 13, 10));
        assertEquals(1,orderHandler.getAskOrderLevels().size());
        assertEquals(2,orderHandler.getAskOrderLevels().get(13d).getOrderCount());
        assertEquals(15,orderHandler.getAskOrderLevels().get(13d).getLevelOrderQuantity());
    }

    @Test
    public void testMultipleAskOrdersAtDifferentPrice() {
        orderHandler.addOrder(new Order(6L, "MSFT", Side.SELL, 13, 5));
        orderHandler.addOrder(new Order(2L, "MSFT", Side.SELL, 14, 10));
        assertEquals(2,orderHandler.getAskOrderLevels().size());
        assertEquals(1,orderHandler.getAskOrderLevels().get(13d).getOrderCount());
        assertEquals(5,orderHandler.getAskOrderLevels().get(13d).getLevelOrderQuantity());
        assertEquals(1,orderHandler.getAskOrderLevels().get(14d).getOrderCount());
        assertEquals(10,orderHandler.getAskOrderLevels().get(14d).getLevelOrderQuantity());
    }

    @Test
    public void testBidOrder() {
        orderHandler.addOrder(new Order(6L, "MSFT", Side.BUY, 13, 5));
        assertEquals(1,orderHandler.getBidOrderLevels().size());
    }

    @Test
    public void testMultipleBidOrdersAtSamePrice() {
        orderHandler.addOrder(new Order(6L, "MSFT", Side.BUY, 13, 5));
        orderHandler.addOrder(new Order(2L, "MSFT", Side.BUY, 13, 10));
        assertEquals(1,orderHandler.getBidOrderLevels().size());
        assertEquals(2,orderHandler.getBidOrderLevels().get(13d).getOrderCount());
        assertEquals(15,orderHandler.getBidOrderLevels().get(13d).getLevelOrderQuantity());
    }

    @Test
    public void testMultipleBidOrdersAtDifferentPrice() {
        orderHandler.addOrder(new Order(6L, "MSFT", Side.BUY, 13, 5));
        orderHandler.addOrder(new Order(2L, "MSFT", Side.BUY, 14, 10));
        assertEquals(2,orderHandler.getBidOrderLevels().size());
        assertEquals(1,orderHandler.getBidOrderLevels().get(13d).getOrderCount());
        assertEquals(5,orderHandler.getBidOrderLevels().get(13d).getLevelOrderQuantity());
        assertEquals(1,orderHandler.getBidOrderLevels().get(14d).getOrderCount());
        assertEquals(10,orderHandler.getBidOrderLevels().get(14d).getLevelOrderQuantity());
    }

    @Test
    public void testModifyOrder() {
        orderHandler.addOrder(new Order(6L, "MSFT", Side.BUY, 13, 5));
        orderHandler.modifyOrder(new OrderModification(6L, 15,  14));
        assertEquals(1,orderHandler.getBidOrderLevels().size());
        assertSame(null,orderHandler.getBidOrderLevels().get(13d));
        assertEquals(1,orderHandler.getBidOrderLevels().get(15d).getOrderCount());
        assertEquals(14,orderHandler.getBidOrderLevels().get(15d).getLevelOrderQuantity());
        assertEquals(15d,orderHandler.getBidOrderLevels().get(15d).getPrice(),0.00d);

    }

    @Test
    public void testModifyOrder_1() {
        orderHandler.addOrder(new Order(6L, "MSFT", Side.BUY, 13, 5));
        orderHandler.addOrder(new Order(7L, "MSFT", Side.BUY, 13, 10));
        assertEquals(1,orderHandler.getBidOrderLevels().size());
        assertEquals(15,orderHandler.getBidOrderLevels().get(13d).getLevelOrderQuantity());
        assertEquals(2,orderHandler.getBidOrderLevels().get(13d).getOrderCount());
        orderHandler.modifyOrder(new OrderModification(6L, 15,  14));
        assertEquals(2,orderHandler.getBidOrderLevels().size());
        assertEquals(10,orderHandler.getBidOrderLevels().get(13d).getLevelOrderQuantity());
        assertEquals(1,orderHandler.getBidOrderLevels().get(13d).getOrderCount());
        assertEquals(14,orderHandler.getBidOrderLevels().get(15d).getLevelOrderQuantity());
        assertEquals(1,orderHandler.getBidOrderLevels().get(15d).getOrderCount());
        assertEquals(15d,orderHandler.getBidOrderLevels().get(15d).getPrice(),0.00d);
    }

    @Test
    public void testRemoveOrder() {
        orderHandler.addOrder(new Order(6L, "MSFT", Side.BUY, 13, 5));
        assertEquals(1,orderHandler.getBidOrderLevels().size());
        assertEquals(1,orderHandler.getBidOrderLevels().get(13d).getOrderCount());
        assertEquals(5,orderHandler.getBidOrderLevels().get(13d).getLevelOrderQuantity());
        orderHandler.removeOrder(6L);
        assertEquals(0,orderHandler.getBidOrderLevels().size());
        assertSame(null,orderHandler.getBidOrderLevels().get(13d));
    }
}