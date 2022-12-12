package com.ken.ibmmq.ibmmq.model;

public class Device {

    public Long id;

    public String name;

    public Long getId() {
        return id;
    }

    public Device setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Device setName(String name) {
        this.name = name;
        return this;
    }
}
