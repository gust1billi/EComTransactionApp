package com.example.ecomtransactionapp.recycler_view;

public class Stub {
    String name;
    int id, qty, price;

    public Stub(String name, int id, int qty, int price) {
        this.name = name;
        this.id = id;
        this.qty = qty;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getQty() {
        return qty;
    }

    public int getPrice() {
        return price;
    }
}
