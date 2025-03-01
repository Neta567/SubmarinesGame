package com.example.submarines.buttons;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.submarines.model.Square;

public abstract class GameButton extends Square {
    Bitmap bitmap;

    public GameButton(int width, int height, int color, Bitmap bitmap) {
        this(0, 0, width, height, color, bitmap);
    }
    public GameButton(int x, int y, int w, int h, int color, Bitmap bitmap) {

        super(x, y, w, h, color);
        this.bitmap = Bitmap.createScaledBitmap(bitmap,w,h,true);
        setPaintAlphaColor(255);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        Paint p = new Paint();
        canvas.drawBitmap(bitmap, x, y, p);
    }
}
