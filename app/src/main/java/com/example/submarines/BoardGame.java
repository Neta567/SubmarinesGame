package com.example.submarines;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class BoardGame extends View {
    protected Square [][] boardPlayer1, boardPlayer2;
    protected Square square;
    protected Square [] ships;
    protected Bitmap submarine1,submarine2,submarine3,submarine4,rotate,erasue;

    protected ArrayList<Submarine> submarineArrayList;
    protected int squareSize;
    protected RotationButton rotationButton;
    protected ErasureButton erasureButton;


    private Context context;
    private final int NUM_OF_SQUARES = 6;
    private boolean firstTimeboard = true,firstTimeSubmarine = true,erasure_button=false;
    private Paint p;
    private LinearLayout linearLayout;
    private boolean userTouchS1,userTouchS2,userTouchS3,userTouchS4;
    private Submarine s1,s2,s3,s4;
    protected boolean isTwoPlayers = false;
    private boolean firstTime = true;


    public BoardGame(Context context) {
        super(context);

        boardPlayer1 = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];
        boardPlayer2 = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];
        //ships = new Square[4];

        submarineArrayList = new ArrayList<Submarine>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
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
            if (square.didUserTouchMe(x,y))
            {
                isTwoPlayers = true;

            }
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
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
        if (event.getAction() == MotionEvent.ACTION_UP) {
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
        //return super.onTouchEvent(event);
        return true;
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
                    updateSubmarineLocation(submarine, boardPlayer1[i][j].x, boardPlayer1[i][j].y);
                    // TODO: Check if square is occupied already

//                    boardPlayer1[i][j].setOccupied(true);
//
//                    int size = submarine.getSize();
//                    int num = size/squareSize;
//                    if(submarine.isVertical())
//                    {
//
//                        for(int k=i; i<num; k++) {
//                            boardPlayer1[k][j].setOccupied(true);
//                        }
//                        // TODO: Mark surrounding relevant board square as occupied
//                    }
//                    else
//                    {
//                        for(int k=i; j<num; k++) {
//                            boardPlayer1[i][k].setOccupied(true);
//                        }
//                        // TODO: Mark surrounding relevant board square as occupied
//                    }
                }
            }
        }
    }


//    @Override
//    public void onDraw(@NonNull Canvas canvas) {
//        super.onDraw(canvas);
//        initBoards(canvas);
//        initShips(canvas);
//
//        drawBoard1(canvas);
//        drawBoard2(canvas);
//        if(isTwoPlayers)
//        {
//            // TODO: 14/01/2025 draw second board
//        }
//    }
    protected void drawRotationButton(Canvas canvas)
    {
        int x = boardPlayer1[5][1].x;
        int y = boardPlayer1[5][5].y + squareSize*6;
        int w = squareSize;
        int h = w;
        int color = Color.WHITE;
        rotate = BitmapFactory.decodeResource(getResources(),R.drawable.rotation_button);

        rotationButton = new RotationButton(x,y,w,h,color,rotate);
        //rotationSubmarine.p.setAlpha(300);
        rotationButton.draw1(canvas);

    }
    protected void drawErasureButton(Canvas canvas)
    {
        int x = boardPlayer1[5][1].x + squareSize*3;
        int y = boardPlayer1[5][5].y + squareSize*6;
        int w = squareSize;
        int h = w;
        int color = Color.WHITE;
        erasue = BitmapFactory.decodeResource(getResources(),R.drawable.trash_can);

        erasureButton = new ErasureButton(x,y,w,h,color,erasue);
        //rotationSubmarine.p.setAlpha(300);
        erasureButton.draw1(canvas);

    }
    protected void drawMoveButton(Canvas canvas)
    {
        if(firstTime)
        {
            int x = boardPlayer1[5][1].x + squareSize*3+4;
            int y = boardPlayer1[5][5].y + squareSize*6;
            int w = squareSize;
            int h = w;
            int color = Color.WHITE;
            square = new Square(x,y,w,h,color);
            firstTime = false;
        }

        if(!isTwoPlayers)
            square.draw2(canvas);

    }

    protected void initSubmarines (Canvas canvas)
    {
        s1 = new Submarine(boardPlayer1[5][1].x,boardPlayer1[5][0].y + squareSize*2, this.getResources(), squareSize, squareSize*2);
        submarineArrayList.add(s1);

        s2 = new Submarine(boardPlayer1[5][2].x,boardPlayer1[5][0].y + squareSize*2, this.getResources(), squareSize, squareSize*2);
        submarineArrayList.add(s2);

        s3 = new Submarine(boardPlayer1[5][3].x,boardPlayer1[5][0].y + squareSize*2, this.getResources(), squareSize, squareSize*3);
        submarineArrayList.add(s3);

        s4 = new Submarine(boardPlayer1[5][4].x,boardPlayer1[5][0].y + squareSize*2, this.getResources(), squareSize, squareSize*4);
        submarineArrayList.add(s4);
    }

    protected void initShips(Canvas canvas) {

        int x1 = canvas.getWidth()/9;
        int y1 = boardPlayer1[5][0].y + squareSize*2;
        int w1 = squareSize;
        int h1 = w1*2;
        int color1 = Color.GRAY;
        int count =0;
        for (int i = 0; i < 4; i++) {

            if (count<=1)
            {
                ships[i] = new Square(x1,y1,w1,h1,color1);
                x1 = x1 + squareSize + ((boardPlayer1[5][0].x + boardPlayer1[5][5].x)/9);
            }
            if(count==2)
            {
                h1= w1*3;
                ships[i] = new Square(x1,y1,w1,h1,color1);
                x1 = x1 + squareSize + ((boardPlayer1[5][0].x + boardPlayer1[5][5].x)/9);
            }
            if (count==3)
            {
                h1= w1*4;
                ships[i] = new Square(x1,y1,w1,h1,color1);
                x1 = x1 + squareSize + ((boardPlayer1[5][0].x + boardPlayer1[5][5].x)/9);
            }
            count++;


        }



    }


    protected void initBoards(@NonNull Canvas canvas) {
        if(firstTimeboard)
        {
            initBoard1(canvas);
            initBoard2(canvas);

            firstTimeboard = false;
        }
    }

    public void drawBoard1(Canvas canvas) {

        for (int i = 0; i < boardPlayer1.length; i++)
        {
            for (int j = 0; j < boardPlayer1.length; j++)
            {
                boardPlayer1[i][j].draw1(canvas);
            }
        }

    }
    public void drawBoard2 (Canvas canvas)
    {
        for (int i = 0; i < boardPlayer2.length; i++)
        {
            for (int j = 0; j < boardPlayer2.length; j++)
            {
                boardPlayer2[i][j].draw1(canvas);
            }
        }
    }

    public int initBoard1(Canvas canvas) {
        squareSize = canvas.getWidth()/3/NUM_OF_SQUARES +80;

        int x1 = canvas.getWidth()/9;
        int y1 = 100;
        int w1 = squareSize;
        int h1 = w1;
        int color1 = Color.GRAY;
        //int color = Color.BLACK;
        //p = new Paint();
        //p.setColor(color2);


        for (int i = 0; i < boardPlayer1.length; i++)
        {
            for (int j = 0; j < NUM_OF_SQUARES; j++)
            {
                boardPlayer1[i][j] = new Square(x1,y1,w1,h1,color1);
                //boardPlayer1[i][j].setP(boardPlayer1[i][j].getP(),100);
                x1 = x1+w1;
            }
            x1 = canvas.getWidth()/9;
            y1 = y1 + h1;

        }
        return y1;


    }
    private void initBoard2(Canvas canvas)
    {
        int x2 = canvas.getWidth()/9;
        int y2 = initBoard1(canvas)+100;
        int w2 = squareSize;
        int h2 = w2;
        int color2 = Color.GRAY;
        //int color2 = Color.BLACK;
        //p.setColor(color2);


        for (int i = 0; i < boardPlayer2.length; i++) {
            for (int j = 0; j < NUM_OF_SQUARES; j++) {
                boardPlayer2[i][j] = new Square(x2, y2, w2, h2, color2);
                //boardPlayer2[i][j].setP(boardPlayer2[i][j].getP(),100);

                x2 = x2 + w2;
            }
            x2 = canvas.getWidth() / 9;
            y2 = y2 + h2;
        }
    }
    protected void drawSubmarine (Canvas canvas)
    {
        if(firstTimeSubmarine)
        {
            initSubmarines(canvas);
            firstTimeSubmarine = false;

        }
        for (int i = 0; i < submarineArrayList.size(); i++) {

            submarineArrayList.get(i).draw(canvas);

        }

    }
}
