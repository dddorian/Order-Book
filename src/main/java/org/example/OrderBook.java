package org.example;


import java.time.LocalDateTime;
import java.util.*;

import static java.util.Map.entry;

public class OrderBook {

    private final Map<Long, TimedOrder> orderRegister; // Timed orders by ID
    private final Map<Double, PriorityQueue<TimedOrder>> bids; // Bids sorted by price, then time
    private final Map<Double, PriorityQueue<TimedOrder>> offers; // Offers sorted by price, then time
    private final Map<Character, Map<Double, PriorityQueue<TimedOrder>>> orderCategoryBySide;
    private static final Comparator<TimedOrder> TIMED_ORDER_COMPARATOR = Comparator.comparing(TimedOrder::getCreationTime);

    public OrderBook() {
        orderRegister = new HashMap<>();
        bids = new TreeMap<>(Collections.reverseOrder());
        offers = new TreeMap<>();
        orderCategoryBySide = Map.ofEntries(entry('B', bids), entry('O', offers));

    }

    public void addOrder(Order order) {
        TimedOrder timedOrder = new TimedOrder(order, LocalDateTime.now());
        Map<Double, PriorityQueue<TimedOrder>> sideOrdersByPrice = orderCategoryBySide.get(order.getSide());
        PriorityQueue<TimedOrder> levelOrders
                = sideOrdersByPrice.computeIfAbsent(order.getPrice(), k -> new PriorityQueue<>(TIMED_ORDER_COMPARATOR));
        levelOrders.offer(timedOrder);
        orderRegister.put(order.getId(), timedOrder);
    }

    public void removeOrder(long orderId) {
        TimedOrder timedOrder = orderRegister.get(orderId);
        if (timedOrder != null) {
            Order order = timedOrder.getOrder();
            Map<Double, PriorityQueue<TimedOrder>> sideOrdersByPrice = orderCategoryBySide.get(order.getSide());
            PriorityQueue<TimedOrder> levelOrders = sideOrdersByPrice.get(order.getPrice());
            levelOrders.remove(timedOrder);
            if (levelOrders.isEmpty()) {
                sideOrdersByPrice.remove(order.getPrice());
            }
            orderRegister.remove(orderId);
        }
    }

    public void modifyOrder(long orderId, long newSize) {
        TimedOrder timedOrder = orderRegister.get(orderId);
        if (timedOrder != null) {
            removeOrder(orderId);
            Order order = timedOrder.getOrder();
            order.setSize(newSize);
            addOrder(order);
        }
    }

    public double getPriceForLevel(char side, int level) {
        if(level == 0) throw new IllegalArgumentException("The level value must be a strictly positive integer");

        Map<Double, PriorityQueue<TimedOrder>> sideOrdersByPrice = orderCategoryBySide.get(side);

        if (level > sideOrdersByPrice.size()) {
            throw new IllegalArgumentException("The order book contains less than " + level + " levels");
        }

        Set<Double> priceSet = sideOrdersByPrice.keySet();
        Double[] priceArray = priceSet.toArray(new Double[priceSet.size()]);
        return priceArray[level -1];
    }

    public long getSizeAtLevel(char side, int level) {

        Map<Double, PriorityQueue<TimedOrder>> sideOrdersByPrice = orderCategoryBySide.get(side);

        if (level > sideOrdersByPrice.size()) {
            throw new IllegalArgumentException("The order book contains less than " + level + "levels");
        }

        Double price = getPriceForLevel(side, level);
        return sideOrdersByPrice.get(price).stream()
                .map(TimedOrder::getOrder)
                .mapToLong(Order::getSize)
                .sum();
    }

    public List<Order> getOrdersForSide(char side) {

        Map<Double, PriorityQueue<TimedOrder>> sideOrdersByPrice = orderCategoryBySide.get(side);
        if(sideOrdersByPrice != null) {
            return sideOrdersByPrice.entrySet().stream()
                    .map(entry -> entry.getValue())
                    .flatMap(q -> q.stream())
                    .map(TimedOrder::getOrder)
                    .toList();
        }
        return Collections.emptyList();

    }
}
