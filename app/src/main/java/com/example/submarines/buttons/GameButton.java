package com.example.submarines.buttons;

import android.graphics.Bitmap;

import com.example.submarines.helpers.ButtonDrawer;
import com.example.submarines.model.Square;

public abstract class GameButton extends Square {
    private final Bitmap bitmap;

    public GameButton(int width, int height, Bitmap bitmap) {
        this(0, 0, width, height, bitmap);
        drawingStrategy = new ButtonDrawer();
    }
    private GameButton(int x, int y, int w, int h, Bitmap bitmap) {
        super(x, y, w, h);
        this.bitmap = Bitmap.createScaledBitmap(bitmap,w,h,true);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
