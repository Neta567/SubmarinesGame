package com.example.submarines.boards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.submarines.FireBaseStore;
import com.example.submarines.helpers.ShapeDrawingStrategy;
import com.example.submarines.helpers.SquareActionCallback;
import com.example.submarines.helpers.SquareDrawer;
import com.example.submarines.helpers.SubmarineDrawer;
import com.example.submarines.model.GameModel;
import com.example.submarines.model.Square;
import com.example.submarines.model.Submarine;

import java.util.ArrayList;

public class BoardGame extends View {

    protected GameModel model = GameModel.getInstance();
    protected Square[][] player1SubmarinesBoard, player2SubmarinesBoard, player1FireBoard;
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
        if (GameModel.getInstance().isGameStarted()) {
            fireOnSubmarineAt(x, y);
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

    public void resetSubmarineBoard() {
        for (int i = 0; i < player1SubmarinesBoard.length; i++) {
            for (int j = 0; j < player1SubmarinesBoard.length; j++) {
                model.setSubmarineBoardSquareState(i, j, Square.SquareState.EMPTY);
            }
        }
        for (Submarine submarine : submarineArrayList) {
            submarine.reset();
        }
        invalidate();
    }

    public void resetFireBoard() {
        for (int i = 0; i < player1FireBoard.length; i++) {
            for (int j = 0; j < player1FireBoard.length; j++) {
                model.setFireBoardSquareState(i, j, Square.SquareState.EMPTY);
            }
        }
        invalidate();
    }

    public void resetGame() {
        resetSubmarineBoard();
        resetFireBoard();
        FireBaseStore.INSTANCE.saveGame(model);
        invalidate();
    }

    public void rotateSubmarine() {
        Submarine submarine = GameModel.getInstance().getCurrentSubmarine();
        if (submarine != null) {
            submarine.rotateShape();
            updateSubmarineAndBoard(submarine, submarine.getX(), submarine.getY());
            invalidate();
        }
    }

    private void updateSubmarineAndBoard(Submarine submarine, int x, int y) {

        if (isInsideSubmarinesBoard(x, y)) {

            findSquareAtBoardAndApplyAction(player1SubmarinesBoard, x, y, (i, j) -> {
                Square square = player1SubmarinesBoard[i][j];
                //TODO: Fix bug when moving submarine to adjacent square it overrides occupied submarines
                // and creates an ability for forbidden move - may be need to manage list of occupied submarines per square
                if (square.getState() == Square.SquareState.EMPTY ||
                        square.getState() != Square.SquareState.OCCUPIED_BY_SUBMARINE
                                && square.getOccupiedSubmarine() == submarine) {
                    submarine.updateLocation(square.getX(), square.getY());
                } else if (square.getOccupiedSubmarine() != submarine) {
                    submarine.reset();
                }
            });
            updateOccupiedSquares(submarine);
        } else {
            submarine.reset();
        }
    }

    private boolean isInsideSubmarinesBoard(int x, int y) {
        return isInsideBoard(player1SubmarinesBoard, x, y);
    }

    private boolean isInsideFireBoard(int x, int y) {
        return isInsideBoard(player1FireBoard, x, y);
    }

    private boolean isInsideBoard(Square[][] boardPlayer, int x, int y) {

        return x >= boardPlayer[0][0].getX() && x <= boardPlayer[0][NUM_OF_SQUARES - 1].getX() + Square.SQUARE_SIZE
                && y >= boardPlayer[0][0].getY() && y <= boardPlayer[NUM_OF_SQUARES - 1][0].getY() + Square.SQUARE_SIZE;
    }

    private void updateOccupiedSquares(Submarine submarine) {
        ArrayList<Square> occupiedSquares = new ArrayList<>();
        for (int i = 0; i < player1SubmarinesBoard.length; i++) {
            for (int j = 0; j < player1SubmarinesBoard.length; j++) {
                if (submarine.strictIntersectsWith(player1SubmarinesBoard[i][j])) {
                    model.setSubmarineBoardSquareState(i, j, Square.SquareState.OCCUPIED_BY_SUBMARINE);
                    occupiedSquares.add(player1SubmarinesBoard[i][j]);
                    player1SubmarinesBoard[i][j].setOccupiedSubmarine(submarine);
                } else if (submarine.intersectsWith(player1SubmarinesBoard[i][j])) {
                    model.setSubmarineBoardSquareState(i, j, Square.SquareState.OCCUPIED_BY_SUBMARINE_SURROUND);
                    player1SubmarinesBoard[i][j].setOccupiedSubmarine(submarine);
                } else {
                    if (player1SubmarinesBoard[i][j].getOccupiedSubmarine() == submarine) {
                        model.setSubmarineBoardSquareState(i, j, Square.SquareState.EMPTY);
                    }
                }
            }
        }
        submarine.updateOccupiedSquares(occupiedSquares);
    }

    protected void initSubmarines() {
        submarineArrayList = GameModel.getInstance().initPlayer1Submarines();
        ShapeDrawingStrategy drawingStrategy = new SubmarineDrawer(this.getContext());

        s1 = new Submarine(player1SubmarinesBoard[5][1].getX(), player1SubmarinesBoard[5][1].getY() + Square.SQUARE_SIZE * 2,
                Square.SQUARE_SIZE, Square.SQUARE_SIZE * 2, drawingStrategy);
        submarineArrayList.add(s1);

        s2 = new Submarine(player1SubmarinesBoard[5][2].getX(), player1SubmarinesBoard[5][2].getY() + Square.SQUARE_SIZE * 2,
                Square.SQUARE_SIZE, Square.SQUARE_SIZE * 2, drawingStrategy);
        submarineArrayList.add(s2);

        s3 = new Submarine(player1SubmarinesBoard[5][3].getX(), player1SubmarinesBoard[5][3].getY() + Square.SQUARE_SIZE * 2,
                Square.SQUARE_SIZE, Square.SQUARE_SIZE * 3, drawingStrategy);
        submarineArrayList.add(s3);

        s4 = new Submarine(player1SubmarinesBoard[5][4].getX(), player1SubmarinesBoard[5][4].getY() + Square.SQUARE_SIZE * 2,
                Square.SQUARE_SIZE, Square.SQUARE_SIZE * 4, drawingStrategy);
        submarineArrayList.add(s4);
    }

    protected void initBoards(@NonNull Canvas canvas) {
        if (firstTimeBoard) {
            Square.SQUARE_SIZE = canvas.getWidth() / 3 / NUM_OF_SQUARES + 95;

            initPlayer1SubmarinesBoard();
            initPlayer2SubmarinesBoard();
            initPlayer1FireBoard();
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

    protected void drawFiredSquares(Square[][] boardPlayer, Canvas canvas) {
        for (Square[] squares : boardPlayer) {
            for (int j = 0; j < boardPlayer.length; j++) {
                if (squares[j].getState() == Square.SquareState.OCCUPIED_BY_SUBMARINE_AND_HIT) {
                    squares[j].draw(canvas);
                }
            }
        }
    }

    public void initPlayer1SubmarinesBoard() {
        player1SubmarinesBoard = GameModel.getInstance().initPlayer1SubmarinesBoard();
        initSubmarinesBoard(player1SubmarinesBoard);
    }

    public void initPlayer2SubmarinesBoard() {
        player2SubmarinesBoard = GameModel.getInstance().initPlayer2SubmarinesBoard();
        initSubmarinesBoard(player2SubmarinesBoard);
    }

    private void initSubmarinesBoard(Square[][] board) {
        ShapeDrawingStrategy drawingStrategy = new SquareDrawer(this.getContext());

        int x1 = 0;
        int y1 = 0;
        int w1 = Square.SQUARE_SIZE;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < NUM_OF_SQUARES; j++) {
                board[i][j] = new Square(x1, y1, w1, w1, drawingStrategy);
                x1 = x1 + w1;
            }
            x1 = 0;
            y1 = y1 + w1;
        }
    }

    private void initPlayer1FireBoard() {
        player1FireBoard = GameModel.getInstance().initPlayer1FireBoard();
        ShapeDrawingStrategy drawingStrategy = new SquareDrawer(this.getContext());

        int x2 = 0;
        int y2 = player1SubmarinesBoard[5][5].getY() + 250;
        int w2 = Square.SQUARE_SIZE;

        for (int i = 0; i < player1FireBoard.length; i++) {
            for (int j = 0; j < NUM_OF_SQUARES; j++) {
                player1FireBoard[i][j] = new Square(x2, y2, w2, w2, drawingStrategy);
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

    public void fireOnSubmarineAt(int x, int y) {
        if(!GameModel.getInstance().isGameOver()) {
            if (isInsideFireBoard(x, y)) {
                findSquareAtBoardAndApplyAction(player1FireBoard, x, y, (i, j) -> {
                    if (player2SubmarinesBoard[i][j].getState() == Square.SquareState.OCCUPIED_BY_SUBMARINE) {
                        model.setFireBoardSquareState(i, j, Square.SquareState.OCCUPIED_BY_SUBMARINE_AND_HIT);
                        model.setPlayer2SubmarineBoardSquareState(i, j, Square.SquareState.OCCUPIED_BY_SUBMARINE_AND_HIT);
                    } else {
                        model.setFireBoardSquareState(i, j, Square.SquareState.MISS);
                        model.setPlayer2SubmarineBoardSquareState(i, j, Square.SquareState.MISS);
                    }
                });
            }
            FireBaseStore.INSTANCE.saveGame(GameModel.getInstance());
        }
        if(GameModel.getInstance().isGameOver()) {
            model.setGameResult("Game Over");
            model.setGameState(GameModel.GameState.GAME_OVER);
            FireBaseStore.INSTANCE.saveGame(GameModel.getInstance());

            Toast.makeText(this.getContext(), "Game Over", Toast.LENGTH_SHORT).show();
            resetGame();
        }
    }

    public void findSquareAtBoardAndApplyAction(Square[][] board, int x, int y, SquareActionCallback callback) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j].didUserTouchMe(x, y)) {
                    callback.performAction(i, j);
                }
            }
        }
    }
    public void setupBoard() {
        updateSubmarineAndBoard(s1, player1SubmarinesBoard[0][0].getX(), player1SubmarinesBoard[0][0].getY());
        updateSubmarineAndBoard(s2, player1SubmarinesBoard[3][0].getX(), player1SubmarinesBoard[3][0].getY());
        updateSubmarineAndBoard(s3, player1SubmarinesBoard[3][3].getX(), player1SubmarinesBoard[3][3].getY());
        updateSubmarineAndBoard(s4, player1SubmarinesBoard[0][5].getX(), player1SubmarinesBoard[0][5].getY());
    }
}
