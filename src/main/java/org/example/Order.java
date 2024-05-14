package org.example;

import java.util.Objects;

public class Order {
    private long id;
    private char side;
    private double price;
    private long size;

    public Order(long id, char side, double price, long size) {
        Objects.requireNonNull(id, "The order must have an id");
        Objects.requireNonNull(side, "The order must have a side");
        if(side != 'B' && side != 'O') throw new IllegalArgumentException("the order's side is invalid");

        this.id = id;
        this.side = side;
        this.price = price;
        this.size = size;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSide(char side) {
        this.side = side;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getId() {
        return id;
    }

    public char getSide() {
        return side;
    }

    public double getPrice() {
        return price;
    }

    public long getSize() {
        return size;
    }
}
