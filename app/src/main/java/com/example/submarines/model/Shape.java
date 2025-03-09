package com.example.submarines.model;

import android.graphics.Canvas;

import com.example.submarines.helpers.ShapeDrawingStrategy;

public class Shape extends Location {
    protected int width;
    protected int height;

    protected ShapeDrawingStrategy drawingStrategy;

    public Shape(int x, int y, int width, int height, ShapeDrawingStrategy drawingStrategy) {
        super(x,y);
        this.width = width;
        this.height = height;
        this.drawingStrategy = drawingStrategy;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void draw(Canvas canvas) {
        drawingStrategy.draw(this, canvas);
    }
}
