package com.example.submarines.model;

import com.example.submarines.helpers.SquareDrawer;

public class Square extends Shape {
    private Submarine submarine;
    private boolean isOccupied = false;

    public Square(int x, int y, int w, int h) {
        super(x, y, w, h);
        drawingStrategy = new SquareDrawer();
    }

    public boolean didUserTouchMe(int xu, int yu)
    {
        return xu >= x && xu <= x + width &&
                yu >= y && yu <= y + height;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(Submarine submarine) {
        isOccupied = submarine != null;
        this.submarine = submarine;
    }

    public Submarine getOccupiedSubmarine() {
        return this.submarine;
    }
}


