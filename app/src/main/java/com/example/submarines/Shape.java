package com.example.submarines;

import android.graphics.Canvas;

public class Shape extends Location {

    // יורשים ממנה ריבוע וצוללת
    // אם רוצים לצייר אותה אז נפעיל אסטרטגיה שמתאים לצורה שלה
    protected int width;
    protected int height;

    public Shape(int x, int y, int width, int height) {
        super(x,y);
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


}
