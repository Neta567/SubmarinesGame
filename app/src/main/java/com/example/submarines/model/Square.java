package com.example.submarines.model;

import android.content.res.Resources;

import com.example.submarines.helpers.SquareDrawer;

public class Square extends Shape {



    public enum SquareState {
        EMPTY(0),
        OCCUPIED_BY_SUBMARINE(1),
        OCCUPIED_BY_SUBMARINE_SURROUND(2),
        OCCUPIED_BY_SUBMARINE_AND_HIT(3),
        OCCUPIED_BY_SUBMARINE_SURROUND_AND_HIT(4),
        MISS(5),
        HIT(6);

        private final int value;

        SquareState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static int SQUARE_SIZE;
    private Submarine submarine;
    private SquareState state = SquareState.EMPTY;

    public Square(int x, int y, int w, int h, Resources resources) {
        super(x, y, w, h);
        drawingStrategy = new SquareDrawer(resources);
    }

    public boolean didUserTouchMe(int xu, int yu)
    {
        return xu >= x && xu <= x + width &&
                yu >= y && yu <= y + height;
    }

    public void setOccupiedSubmarine(Submarine submarine) {
        boolean isOccupied = submarine != null;
        this.submarine = submarine;
        if(isOccupied) {
            state = SquareState.OCCUPIED_BY_SUBMARINE;
        } else {
            state = SquareState.EMPTY;
        }
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


