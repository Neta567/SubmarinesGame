package com.example.submarines.model;

public class Shape extends Location {
    protected int width;
    protected int height;

    public Shape(int x, int y, int width, int height) {
        super(x,y);
        this.width = width;
        this.height = height;
    }
}
