package com.example.submarines.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.example.submarines.R;
import com.example.submarines.model.Shape;
import com.example.submarines.model.Square;

public class SquareDrawer implements ShapeDrawingStrategy {

    // מימוש של האינטרפייס...

    private final Context context;

    private final BitmapCache bitmapCache = new BitmapCache();

    public SquareDrawer(Context context) {
        this.context = context;
    }

    public void draw(Shape shape, Canvas canvas) { // ציור ריבועים על המסך
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

        String hitMissKey = null;
        int resId = R.drawable.boom;
        if (shape instanceof Square) { // אם הצורה זה ריבוע
            Square square = (Square) shape;
            switch (square.getState()) { // נעבור על כל המצבים של הריבועים ונראה מה מתאים לריבוע שרוצים לצייר
                case OCCUPIED_BY_SUBMARINE: // לצורך דיבאג
                    //p.setColor(Color.RED);
                    break;
                case OCCUPIED_BY_SUBMARINE_SURROUND: // לצורך דיבאג
                    //p.setColor(Color.YELLOW);
                    break;
                case OCCUPIED_BY_SUBMARINE_AND_HIT: // נשים בסטרינג שצריך להיות שם בום
                    hitMissKey = "boom";
                    break;
                case MISS: // נשים בסטרינג שצריך להיות שם איקס
                    hitMissKey = "miss";
                    resId = R.drawable.miss_icon;
                    break;
            }
        }
        if (hitMissKey != null) { // אם צריך לצייר בום או איקס -
            if (bitmapCache.getBitmapFromMemCache(hitMissKey) == null) { // אם אין ביטמאפ כזה עוד -
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId); // תכניס לביטמאפ את מה שנכון לפי האידי
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, Square.SQUARE_SIZE-80, Square.SQUARE_SIZE-80, true); // תיצור אותו בגודל נכון
                bitmapCache.addBitmapToMemoryCache(hitMissKey, scaledBitmap); // תוסיף אותו כמשהו קיים כבר לפעם הבאה
            }
            // אם קיים כבר ביטמאפ כזה אז -
            Bitmap boomBitmap = bitmapCache.getBitmapFromMemCache(hitMissKey); //אותו הדבר
            canvas.drawBitmap(boomBitmap, shape.getX()+40, shape.getY()+40, p);
        } else { //אם סתם צריך לצייר ריבוע -
            canvas.drawRect(shape.getX(), shape.getY(),
                    shape.getX() + shape.getWidth(), shape.getY() + shape.getHeight(), p);
        }
    }
}
