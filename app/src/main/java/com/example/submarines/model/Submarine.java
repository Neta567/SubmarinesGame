package com.example.submarines.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.submarines.R;

public class Submarine extends Shape {
    private final Bitmap vertBitmap;
    private final Bitmap horizBitmap;
    private final int initialX;
    private final int initialY;
    private boolean isVertical = true;
    private Bitmap bitmap;

    private final int squareSize = 140;

    public Submarine(int x, int y, Resources resources, int width, int height) {
        super(x, y, width, height);
        initialX = x;
        initialY = y;

        Bitmap tempBitmap = BitmapFactory.decodeResource(resources, R.drawable.submarine_vertical);
        this.vertBitmap = Bitmap.createScaledBitmap(tempBitmap, width, height,true);

        tempBitmap = BitmapFactory.decodeResource(resources,R.drawable.submarine_horizontal);
        this.horizBitmap = Bitmap.createScaledBitmap(tempBitmap, height, width,true);

        this.bitmap = vertBitmap;
    }

    public void rotateBitmap()
    {
        if(isVertical)
        {
            this.bitmap = horizBitmap;
            isVertical = false;
        }
        else
        {
            this.bitmap = vertBitmap;
            isVertical = true;
        }
    }


    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(bitmap,x,y,null);

        // draw rectangle around submarine
        if(false) {
            Paint p = new Paint();
            p.setAlpha(255);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(10);
            p.setColor(Color.RED);
            canvas.drawRect(x - squareSize, y - squareSize,
                    x + width + squareSize, y + height + squareSize, p);
        }
    }
    public boolean didUserTouchedMe (int xu, int yu)
    {
        return xu >= x && xu < x + bitmap.getWidth() &&
                yu >= y && yu < y + bitmap.getHeight();
    }

    public boolean isVertical() {
        return isVertical;
    }

    public int getSize() {
        return isVertical ? height : width;
    }

    public boolean intersectsWith(Square square) {
        boolean result = square.getX() >= this.x - squareSize && square.getX() <= this.x + width + squareSize
                && square.getY() >= this.y - squareSize && square.getY() <= this.y + squareSize;

        if(isVertical) {
            result = square.getX() >= this.x - squareSize && square.getX() <= this.x + squareSize
                    && square.getY() >= this.y - squareSize && square.getY() < this.y + height + squareSize;
        }
        return result;
    }
    public boolean strictIntersectsWith(Square square) {
        boolean result = square.getX() > this.x - squareSize && square.getX() < this.x + squareSize + width
                && square.getY() > this.y - squareSize && square.getY() < this.y + squareSize;

        if(isVertical) {
            result = square.getX() + squareSize > this.x && square.getX() - squareSize < this.x
                    && square.getY() + squareSize > this.y && square.getY()  - height < this.y;
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
