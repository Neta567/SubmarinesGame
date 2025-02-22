package com.example.submarines;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class RotationButton extends Square {

    Bitmap bitmap;

    public RotationButton(int x, int y, int w, int h, int color, Bitmap bitmap) {
        super(x, y, w, h, color);
        this.bitmap = bitmap;
        this.bitmap = Bitmap.createScaledBitmap(bitmap,w,h,true);
        setPaintAlphaColor(255);
    }

    public boolean didUserTouchMe(int xu, int yu)
    {
        return xu >= x && xu <= x + width && yu >= y && yu <= y + height;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        Paint p = new Paint();
        canvas.drawBitmap(bitmap,x,y,p);
    }
}
