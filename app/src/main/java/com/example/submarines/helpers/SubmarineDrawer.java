package com.example.submarines.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import com.example.submarines.R;
import com.example.submarines.model.Shape;
import com.example.submarines.model.Submarine;

public class SubmarineDrawer implements ShapeDrawingStrategy {

    private final Context context;
    private final static BitmapCache bitmapCache = BitmapCache.getInstance();

    public SubmarineDrawer(Context context) {
        this.context = context;
    }

    public void draw(Shape shape, Canvas canvas) {
        if (shape instanceof Submarine) { // אם צריך לצייר צוללת
            Submarine submarine = (Submarine) shape;
            String key; // שם של ביטמאפ
            int resId; // אידי של הביטמאפ
            int width = submarine.getWidth();
            int height = submarine.getHeight();

            if (submarine.isVertical()) { // אם אנכי -
                key = "submarine_vertical_" + submarine.getHeight();
                resId = R.drawable.submarine_vertical;
            } else {
                key = "submarine_horizontal_" + submarine.getWidth();
                resId = R.drawable.submarine_horizontal;
                width = submarine.getHeight();
                height = submarine.getWidth();
            }

            if (bitmapCache.getBitmapFromMemCache(key) == null) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,width, height, true);
                bitmapCache.addBitmapToMemoryCache(key, scaledBitmap);
            }

            Bitmap submarineBitmap = bitmapCache.getBitmapFromMemCache(key);
            canvas.drawBitmap(submarineBitmap, submarine.getX(), submarine.getY(), null);
        }
    }
}
