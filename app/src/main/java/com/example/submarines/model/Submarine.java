package com.example.submarines.model;

import static com.example.submarines.model.Square.SQUARE_SIZE;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.submarines.GameActivity;
import com.example.submarines.R;

public class Submarine extends Shape {
    private final int initialX;
    private final int initialY;
    private final Context context;
    private boolean isVertical = true;
    private Bitmap bitmap;

    public Submarine(int x, int y, int width, int height, Context context) {
        super(x, y, width, height);
        initialX = x;
        initialY = y;

        this.context = context;
    }

    public void rotateBitmap()
    {
        isVertical = !isVertical;
    }

    public void draw(Canvas canvas)
    {
        if(isVertical) {
            String key = "submarine_vertical_" + height;
            if (((GameActivity) context).getBitmapFromMemCache(key) == null) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.submarine_vertical);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                ((GameActivity) context).addBitmapToMemoryCache(key, scaledBitmap);
            }
            this.bitmap = ((GameActivity) context).getBitmapFromMemCache(key);
        } else {
            String key = "submarine_horizontal_" + height;
            if (((GameActivity) context).getBitmapFromMemCache(key) == null) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.submarine_vertical);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, height, width, true);
                ((GameActivity) context).addBitmapToMemoryCache(key, scaledBitmap);
            }
            this.bitmap = ((GameActivity) context).getBitmapFromMemCache(key);
        }
        canvas.drawBitmap(bitmap, x, y, null);

        // draw rectangle around submarine
        if(false) {
            Paint p = new Paint();
            p.setAlpha(255);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(10);
            p.setColor(Color.RED);
            canvas.drawRect(x - SQUARE_SIZE, y - SQUARE_SIZE,
                    x + width + SQUARE_SIZE, y + height + SQUARE_SIZE, p);
        }
    }
    public boolean didUserTouchedMe (int xu, int yu)
    {
        return xu >= x && xu < x + bitmap.getWidth() &&
                yu >= y && yu < y + bitmap.getHeight();
    }

    public boolean intersectsWith(Square square) {
        boolean result = square.getX() >= this.x - SQUARE_SIZE && square.getX() <= this.x + width + SQUARE_SIZE
                && square.getY() >= this.y - SQUARE_SIZE && square.getY() <= this.y + SQUARE_SIZE;

        if(isVertical) {
            result = square.getX() >= this.x - SQUARE_SIZE && square.getX() <= this.x + SQUARE_SIZE
                    && square.getY() >= this.y - SQUARE_SIZE && square.getY() < this.y + height + SQUARE_SIZE;
        }
        return result;
    }
    public boolean strictIntersectsWith(Square square) {
        boolean result = this.x + height > square.getX() &&  this.x <= square.getX()
                && this.y >= square.getY() &&  this.y < square.getY() + SQUARE_SIZE;

        if(isVertical) {
            result = this.x >= square.getX() && this.x < square.getX() + SQUARE_SIZE
                    && this.y + height > square.getY() && this.y <= square.getY();
        }
        return result;
    }

    public void reset() {
        this.x = initialX;
        this.y = initialY;
    }

    public boolean isPlacedOnBoard() {
        return this.x != initialX || this.y != initialY;
    }
}
