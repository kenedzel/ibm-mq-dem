package com.ken.ibmmq.ibmmq.model;

public class Order {

    private String id;
    private String name;
    private String order;

    public String getId() {
        return id;
    }

    public Order setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Order setName(String name) {
        this.name = name;
        return this;
    }

    public String getOrder() {
        return order;
    }

    public Order setOrder(String order) {
        this.order = order;
        return this;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", order='" + order + '\'' +
                '}';
    }
}
