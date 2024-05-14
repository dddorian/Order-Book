package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderBookTest {

    private OrderBook orderBook;

    @BeforeEach
    public void setUp() {
        orderBook = new OrderBook();
    }


    @Test
    public void testAddOrderWithNegativeSize() {

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Order order = new Order(1, 'B', 100.0, -10);
            orderBook.addOrder(order);
        });

        String expectedMessage = "The order's size is invalid";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testRemoveNonExistingOrderId() {
        Order order = new Order(1, 'B', 100.0, 10);
        orderBook.addOrder(order);
        orderBook.removeOrder(2);
        assertEquals(1, orderBook.getOrdersForSide('B').size());
    }

    @Test
    public void testAddOrderWithInvalidSide() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Order order = new Order(1, 'X', 100.0, 10);
            orderBook.addOrder(order);
        });

        String expectedMessage = "The order's side is invalid";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetPriceForLevelZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderBook.getPriceForLevel('B', 0);

        });

        String expectedMessage = "The level value must be a strictly positive integer";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetPriceForLevelGreaterThanLevels() {
        Order order1 = new Order(1, 'B', 100.0, 10);
        Order order2 = new Order(2, 'B', 99.0, 10);
        orderBook.addOrder(order1);
        orderBook.addOrder(order2);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderBook.getPriceForLevel('B', 3);

        });

        String expectedMessage = "The order book contains less than 3 levels";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testAddOrder_SingleOrder() {
        Order order = new Order(1, 'B', 100.0, 1000);
        orderBook.addOrder(order);

        assertEquals(1, orderBook.getOrdersForSide('B').size());
        assertEquals(100.0, orderBook.getPriceForLevel('B', 1), 0.001);
        assertEquals(1000, orderBook.getSizeAtLevel('B', 1));
    }

    @Test
    public void testRemoveOrder_SingleOrder() {
        Order order = new Order(1, 'B', 100.0, 1000);
        orderBook.addOrder(order);
        orderBook.removeOrder(1);

        assertTrue(orderBook.getOrdersForSide('B').isEmpty());
    }

    @Test
    public void testModifyOrder_SingleOrder_IncreaseSize() {
        Order order = new Order(1, 'B', 100.0, 1000);
        orderBook.addOrder(order);
        orderBook.modifyOrder(1, 2000);

        assertEquals(2000, orderBook.getSizeAtLevel('B', 1));
    }

    @Test
    public void testModifyOrder_SingleOrder_DecreaseSize() {
        Order order = new Order(1, 'B', 100.0, 1000);
        orderBook.addOrder(order);
        orderBook.modifyOrder(1, 500);

        assertEquals(500, orderBook.getSizeAtLevel('B', 1));
    }

    @Test
    public void testAddOrder_MultipleOrders() {
        // Add multiple orders
        orderBook.addOrder(new Order(1, 'B', 100.0, 100));
        orderBook.addOrder(new Order(2, 'B', 101.0, 200));
        orderBook.addOrder(new Order(3, 'O', 99.0, 300));
        orderBook.addOrder(new Order(4, 'O', 98.0, 400));

        // Check the size at level 1 for bids and offers
        assertEquals(200, orderBook.getSizeAtLevel('B', 1));
        assertEquals(100, orderBook.getSizeAtLevel('B', 2));
        assertEquals(400, orderBook.getSizeAtLevel('O', 1));
        assertEquals(300, orderBook.getSizeAtLevel('O', 2));
    }

    @Test
    public void testRemoveOrder_MultipleOrders() {
        // Add multiple orders
        orderBook.addOrder(new Order(1, 'B', 100.0, 100));
        orderBook.addOrder(new Order(2, 'B', 101.0, 200));
        orderBook.addOrder(new Order(3, 'O', 99.0, 300));
        orderBook.addOrder(new Order(4, 'O', 98.0, 400));

        // Remove an order
        orderBook.removeOrder(2);

        // Check the size at level 1 for bids and offers
        assertEquals(100, orderBook.getSizeAtLevel('B', 1));
        assertEquals(400, orderBook.getSizeAtLevel('O', 1));
        assertEquals(300, orderBook.getSizeAtLevel('O', 2));
    }

    @Test
    public void testModifyOrder_MultipleOrders() {
        // Add multiple orders
        orderBook.addOrder(new Order(1, 'B', 100.0, 100));
        orderBook.addOrder(new Order(2, 'B', 101.0, 200));
        orderBook.addOrder(new Order(3, 'O', 99.0, 300));
        orderBook.addOrder(new Order(4, 'O', 98.0, 400));

        // Modify an order
        orderBook.modifyOrder(2, 250);

        // Check the size at level 1 for bids and offers
        assertEquals(250, orderBook.getSizeAtLevel('B', 1));
        assertEquals(100, orderBook.getSizeAtLevel('B', 2));
        assertEquals(400, orderBook.getSizeAtLevel('O', 1));
        assertEquals(300, orderBook.getSizeAtLevel('O', 2));
    }

    @Test
    public void testAddOrder_RemoveOrder_ModifyOrder_MultipleOrders() {
        // Add multiple orders
        orderBook.addOrder(new Order(1, 'B', 100.0, 100));
        orderBook.addOrder(new Order(2, 'B', 101.0, 200));
        orderBook.addOrder(new Order(3, 'O', 99.0, 300));
        orderBook.addOrder(new Order(4, 'O', 98.0, 400));

        // Remove an order
        orderBook.removeOrder(2);

        // Modify an order
        orderBook.modifyOrder(3, 350);

        // Check the size at level 1 for bids and offers
        assertEquals(100, orderBook.getSizeAtLevel('B', 1));
        assertEquals(400, orderBook.getSizeAtLevel('O', 1));
        assertEquals(350, orderBook.getSizeAtLevel('O', 2));
    }

    @Test
    public void testRemoveOrder_NonExistentOrder() {
        // Add multiple orders
        orderBook.addOrder(new Order(1, 'B', 100.0, 100));
        orderBook.addOrder(new Order(2, 'B', 101.0, 200));
        orderBook.addOrder(new Order(3, 'O', 99.0, 300));
        orderBook.addOrder(new Order(4, 'O', 98.0, 400));

        // Remove a non-existent order
        orderBook.removeOrder(5);

        // Check the size at level 1 for bids and offers
        assertEquals(200, orderBook.getSizeAtLevel('B', 1));
        assertEquals(100, orderBook.getSizeAtLevel('B', 2));
        assertEquals(400, orderBook.getSizeAtLevel('O', 1));
        assertEquals(300, orderBook.getSizeAtLevel('O', 2));
    }

    @Test
    public void testGetPriceForLevel_bids_firstLevel() {
        // Given
        orderBook.addOrder(new Order(1, 'B', 10.0, 100));
        orderBook.addOrder(new Order(2, 'B', 9.5, 200));
        orderBook.addOrder(new Order(3, 'B', 9.0, 300));

        // When
        double price = orderBook.getPriceForLevel('B', 1);

        // Then
        assertEquals(10.0, price);
    }

    @Test
    public void testGetPriceForLevel_bids_secondLevel() {
        // Given
        orderBook.addOrder(new Order(1, 'B', 10.0, 100));
        orderBook.addOrder(new Order(2, 'B', 9.5, 200));
        orderBook.addOrder(new Order(3, 'B', 9.0, 300));

        // When
        double price = orderBook.getPriceForLevel('B', 2);

        // Then
        assertEquals(9.5, price);
    }

    @Test
    public void testGetPriceForLevel_offers_firstLevel() {
        // Given
        orderBook.addOrder(new Order(1, 'O', 10.0, 100));
        orderBook.addOrder(new Order(2, 'O', 9.5, 200));
        orderBook.addOrder(new Order(3, 'O', 9.0, 300));

        // When
        double price = orderBook.getPriceForLevel('O', 1);

        // Then
        assertEquals(9.0, price);
    }

    @Test
    public void testGetPriceForLevel_offers_secondLevel() {
        // Given
        orderBook.addOrder(new Order(1, 'O', 10.0, 100));
        orderBook.addOrder(new Order(2, 'O', 9.5, 200));
        orderBook.addOrder(new Order(3, 'O', 9.0, 300));

        // When
        double price = orderBook.getPriceForLevel('O', 2);

        // Then
        assertEquals(9.5, price);
    }

    @Test
    public void testGetSizeAtLevel_bids_firstLevel() {
        // Given
        orderBook.addOrder(new Order(1, 'B', 10.0, 100));
        orderBook.addOrder(new Order(2, 'B', 9.5, 200));
        orderBook.addOrder(new Order(3, 'B', 9.0, 300));

        // When
        long size = orderBook.getSizeAtLevel('B', 1);

        // Then
        assertEquals(100, size);
    }

    @Test
    public void testGetSizeAtLevel_bids_secondLevel() {
        // Given
        orderBook.addOrder(new Order(1, 'B', 10.0, 100));
        orderBook.addOrder(new Order(2, 'B', 9.5, 200));
        orderBook.addOrder(new Order(3, 'B', 9.0, 300));

        // When
        long size = orderBook.getSizeAtLevel('B', 2);

        // Then
        assertEquals(200, size);
    }

    @Test
    public void testGetSizeAtLevel_offers_firstLevel() {
        // Given
        orderBook.addOrder(new Order(1, 'O', 10.0, 100));
        orderBook.addOrder(new Order(2, 'O', 9.5, 200));
        orderBook.addOrder(new Order(3, 'O', 9.0, 300));

        // When
        long size = orderBook.getSizeAtLevel('O', 1);

        // Then
        assertEquals(300, size);
    }

    @Test
    public void testGetSizeAtLevel_offers_secondLevel() {
        // Given
        orderBook.addOrder(new Order(1, 'O', 10.0, 100));
        orderBook.addOrder(new Order(2, 'O', 9.5, 200));
        orderBook.addOrder(new Order(3, 'O', 9.0, 300));

        // When
        long size = orderBook.getSizeAtLevel('O', 2);

        // Then
        assertEquals(200, size);
    }

    @Test
    void testGetOrdersForSide_returnsAllOrdersForBidSide() {
        // Arrange
        Order bidOrder1 = new Order(1, 'B', 100.0, 100);
        Order bidOrder2 = new Order(2, 'B', 99.0, 200);
        Order offerOrder = new Order(3, 'O', 101.0, 300);

        orderBook.addOrder(bidOrder1);
        orderBook.addOrder(bidOrder2);
        orderBook.addOrder(offerOrder);

        // Act
        List<Order> bidOrders = orderBook.getOrdersForSide('B');

        // Assert
        assertEquals(2, bidOrders.size());
        assertTrue(bidOrders.contains(bidOrder1));
        assertTrue(bidOrders.contains(bidOrder2));
        assertFalse(bidOrders.contains(offerOrder));
    }

    @Test
    void testGetOrdersForSide_returnsAllOrdersForOfferSide() {
        // Arrange
        Order bidOrder = new Order(1, 'B', 100.0, 100);
        Order offerOrder1 = new Order(2, 'O', 101.0, 200);
        Order offerOrder2 = new Order(3, 'O', 102.0, 300);

        orderBook.addOrder(bidOrder);
        orderBook.addOrder(offerOrder1);
        orderBook.addOrder(offerOrder2);

        // Act
        List<Order> offerOrders = orderBook.getOrdersForSide('O');

        // Assert
        assertEquals(2, offerOrders.size());
        assertTrue(offerOrders.contains(offerOrder1));
        assertTrue(offerOrders.contains(offerOrder2));
        assertFalse(offerOrders.contains(bidOrder));
    }

    @Test
    void testGetOrdersForSide_returnsEmptyListForUnknownSide() {
        // Arrange
        Order bidOrder = new Order(1, 'B', 100.0, 100);
        Order offerOrder = new Order(2, 'O', 101.0, 200);

        orderBook.addOrder(bidOrder);
        orderBook.addOrder(offerOrder);

        // Act
        List<Order> unknownSideOrders = orderBook.getOrdersForSide('X');

        // Assert
        assertTrue(unknownSideOrders.isEmpty());
    }

    @Test
    void testGetOrdersForSide_returnsEmptyListForNoOrders() {
        // Arrange
        // No orders added to the order book

        // Act
        List<Order> bidOrders = orderBook.getOrdersForSide('B');
        List<Order> offerOrders = orderBook.getOrdersForSide('O');

        // Assert
        assertTrue(bidOrders.isEmpty());
        assertTrue(offerOrders.isEmpty());
    }







}


