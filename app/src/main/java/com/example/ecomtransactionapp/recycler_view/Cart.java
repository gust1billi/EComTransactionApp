package com.example.ecomtransactionapp.recycler_view;

import java.io.Serializable;

public class Cart implements Serializable {
    String name, price;
    int quantity;

    public Cart(String name, String price) {
        this.name = name;
        this.price = price;
        this.quantity = 1;
    }

    public Cart(String name, int quantity, String price) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }


    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(){
        this.quantity += 1;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
