package com.example.submarines.helpers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.submarines.buttons.GameButton;
import com.example.submarines.model.Shape;

public class ButtonDrawer implements ShapeDrawingStrategy {
    @Override
    public void draw(Shape shape, Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setAlpha(180);
        p.setStyle(Paint.Style.FILL);
        canvas.drawRect(shape.getX(), shape.getY(), shape.getX() + shape.getWidth(),
                shape.getY() + shape.getHeight(), p);

        p.setAlpha(255);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(10);
        p.setColor(Color.BLACK);
        canvas.drawRect(shape.getX(), shape.getY(),
                shape.getX() + shape.getWidth(), shape.getY() + shape.getHeight(), p);


        p = new Paint();
        canvas.drawBitmap(((GameButton)shape).getBitmap(), shape.getX(), shape.getY(), p);
    }
}
