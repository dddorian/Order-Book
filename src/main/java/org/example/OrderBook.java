package org.example;


import java.util.*;

import static java.util.Map.entry;

public class OrderBook {

    private final Map<Long, Order> orderRegister; // Order lookup by ID
    private final Map<Double, PriorityQueue<Order>> bids; // Bids sorted by price, then time
    private final Map<Double, PriorityQueue<Order>> offers; // Offers sorted by price, then time
    private final Map<Character, Map<Double, PriorityQueue<Order>>> orderCategoryBySide;

    public OrderBook() {
        orderRegister = new HashMap<>();
        bids = new TreeMap<>(Collections.reverseOrder());
        offers = new TreeMap<>();
        orderCategoryBySide = Map.ofEntries(entry('B', bids), entry('O', offers));


    }

    public void addOrder(Order order) {
        Map<Double, PriorityQueue<Order>> side = orderCategoryBySide.get(order.getSide());
        PriorityQueue<Order> levelOrders
                = side.computeIfAbsent(order.getPrice(), k -> new PriorityQueue<>(Comparator.comparingLong(Order::getId)));
        levelOrders.offer(order);
        orderRegister.put(order.getId(), order);
    }

    public void removeOrder(long orderId) {
        Order order = orderRegister.get(orderId);
        if (order != null) {
            Map<Double, PriorityQueue<Order>> side = orderCategoryBySide.get(order.getSide());
            PriorityQueue<Order> levelOrders = side.get(order.getPrice());
            levelOrders.remove(order);
            if (levelOrders.isEmpty()) {
                side.remove(order.getPrice());
            }
            orderRegister.remove(orderId);
        }
    }

    public void modifyOrder(long orderId, long newSize) {
        Order order = orderRegister.get(orderId);
        if (order != null) {
            removeOrder(orderId);
            order.setSize(newSize);
            addOrder(order);
        }
    }

    public double getPriceAtLevel(char side, int level) {
        Map<Double, PriorityQueue<Order>> book = side == 'B' ? bids : offers;
        if (level > book.size()) {
            return Double.NaN; // Invalid level
        }
        Iterator<Double> priceIter = book.keySet().iterator();
        for (int i = 1; i < level; i++) {
            priceIter.next();
        }
        return priceIter.next();
    }

    public long getSizeAtLevel(char side, int level) {
        Map<Double, PriorityQueue<Order>> book = side == 'B' ? bids : offers;
        if (level > book.size()) {
            return 0; // Invalid level
        }
        Iterator<Double> priceIter = book.keySet().iterator();
        double price = 0;
        for (int i = 1; i <= level; i++) {
            price = priceIter.next();
        }
        return book.get(price).stream().mapToLong(Order::getSize).sum();
    }

    public List<Order> getOrdersForSide(char side) {
        Map<Double, PriorityQueue<Order>> book = side == 'B' ? bids : offers;
        List<Order> orders = new ArrayList<>();
        for (PriorityQueue<Order> queue : book.values()) {
            orders.addAll(queue);
        }
        return orders;
    }
}
