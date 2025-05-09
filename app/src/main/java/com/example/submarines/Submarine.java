package com.example.submarines;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.ArrayList;

public class Submarine extends Shape {

    private boolean visibale = true;
    private ArrayList<Square> occupiedSquares = new ArrayList<>();

    public Submarine(int x, int y, int width, int height)
    {
        super(x, y, width, height);
    }

    public void updateOccupiedSquares(ArrayList<Square> squares)
    {
        occupiedSquares = squares;
    }

    public boolean isVisibale()
    {
        return visibale;
    }

    public void setVisibale(boolean visibale)
    {
        this.visibale = visibale;
    }


    public boolean isDestroyed() {
        for (int i = 0; i < occupiedSquares.size(); i++)
        {
            if (occupiedSquares.get(i).getState() != Square.OCCUPIED_BY_SUBMARINE_AND_HIT) {
                return false;
            }
        }
        return true;
    }

    public void draw(Canvas canvas, Context context)
    {
        int width = this.getWidth();
        int height = this.getHeight();

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.submarine_vertical);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        canvas.drawBitmap(scaledBitmap, this.getX(), this.getY(), null);
    }
}



