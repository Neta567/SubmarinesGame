package com.example.submarines.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Square extends Shape {
    protected int color;
    private final Paint p;
    private Submarine submarine;
    private boolean isOccupied = false;

    public Square(int x, int y, int w, int h, int color) {
        super(x, y, w, h);
        this.color = color;

        p = new Paint();
        p.setColor(color);
        p.setAlpha(180);
    }

    protected void setPaintAlphaColor(int alphaColor) {
        p.setAlpha(alphaColor);
    }

    public Paint getP() {
        return p;
    }

    public boolean didUserTouchMe(int xu, int yu)
    {
        return xu >= x && xu <= x + width &&
                yu >= y && yu <= y + height;
    }

    public void draw(Canvas canvas) {
        p.setColor(color);
        p.setAlpha(100);
        p.setStyle(Paint.Style.FILL);
        canvas.drawRect(x, y, x + width, y + height, p);

        p.setAlpha(255);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(10);
        p.setColor(Color.BLACK);
        if(isOccupied) {
            p.setColor(Color.RED);
        }
        canvas.drawRect(x, y, x + width, y + height, p);
    }
    public void draw2 (Canvas canvas)
    {
        p.setColor(color);
        p.setStyle(Paint.Style.FILL);
        canvas.drawRect(x, y, x + width, y + height, p);
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


