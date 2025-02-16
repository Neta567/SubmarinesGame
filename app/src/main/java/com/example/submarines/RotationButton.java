package com.example.submarines;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class RotationButton extends Square {

    Bitmap bitmap;
    //Paint p;



    public RotationButton(int x, int y, int w, int h, int color, Bitmap bitmap) {
        super(x, y, w, h, color);
        this.bitmap = bitmap;
        this.bitmap = Bitmap.createScaledBitmap(bitmap,w,h,true);
        setP(255);
    }


    public boolean didUserTouchMe(int xu, int yu)
    {
        if (xu>=x && xu<=x+ width && yu>=y && yu<=y+ height)
            return true;
        return false;
    }

    @Override
    public void draw1(Canvas canvas) {
        super.draw1(canvas);

        Paint p = new Paint();
        canvas.drawBitmap(bitmap,x,y,p);
    }
}
