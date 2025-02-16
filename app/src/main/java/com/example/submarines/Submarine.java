package com.example.submarines;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Submarine extends Shape {
    private Bitmap vertBitmap, horizBitmap;
    private boolean isVertical = true;
    private Bitmap bitmap;

    private int squareSize = 140;

    public Submarine(int x, int y, Resources resources, int width, int height) {
        super(x, y, width, height);

        Bitmap tempBitmap = BitmapFactory.decodeResource(resources,R.drawable.submarine_vertical);
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
        if(xu >= x && xu < x + bitmap.getWidth() &&
                yu >=y && yu < y+bitmap.getHeight())
            return true;
        return false;
    }

    public boolean isVertical() {
        return isVertical;
    }

    public int getSize() {
        return isVertical ? height : width;
    }

    public boolean intersectsWith(Square square) {
        if(square.getX() >= this.x - squareSize && square.getX() < this.x + width + squareSize
                && square.getY() >= this.y - squareSize && square.getY() < this.y + height + squareSize)
            return true;
        else
            return false;
    }
}
