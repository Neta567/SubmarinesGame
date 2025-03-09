package com.example.submarines.helpers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.example.submarines.model.Shape;
import com.example.submarines.model.Square;

public class SquareDrawer implements ShapeDrawingStrategy {

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

            if(shape instanceof Square) {
                Square square = (Square) shape;
                switch (square.getState()) {
                    case OCCUPIED_BY_SUBMARINE:
                        p.setColor(Color.RED);
                        break;
                    case OCCUPIED_BY_SUBMARINE_SURROUND:
                        p.setColor(Color.YELLOW);
                        break;
                    case OCCUPIED_BY_SUBMARINE_AND_HIT:
                        p.setColor(Color.GREEN);
                        break;
                    case MISS:
                        p.setColor(Color.BLUE);
                        break;
                }
            }
            canvas.drawRect(shape.getX(), shape.getY(),
                    shape.getX() + shape.getWidth(), shape.getY() + shape.getHeight(), p);
    }
}
