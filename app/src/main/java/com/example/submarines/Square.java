package com.example.submarines;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.HashMap;
import java.util.Map;

public class Square extends Shape {

    public enum SquareState { // מצבים של ריבועים
        EMPTY(0),
        OCCUPIED_BY_SUBMARINE(1),
        OCCUPIED_BY_SUBMARINE_SURROUND(2),
        OCCUPIED_BY_SUBMARINE_AND_HIT(3),
        MISS(4);

        private static final Map<Integer, SquareState> map = new HashMap<>(); // יוצר מאפ של אינט ואינמ עבור כל מצב ריבוע

        static {
            for (SquareState state : SquareState.values()) { // מייצר מפה של כל המצבים של הריבוע יחד עם האינט של האינמ עצמו
                map.put(state.value, state);
            }
        }

        private final int value; // מייצר ערך מספרי לאינמ

        SquareState(int value) {
            this.value = value;
        } // משנה ערך עבור אינמ

        public static SquareState fromValue(int i) {
            return map.get(i);
        } // מחזיר אינמ עבור מספר

        public int getValue() {
            return value;
        }
    }

    public static int SQUARE_SIZE;
    private Submarine submarine;
    private SquareState state = SquareState.EMPTY; // בהתחלה כולם ריקים
    private final BitmapCache bitmapCache = new BitmapCache();



    public Square(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public boolean didUserTouchMe(int xu, int yu) {
        return xu >= x && xu <= x + width &&
                yu >= y && yu <= y + height;
    }

    public void setOccupiedSubmarine(Submarine submarine) { // כל ריבוע יודע איזה צוללת נמצאת מעליו
        this.submarine = submarine;
    }

    public Submarine getOccupiedSubmarine() {
        return this.submarine;
    }

    public void setState(SquareState state) {
        this.state = state;
    }

    public SquareState getState() {
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

        String hitMissKey = null;
        int resId = R.drawable.boom;

        switch (this.getState()) { // נעבור על כל המצבים של הריבועים ונראה מה מתאים לריבוע שרוצים לצייר
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
        if (hitMissKey != null) { // אם צריך לצייר בום או איקס -
            if (bitmapCache.getBitmapFromMemCache(hitMissKey) == null) { // אם אין ביטמאפ כזה עוד -
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId); // תכניס לביטמאפ את מה שנכון לפי האידי
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, Square.SQUARE_SIZE - 80, Square.SQUARE_SIZE - 80, true); // תיצור אותו בגודל נכון
                bitmapCache.addBitmapToMemoryCache(hitMissKey, scaledBitmap); // תוסיף אותו כמשהו קיים כבר לפעם הבאה
            }
            // אם קיים כבר ביטמאפ כזה אז -
            Bitmap boomBitmap = bitmapCache.getBitmapFromMemCache(hitMissKey); //אותו הדבר
            canvas.drawBitmap(boomBitmap, this.getX() + 40, this.getY() + 40, p);
        } else { //אם סתם צריך לצייר ריבוע -
            canvas.drawRect(this.getX(), this.getY(),
                            this.getX() + this.getWidth(), this.getY() + this.getHeight(), p);
        }
    }

}



