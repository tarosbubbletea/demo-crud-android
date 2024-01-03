package com.example.myapplication2;

public class Data {
    private int id;
    private String name;

    // constructor
    public Data(int id, String name){
        this.id = id;
        this.name = name;
    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}