package com.example.submarines.boards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.submarines.helpers.SquareDrawer;
import com.example.submarines.model.Square;
import com.example.submarines.model.Submarine;
import com.example.submarines.buttons.ErasureButton;
import com.example.submarines.buttons.GameButton;
import com.example.submarines.buttons.RotationButton;
import com.example.submarines.buttons.StartGameButton;

import java.util.ArrayList;

public class BoardGame extends View {
    protected Square[][] boardPlayer1, boardPlayer2;
    protected Square square;
    protected Square [] ships;
    protected ArrayList<Submarine> submarineArrayList;
    protected int squareSize;
    protected RotationButton rotationButton;
    protected ErasureButton erasureButton;
    protected StartGameButton startGameButton;
    protected ArrayList<GameButton> gameButtons = new ArrayList<>();
    private final int NUM_OF_SQUARES = 6;
    private boolean firstTimeBoard = true;
    private boolean firstTimeSubmarine = true;
    protected boolean firstTimeButtons = true;
    private boolean userTouchS1, userTouchS2, userTouchS3, userTouchS4;
    private Submarine s1, s2, s3, s4;
    protected boolean isTwoPlayers = false;
    private boolean firstTime = true;


    public BoardGame(Context context) {
        super(context);

        boardPlayer1 = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];
        boardPlayer2 = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];
        //ships = new Square[4];

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
        if (userTouchS1) {
            updateSubmarineAndBoard(s1, x, y);
        }
        if (userTouchS2) {
            updateSubmarineAndBoard(s2, x, y);
        }
        if (userTouchS3) {
            updateSubmarineAndBoard(s3, x, y);
        }
        if (userTouchS4) {
            updateSubmarineAndBoard(s4, x, y);
        }
        invalidate();
    }

    private void handleActionMoveEvent(int x, int y) {
        if (userTouchS1) {
            updateSubmarineLocation(s1, x, y);
        }
        if (userTouchS2) {
            updateSubmarineLocation(s2, x, y);
        }
        if (userTouchS3) {
            updateSubmarineLocation(s3, x, y);
        }
        if (userTouchS4) {
            updateSubmarineLocation(s4, x, y);
        }
        invalidate();
    }

    private void handleActionDownEvent(int x, int y) {
        if (s1.didUserTouchedMe(x, y)) {
            userTouchS1 = true;
            userTouchS2 = userTouchS3 = userTouchS4 = false;
        }
        if (s2.didUserTouchedMe(x, y)) {
            userTouchS2 = true;
            userTouchS1 = userTouchS3 = userTouchS4 = false;
        }
        if (s3.didUserTouchedMe(x, y)) {
            userTouchS3 = true;
            userTouchS1 = userTouchS2 = userTouchS4 = false;
        }
        if (s4.didUserTouchedMe(x, y)) {
            userTouchS4 = true;
            userTouchS1 = userTouchS2 = userTouchS3 = false;
        }
        if (rotationButton.didUserTouchMe(x, y)) {
            if (userTouchS1) {
                s1.rotateBitmap();
            } else if (userTouchS2) {
                s2.rotateBitmap();
            } else if (userTouchS3) {
                s3.rotateBitmap();
            } else if (userTouchS4) {
                s4.rotateBitmap();
            }
        }
        if (erasureButton.didUserTouchMe(x, y)) {
            //TODO: Clear the board
        }
//        if (square.didUserTouchMe(x, y))
//        {
//            isTwoPlayers = true;
//        }
    }

    private void updateSubmarineLocation(Submarine submarine, int x, int y) {
        submarine.setX(x);
        submarine.setY(y);
    }
    private void updateSubmarineAndBoard(Submarine submarine, int x, int y) {

        for (int i = 0; i < boardPlayer1.length; i++)
        {
            for (int j = 0; j < boardPlayer1.length; j++)
            {
                if(boardPlayer1[i][j].didUserTouchMe(x,y))
                {
                    System.out.println("i: "+ i + "j: " + j);
                    updateSubmarineLocation(submarine, boardPlayer1[i][j].getX(), boardPlayer1[i][j].getY());
                    // TODO: Check if square is occupied already

                    //boardPlayer1[i][j].setOccupied(true);
                }
            }
        }
        updateOccupiedSquares(submarine);
    }

    private void updateOccupiedSquares(Submarine submarine) {
        for (Square[] squares : boardPlayer1) {
            for (int j = 0; j < boardPlayer1.length; j++) {
                if (submarine.intersectsWith(squares[j])) {
                    squares[j].setOccupied(submarine);
                } else {
                    if (squares[j].getOccupiedSubmarine() == submarine)
                        squares[j].setOccupied(null);
                }
            }
        }
    }

    protected void drawMoveButton(Canvas canvas)
    {
        if(firstTime)
        {
            int x = boardPlayer1[5][1].getX() + squareSize*3+4;
            int y = boardPlayer1[5][5].getY() + squareSize*6;
            int w = squareSize;
            int color = Color.WHITE;
            square = new Square(x, y, w, w);
            firstTime = false;
        }

        if(!isTwoPlayers) {
            SquareDrawer drawer = new SquareDrawer();
            drawer.draw(square, canvas);
        }

    }

    protected void initSubmarines (Canvas canvas)
    {
        s1 = new Submarine(boardPlayer1[5][1].getX(),boardPlayer1[5][0].getY() + squareSize*2, this.getResources(), squareSize, squareSize*2);
        submarineArrayList.add(s1);

        s2 = new Submarine(boardPlayer1[5][2].getX(),boardPlayer1[5][0].getY() + squareSize*2, this.getResources(), squareSize, squareSize*2);
        submarineArrayList.add(s2);

        s3 = new Submarine(boardPlayer1[5][3].getX(),boardPlayer1[5][0].getY() + squareSize*2, this.getResources(), squareSize, squareSize*3);
        submarineArrayList.add(s3);

        s4 = new Submarine(boardPlayer1[5][4].getX(),boardPlayer1[5][0].getY() + squareSize*2, this.getResources(), squareSize, squareSize*4);
        submarineArrayList.add(s4);
    }

    protected void initShips(Canvas canvas) {

        int x1 = canvas.getWidth()/9;
        int y1 = boardPlayer1[5][0].getY() + squareSize*2;
        int w1 = squareSize;
        int h1 = w1*2;
        int color1 = Color.GRAY;
        int count =0;
        for (int i = 0; i < 4; i++) {

            if (count<=1)
            {
                ships[i] = new Square(x1, y1, w1, h1);
                x1 = x1 + squareSize + ((boardPlayer1[5][0].getX() + boardPlayer1[5][5].getX())/9);
            }
            if(count==2)
            {
                h1= w1*3;
                ships[i] = new Square(x1, y1, w1, h1);
                x1 = x1 + squareSize + ((boardPlayer1[5][0].getX() + boardPlayer1[5][5].getX())/9);
            }
            if (count==3)
            {
                h1= w1*4;
                ships[i] = new Square(x1, y1, w1, h1);
                x1 = x1 + squareSize + ((boardPlayer1[5][0].getX() + boardPlayer1[5][5].getX())/9);
            }
            count++;
        }
    }

    protected void initBoards(@NonNull Canvas canvas) {
        if(firstTimeBoard)
        {
            initBoard1(canvas);
            initBoard2(canvas);

            firstTimeBoard = false;
        }
    }

    public void drawBoard1(Canvas canvas) {

        for (Square[] squares : boardPlayer1) {
            for (int j = 0; j < boardPlayer1.length; j++) {
                squares[j].draw(canvas);
            }
        }
    }
    public void drawBoard2 (Canvas canvas)
    {
        for (Square[] squares : boardPlayer2) {
            for (int j = 0; j < boardPlayer2.length; j++) {
                squares[j].draw(canvas);
            }
        }
    }

    public int initBoard1(Canvas canvas) {
        squareSize = canvas.getWidth()/3/NUM_OF_SQUARES +80;

        int x1 = canvas.getWidth()/9;
        int y1 = 50;
        int w1 = squareSize;
        int color1 = Color.GRAY;
        //int color = Color.BLACK;
        //p = new Paint();
        //p.setColor(color2);


        for (int i = 0; i < boardPlayer1.length; i++)
        {
            for (int j = 0; j < NUM_OF_SQUARES; j++)
            {
                boardPlayer1[i][j] = new Square(x1, y1, w1, w1);
                //boardPlayer1[i][j].setP(boardPlayer1[i][j].getP(),100);
                x1 = x1+w1;
            }
            x1 = canvas.getWidth()/9;
            y1 = y1 + w1;
        }
        return y1;
    }
    private void initBoard2(Canvas canvas)
    {
        int x2 = canvas.getWidth()/9;
        int y2 = initBoard1(canvas)+100;
        int w2 = squareSize;
        int color2 = Color.GRAY;
        //int color2 = Color.BLACK;
        //p.setColor(color2);


        for (int i = 0; i < boardPlayer2.length; i++) {
            for (int j = 0; j < NUM_OF_SQUARES; j++) {
                boardPlayer2[i][j] = new Square(x2, y2, w2, w2);
                //boardPlayer2[i][j].setP(boardPlayer2[i][j].getP(),100);

                x2 = x2 + w2;
            }
            x2 = canvas.getWidth() / 9;
            y2 = y2 + w2;
        }
    }
    protected void drawSubmarines(Canvas canvas)
    {
        if(firstTimeSubmarine)
        {
            initSubmarines(canvas);
            firstTimeSubmarine = false;
        }
        for (int i = 0; i < submarineArrayList.size(); i++) {
        //for (int i = 0; i < 1; i++) {
            submarineArrayList.get(i).draw(canvas);
        }
    }
}
