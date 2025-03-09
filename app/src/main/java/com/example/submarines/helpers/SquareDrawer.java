package com.example.submarines.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.example.submarines.GameActivity;
import com.example.submarines.R;
import com.example.submarines.model.Shape;
import com.example.submarines.model.Square;

public class SquareDrawer implements ShapeDrawingStrategy {


    private final Context context;

    public SquareDrawer(Context context) {
        this.context = context;
    }

    public void draw(Shape shape, Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setAlpha(180);
        p.setStyle(Paint.Style.FILL);
        canvas.drawRect(shape.getX(), shape.getY(), shape.getX() + shape.getWidth(),
                shape.getY() + shape.getHeight(), p);

        p.setAlpha(255);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(10);
        p.setColor(Color.BLACK);

        boolean isHit = false;
        if (shape instanceof Square) {
            Square square = (Square) shape;
            switch (square.getState()) {
                case OCCUPIED_BY_SUBMARINE:
                    p.setColor(Color.RED);
                    break;
                case OCCUPIED_BY_SUBMARINE_SURROUND:
                    p.setColor(Color.YELLOW);
                    break;
                case OCCUPIED_BY_SUBMARINE_AND_HIT:
                    p.setColor(Color.GREEN);
                    isHit = true;
                    break;
                case MISS:
                    p.setColor(Color.BLUE);
                    break;
            }
        }
        if (isHit) {
            String boomKey = "boom";
            if (((GameActivity) context).getBitmapFromMemCache(boomKey) == null) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.boom);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, Square.SQUARE_SIZE, Square.SQUARE_SIZE, true);
                ((GameActivity) context).addBitmapToMemoryCache(boomKey, scaledBitmap);
            }
            Bitmap boomBitmap = ((GameActivity) context).getBitmapFromMemCache(boomKey);
            canvas.drawBitmap(boomBitmap, shape.getX(), shape.getY(), p);
        } else {
            canvas.drawRect(shape.getX(), shape.getY(),
                    shape.getX() + shape.getWidth(), shape.getY() + shape.getHeight(), p);
        }
    }
}
