package com.example.submarines.helpers;

import static com.example.submarines.model.Square.SQUARE_SIZE;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.submarines.GameActivity;
import com.example.submarines.R;
import com.example.submarines.model.Shape;
import com.example.submarines.model.Submarine;

public class SubmarineDrawer implements ShapeDrawingStrategy {

    private final Context context;

    public SubmarineDrawer(Context context) {
        this.context = context;
    }

    public void draw(Shape shape, Canvas canvas) {
        if (shape instanceof Submarine) {
            Submarine submarine = (Submarine) shape;
            String key;
            int resId;
            int width = submarine.getWidth();
            int height = submarine.getHeight();

            if (submarine.isVertical()) {
                key = "submarine_vertical_" + submarine.getHeight();
                resId = R.drawable.submarine_vertical;
            } else {
                key = "submarine_horizontal_" + submarine.getWidth();
                resId = R.drawable.submarine_horizontal;
                width = submarine.getHeight();
                height = submarine.getWidth();
            }

            if (((GameActivity) context).getBitmapFromMemCache(key) == null) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,width, height, true);
                ((GameActivity) context).addBitmapToMemoryCache(key, scaledBitmap);
            }

            Bitmap submarineBitmap = ((GameActivity) context).getBitmapFromMemCache(key);
            canvas.drawBitmap(submarineBitmap, submarine.getX(), submarine.getY(), null);

            // draw rectangle around submarine
            if (false) {
                Paint p = new Paint();
                p.setAlpha(255);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(10);
                p.setColor(Color.RED);
                canvas.drawRect(submarine.getX() - SQUARE_SIZE, submarine.getY() - SQUARE_SIZE,
                        submarine.getX() + submarine.getWidth() + SQUARE_SIZE,
                        submarine.getY() + submarine.getHeight() + SQUARE_SIZE, p);
            }
        }
    }
}
