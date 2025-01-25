package com.example.submarines;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Submarine extends Location {
    private final int width;
    private final int height;
    private Bitmap vertBitmap, horizBitmap;
    private boolean isVertical = true;
    private Bitmap bitmap;
    private int x,y;

    public Submarine(int x, int y, Resources resources, int width, int height) {
        this.x=x;
        this.y=y;
        this.width = width;
        this.height = height;

        Bitmap tempBitmap = BitmapFactory.decodeResource(resources,R.drawable.submarine_vertical);
        this.vertBitmap = Bitmap.createScaledBitmap(tempBitmap, width, height,true);

        tempBitmap = BitmapFactory.decodeResource(resources,R.drawable.submarine_horizontal);
        this.horizBitmap = Bitmap.createScaledBitmap(tempBitmap, height, width,true);

        this.bitmap = vertBitmap;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
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

    }
    public boolean didUserTouchedMe (int xu,int yu)
    {
        if(xu >= x && xu < x + bitmap.getWidth() && yu>=y && yu < y+bitmap.getHeight())
            return true;
        return false;
    }

    public boolean isVertical() {
        return isVertical;
    }

    public int getSize() {
//        if(isVertical)
//            return width;
//        else return height;

        return isVertical ? height : width;
    }
}
