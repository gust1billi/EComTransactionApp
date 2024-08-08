package com.example.ecomtransactionapp.recycler_view;

public class Product {
    String product_name;
    String product_code;
    String price;
    String imageUri;

    public Product(String product_name, String product_code, String price) {
        this.product_name = product_name;
        this.product_code = product_code;
        this.price = price;
        this.imageUri = "";
    }

    public Product(String product_name, String price){
        this.product_name = product_name;
        this.product_code = "";
        this.price = price;
        this.imageUri = "";
    }

    public String getProductName() {
        return product_name;
    }

    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    public String getProductCode() {
        return product_code;
    }

    public void setProductCode(String product_code) {
        this.product_code = product_code;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

}
