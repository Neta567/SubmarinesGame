package com.example.submarines.model;

import static com.example.submarines.model.Square.SQUARE_SIZE;
import com.example.submarines.helpers.ShapeDrawingStrategy;

import java.util.ArrayList;

public class Submarine extends Shape {
    private final int initialX;
    private final int initialY;
    private boolean isVertical = true;
    ArrayList<Square> occupiedSquares = new ArrayList<>();

    public Submarine(int x, int y, int width, int height, ShapeDrawingStrategy drawingStrategy) {
        super(x, y, width, height, drawingStrategy);
        initialX = x;
        initialY = y;
    }

    public void rotateShape()
    {
        isVertical = !isVertical;
    }

    public boolean didUserTouchedMe (int xu, int yu)
    {
        return xu >= x && xu < x + getWidth() &&
                yu >= y && yu < y + getHeight();
    }

    public boolean intersectsWith(Square square) {
        boolean result = square.getX() >= this.x - SQUARE_SIZE && square.getX() <= this.x + width + SQUARE_SIZE
                && square.getY() >= this.y - SQUARE_SIZE && square.getY() <= this.y + SQUARE_SIZE;

        if(isVertical) {
            result = square.getX() >= this.x - SQUARE_SIZE && square.getX() <= this.x + SQUARE_SIZE
                    && square.getY() >= this.y - SQUARE_SIZE && square.getY() < this.y + height + SQUARE_SIZE;
        }
        return result;
    }
    public boolean strictIntersectsWith(Square square) {
        boolean result = this.x + height > square.getX() &&  this.x <= square.getX()
                && this.y >= square.getY() &&  this.y < square.getY() + SQUARE_SIZE;

        if(isVertical) {
            result = this.x >= square.getX() && this.x < square.getX() + SQUARE_SIZE
                    && this.y + height > square.getY() && this.y <= square.getY();
        }
        return result;
    }

    public void reset() {
        this.x = initialX;
        this.y = initialY;
    }

    public boolean isPlacedOnBoard() {
        return this.x != initialX || this.y != initialY;
    }

    public boolean isVertical() {
        return isVertical;
    }

    public void updateOccupiedSquares(ArrayList<Square> squares) {
        occupiedSquares = squares;
    }

    public boolean isDestroyed() {
        return occupiedSquares.stream()
                .allMatch(square ->
                        square.getState() == Square.SquareState.OCCUPIED_BY_SUBMARINE_AND_HIT);
    }
}
