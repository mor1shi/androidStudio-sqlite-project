package com.example.shop_app;

public class Product {
    public int id;
    public String name;
    public double price;
    public int sku;
    public String category;
    public String status;
    public int quantity;
    public String img;

    public Product() {}

    public Product(int id, String name, double price, int sku, String category,
                   String status, int quantity, String img) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.sku = sku;
        this.category = category;
        this.status = status;
        this.quantity = quantity;
        this.img = img;
    }

    public Product(String name, double price, int sku, String category,
                   String status, int quantity, String img) {
        this(0, name, price, sku, category, status, quantity, img);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getSku() { return sku; }
    public String getCategory() { return category; }
    public String getStatus() { return status; }
    public int getQuantity() { return quantity; }
    public String getImg() { return img; }
}