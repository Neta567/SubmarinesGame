package com.example.submarines.model;

public class Location {
    protected int x;
    protected int y;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void updateLocation(int x, int y) {
        setX(x);
        setY(y);
    }
}
