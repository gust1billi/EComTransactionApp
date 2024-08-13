package com.example.ecomtransactionapp.recycler_view;

public class Transaction {
    int id, price;

    public Transaction(int id, int price) {
        this.id = id;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }
}
