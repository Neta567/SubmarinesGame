package com.example.submarines;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Square extends Shape {

    public static int EMPTY = 0;
    public static int OCCUPIED_BY_SUBMARINE = 1;
    public static int OCCUPIED_BY_SUBMARINE_SURROUND = 2;
    public static int OCCUPIED_BY_SUBMARINE_AND_HIT = 3;
    public static int MISS = 4;
    public static int SQUARE_SIZE;
    private Submarine submarine;
    private int state = EMPTY; // בהתחלה כולם ריקים

    public Square(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public boolean didUserTouchMe(int xu, int yu) {
        return xu >= x && xu <= x + width &&
                yu >= y && yu <= y + height;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    public void draw(Canvas canvas, Context context) {
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setAlpha(180);
        p.setStyle(Paint.Style.FILL);
        canvas.drawRect(this.getX(), this.getY(), this.getX() + this.getWidth(),
                this.getY() + this.getHeight(), p);

        p.setAlpha(255);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(10);
        p.setColor(Color.BLACK);

        if(state == OCCUPIED_BY_SUBMARINE_AND_HIT)
        {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.boom); // תכניס לביטמאפ את מה שנכון לפי האידי
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, SQUARE_SIZE - 80, SQUARE_SIZE - 80, true); // תיצור אותו בגודל נכון
            canvas.drawBitmap(scaledBitmap, this.getX() + 40, this.getY() + 40, p);
        }
        else if(state == MISS)
        {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.miss_icon); // תכניס לביטמאפ את מה שנכון לפי האידי
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, SQUARE_SIZE - 80, SQUARE_SIZE - 80, true); // תיצור אותו בגודל נכון
            canvas.drawBitmap(scaledBitmap, this.getX() + 40, this.getY() + 40, p);
        }
        else
        { //אם סתם צריך לצייר ריבוע -
            canvas.drawRect(this.getX(), this.getY(),
                            this.getX() + this.getWidth(), this.getY() + this.getHeight(), p);
        }
    }

}



