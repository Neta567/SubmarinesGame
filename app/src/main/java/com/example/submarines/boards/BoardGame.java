package com.example.submarines.boards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.submarines.FireBaseStore;
import com.example.submarines.model.GameModel;
import com.example.submarines.model.Square;
import com.example.submarines.model.Submarine;

import java.util.ArrayList;

public class BoardGame extends View {
    protected GameModel model = GameModel.getInstance();
    protected Square[][] boardPlayer1, boardPlayer2;
    protected TextView[][] boardTextViewPlayer1, boardTextViewPlayer2;
    protected ArrayList<Submarine> submarineArrayList;
    protected final int NUM_OF_SQUARES = 6;
    private boolean firstTimeBoard = true;
    private boolean firstTimeSubmarine = true;
    protected Submarine s1, s2, s3, s4;

    public BoardGame(Context context) {
        super(context);

        boardPlayer1 = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];
        boardTextViewPlayer1 = new TextView[NUM_OF_SQUARES][NUM_OF_SQUARES];

        boardPlayer2 = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];
        boardTextViewPlayer2 = new TextView[NUM_OF_SQUARES][NUM_OF_SQUARES];

        submarineArrayList = new ArrayList<>();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            handleActionDownEvent(x, y);
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            handleActionMoveEvent(x, y);
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            handleActionUpEvent(x, y);
        }
        return true;
    }

    private void handleActionUpEvent(int x, int y) {
        Submarine submarine = GameModel.getInstance().getCurrentSubmarine();
        if (submarine != null) {
            updateSubmarineAndBoard(submarine, x, y);
        }
        invalidate();
    }

    private void handleActionMoveEvent(int x, int y) {
        Submarine submarine = GameModel.getInstance().getCurrentSubmarine();
        if (submarine != null) {
            updateSubmarineLocation(submarine, x, y);
        }
        invalidate();
    }

    protected void handleActionDownEvent(int x, int y) {
        if(GameModel.getInstance().isGameStarted()) {
            markSquare(x, y);
        } else {
            if (s1.didUserTouchedMe(x, y)) {
                GameModel.getInstance().setCurrentSubmarine(s1);
            }
            if (s2.didUserTouchedMe(x, y)) {
                GameModel.getInstance().setCurrentSubmarine(s2);
            }
            if (s3.didUserTouchedMe(x, y)) {
                GameModel.getInstance().setCurrentSubmarine(s3);
            }
            if (s4.didUserTouchedMe(x, y)) {
                GameModel.getInstance().setCurrentSubmarine(s4);
            }
        }
    }

    public boolean validateCanStartTheGame() {
        boolean check = true;
        for (Submarine submarine : submarineArrayList) {
            check &= submarine.isPlacedOnBoard();
        }
        return check;
    }

    public void resetBoard() {
        for (int i = 0; i < boardPlayer1.length; i++) {
            for (int j = 0; j < boardPlayer1.length; j++) {
                boardPlayer1[i][j].setOccupied(null);
                model.setSquareState(i, j, Square.SquareState.EMPTY);
            }
        }
        for (Submarine submarine : submarineArrayList) {
            submarine.reset();
        }
        FireBaseStore.INSTANCE.saveGame(model);
        invalidate();
    }

    public void rotateSubmarine() {
        Submarine submarine = GameModel.getInstance().getCurrentSubmarine();
        if (submarine != null) {
            submarine.rotateBitmap();
            updateSubmarineAndBoard(submarine, submarine.getX(), submarine.getY());
            invalidate();
        }

    }

    private void updateSubmarineLocation(Submarine submarine, int x, int y) {
        submarine.setX(x);
        submarine.setY(y);
    }


    private void updateSubmarineAndBoard(Submarine submarine, int x, int y) {

        if(isInsideSubmarinesBoard(x,y)) {
            for (Square[] squares : boardPlayer1) {
                for (int j = 0; j < boardPlayer1.length; j++) {
                    if (squares[j].didUserTouchMe(x, y)) {
                        if (!squares[j].isOccupied()) {
                            updateSubmarineLocation(submarine, squares[j].getX(), squares[j].getY());
                        } else {
                            submarine.reset();
                        }
                    }
                }
            }
            updateOccupiedSquares(submarine);
            FireBaseStore.INSTANCE.saveGame(model);
        } else {
            submarine.reset();
        }
    }

    private boolean isInsideSubmarinesBoard(int x, int y) {
        return isInsideBoard(boardPlayer1, x, y);
    }

    private boolean isInsideBoard(Square[][] boardPlayer, int x, int y) {

        return x >= boardPlayer[0][0].getX() && x <= boardPlayer[0][NUM_OF_SQUARES - 1].getX() + Square.SQUARE_SIZE
                && y >= boardPlayer[0][0].getY() && y <= boardPlayer[NUM_OF_SQUARES - 1][0].getY() + Square.SQUARE_SIZE;
    }

    private void updateOccupiedSquares(Submarine submarine) {
        for (int i = 0; i < boardPlayer1.length; i++) {
            for (int j = 0; j < boardPlayer1.length; j++) {
                if (submarine.strictIntersectsWith(boardPlayer1[i][j])) {
                    //todo: bind board to model
                    boardPlayer1[i][j].setOccupied(submarine);
                    model.setSquareState(i, j, Square.SquareState.OCCUPIED_BY_SUBMARINE);
                } else if (submarine.intersectsWith(boardPlayer1[i][j])) {
                    boardPlayer1[i][j].setOccupied(submarine);
                    model.setSquareState(i, j, Square.SquareState.OCCUPIED_BY_SUBMARINE_SURROUND);
                } else {
                    if (boardPlayer1[i][j].getOccupiedSubmarine() == submarine) {
                        boardPlayer1[i][j].setOccupied(null);
                        model.setSquareState(i, j, Square.SquareState.EMPTY);
                    }
                }
            }
        }
    }

    protected void initSubmarines() {
        s1 = new Submarine(boardPlayer1[5][1].getX(), boardPlayer1[5][0].getY() + Square.SQUARE_SIZE * 2, this.getResources(), Square.SQUARE_SIZE, Square.SQUARE_SIZE * 2);
        submarineArrayList.add(s1);

        s2 = new Submarine(boardPlayer1[5][2].getX(), boardPlayer1[5][0].getY() + Square.SQUARE_SIZE * 2, this.getResources(), Square.SQUARE_SIZE, Square.SQUARE_SIZE * 2);
        submarineArrayList.add(s2);

        s3 = new Submarine(boardPlayer1[5][3].getX(), boardPlayer1[5][0].getY() + Square.SQUARE_SIZE * 2, this.getResources(), Square.SQUARE_SIZE, Square.SQUARE_SIZE * 3);
        submarineArrayList.add(s3);

        s4 = new Submarine(boardPlayer1[5][4].getX(), boardPlayer1[5][0].getY() + Square.SQUARE_SIZE * 2, this.getResources(), Square.SQUARE_SIZE, Square.SQUARE_SIZE * 4);
        submarineArrayList.add(s4);
    }

    protected void initBoards(@NonNull Canvas canvas) {
        if (firstTimeBoard) {
            initBoard1(canvas);
            initBoard2();
            firstTimeBoard = false;
        }
    }

    protected void drawBoard(Square[][] boardPlayer, Canvas canvas) {
        for (Square[] squares : boardPlayer) {
            for (int j = 0; j < boardPlayer.length; j++) {
                squares[j].draw(canvas);
            }
        }
    }

    public void initBoard1(Canvas canvas) {
        Square.SQUARE_SIZE = canvas.getWidth() / 3 / NUM_OF_SQUARES + 95;
        int x1 = 0;
        int y1 = 50;
        int w1 = Square.SQUARE_SIZE;

        for (int i = 0; i < boardPlayer1.length; i++) {
            for (int j = 0; j < NUM_OF_SQUARES; j++) {
                boardPlayer1[i][j] = new Square(x1, y1, w1, w1);
                x1 = x1 + w1;
            }
            x1 = 0;
            y1 = y1 + w1;
        }
    }

    private void initBoard2() {
        int x2 = 0;
        int y2 = boardPlayer1[5][5].getY() + 250;
        int w2 = Square.SQUARE_SIZE;

        for (int i = 0; i < boardPlayer2.length; i++) {
            for (int j = 0; j < NUM_OF_SQUARES; j++) {
                boardPlayer2[i][j] = new Square(x2, y2, w2, w2);
                x2 = x2 + w2;
            }
            x2 = 0;
            y2 = y2 + w2;
        }
    }

    public void drawSubmarines(Canvas layout) {
        if (firstTimeSubmarine) {
            initSubmarines();
            firstTimeSubmarine = false;
        }
        for (int i = 0; i < submarineArrayList.size(); i++) {
            submarineArrayList.get(i).draw(layout);
        }
    }

    public void markSquare(int x, int y) {
        for (Square[] squares : boardPlayer2) {
            for (int j = 0; j < boardPlayer2.length; j++) {
                if (squares[j].didUserTouchMe(x, y)) {
                    squares[j].setFired();
                }
            }
        }
    }

    public void setupBoard() {
        updateSubmarineAndBoard(s1, boardPlayer1[0][0].getX(), boardPlayer1[0][0].getY());
        updateSubmarineAndBoard(s2, boardPlayer1[3][0].getX(), boardPlayer1[3][0].getY());
        updateSubmarineAndBoard(s3, boardPlayer1[3][3].getX(), boardPlayer1[3][3].getY());
        updateSubmarineAndBoard(s4, boardPlayer1[0][5].getX(), boardPlayer1[0][5].getY());
    }
}
