package com.example.submarines.boards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import com.example.submarines.FireBaseStore;
import com.example.submarines.helpers.ShapeDrawingStrategy;
import com.example.submarines.helpers.SquareDrawer;
import com.example.submarines.helpers.SubmarineDrawer;
import com.example.submarines.model.GameModel;
import com.example.submarines.model.Square;
import com.example.submarines.model.Submarine;
import java.util.ArrayList;

public class BoardGame extends View {

    protected GameModel model = GameModel.getInstance();
    protected Square[][] boardPlayer1, boardPlayer2;
    protected ArrayList<Submarine> submarineArrayList;
    public static final int NUM_OF_SQUARES = 6;
    private boolean firstTimeBoard = true;
    private boolean firstTimeSubmarine = true;
    protected Submarine s1, s2, s3, s4;

    public BoardGame(Context context) {
        super(context);
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
            submarine.updateLocation(x, y);
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
                boardPlayer1[i][j].setState(Square.SquareState.EMPTY);
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

    private void updateSubmarineAndBoard(Submarine submarine, int x, int y) {

        if(isInsideSubmarinesBoard(x,y)) {
            for (Square[] squares : boardPlayer1) {
                for (int j = 0; j < boardPlayer1.length; j++) {
                    if (squares[j].didUserTouchMe(x, y)) {
                        if (squares[j].getState() == Square.SquareState.EMPTY ||
                                squares[j].getState() != Square.SquareState.OCCUPIED_BY_SUBMARINE
                                && squares[j].getOccupiedSubmarine() == submarine) {
                            submarine.updateLocation(squares[j].getX(), squares[j].getY());
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
                    boardPlayer1[i][j].setOccupiedSubmarine(submarine);
                    boardPlayer1[i][j].setState(Square.SquareState.OCCUPIED_BY_SUBMARINE);
                    model.setSquareState(i, j, Square.SquareState.OCCUPIED_BY_SUBMARINE);
                } else if (submarine.intersectsWith(boardPlayer1[i][j])) {
                    //boardPlayer1[i][j].setOccupied(submarine);
                    boardPlayer1[i][j].setState(Square.SquareState.OCCUPIED_BY_SUBMARINE_SURROUND);
                    model.setSquareState(i, j, Square.SquareState.OCCUPIED_BY_SUBMARINE_SURROUND);
                } else {
                    if (boardPlayer1[i][j].getOccupiedSubmarine() == submarine) {
                        boardPlayer1[i][j].setOccupiedSubmarine(null);
                        model.setSquareState(i, j, Square.SquareState.EMPTY);
                    }
                }
            }
        }
    }

    protected void initSubmarines() {
        submarineArrayList = GameModel.getInstance().initPlayer1Submarines();
        ShapeDrawingStrategy drawingStrategy = new SubmarineDrawer(this.getContext());

        s1 = new Submarine(boardPlayer1[5][1].getX(), boardPlayer1[5][1].getY() + Square.SQUARE_SIZE * 2,
                Square.SQUARE_SIZE, Square.SQUARE_SIZE * 2, drawingStrategy);
        submarineArrayList.add(s1);

        s2 = new Submarine(boardPlayer1[5][2].getX(), boardPlayer1[5][2].getY() + Square.SQUARE_SIZE * 2,
                Square.SQUARE_SIZE, Square.SQUARE_SIZE * 2, drawingStrategy);
        submarineArrayList.add(s2);

        s3 = new Submarine(boardPlayer1[5][3].getX(), boardPlayer1[5][3].getY() + Square.SQUARE_SIZE * 2,
                Square.SQUARE_SIZE, Square.SQUARE_SIZE * 3, drawingStrategy);
        submarineArrayList.add(s3);

        s4 = new Submarine(boardPlayer1[5][4].getX(), boardPlayer1[5][4].getY() + Square.SQUARE_SIZE * 2,
                Square.SQUARE_SIZE, Square.SQUARE_SIZE * 4, drawingStrategy);
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
        boardPlayer1 = GameModel.getInstance().initPlayer1SubmarinesBoard();
        ShapeDrawingStrategy drawingStrategy = new SquareDrawer(this.getContext());

        Square.SQUARE_SIZE = canvas.getWidth() / 3 / NUM_OF_SQUARES + 95;
        int x1 = 0;
        int y1 = 0;
        int w1 = Square.SQUARE_SIZE;

        for (int i = 0; i < boardPlayer1.length; i++) {
            for (int j = 0; j < NUM_OF_SQUARES; j++) {
                boardPlayer1[i][j] = new Square(x1, y1, w1, w1, drawingStrategy);
                x1 = x1 + w1;
            }
            x1 = 0;
            y1 = y1 + w1;
        }
    }

    private void initBoard2() {
        boardPlayer2 = GameModel.getInstance().initPlayer1FireBoard();
        ShapeDrawingStrategy drawingStrategy = new SquareDrawer(this.getContext());

        int x2 = 0;
        int y2 = boardPlayer1[5][5].getY() + 250;
        int w2 = Square.SQUARE_SIZE;

        for (int i = 0; i < boardPlayer2.length; i++) {
            for (int j = 0; j < NUM_OF_SQUARES; j++) {
                boardPlayer2[i][j] = new Square(x2, y2, w2, w2, drawingStrategy);
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
        for (int i = 0; i < boardPlayer2.length; i++) {
            for (int j = 0; j < boardPlayer2.length; j++) {
                if (boardPlayer2[i][j].didUserTouchMe(x, y)) {
                    if(boardPlayer1[i][j].getState() == Square.SquareState.OCCUPIED_BY_SUBMARINE) {
                        boardPlayer2[i][j].setState(Square.SquareState.OCCUPIED_BY_SUBMARINE_AND_HIT);
                        boardPlayer1[i][j].setState(Square.SquareState.OCCUPIED_BY_SUBMARINE_AND_HIT);
                    } else {
                        boardPlayer2[i][j].setState(Square.SquareState.MISS);
                        boardPlayer1[i][j].setState(Square.SquareState.MISS);
                    }
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
