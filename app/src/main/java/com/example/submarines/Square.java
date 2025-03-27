package com.example.submarines;

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

    public Square(int x, int y, int w, int h, ShapeDrawingStrategy drawingStrategy) {
        super(x, y, w, h, drawingStrategy);
    }

    public boolean didUserTouchMe(int xu, int yu)
    {
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
}


