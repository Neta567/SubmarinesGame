package com.example.submarines;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Square extends Shape {
    protected int color;
    private Paint p;
    private int alfa;
    private Submarine submarine;

    private boolean isOccupied = false;

    public Square(int x, int y, int w, int h, int color) {
        super(x, y, w, h);
        this.color = color;
        alfa = 100;

        p = new Paint();
        p.setColor(color);
        setP(alfa);
    }

    protected void setP(int a) {
        p.setAlpha(a);
    }

    public Paint getP() {
        return p;
    }

    public void draw1(Canvas canvas) {
        p.setColor(color);
        setP(alfa);
        p.setStyle(Paint.Style.FILL);

        canvas.drawRect(x, y, x + width, y + height, p);
        setP(255);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(10);
        p.setColor(Color.BLACK);
        if(isOccupied) {
            p.setColor(Color.RED);
        }
        canvas.drawRect(x, y, x + width, y + height, p);
    }
    public boolean didUserTouchMe(int xu, int yu)
    {
        if (xu>=x && xu<=x+ width &&
                yu>=y && yu<=y+ height) {
            return true;
        }
        return false;
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


