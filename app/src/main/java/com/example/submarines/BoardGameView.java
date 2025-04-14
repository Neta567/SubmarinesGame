package com.example.submarines;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class BoardGameView extends View {

    private Square[][] submarinesBoard, fireBoard; //לוח ספינות שלי ושל השני ולוח יריות שלי
    private ArrayList<Submarine> submarineArrayList;
    private final int NUM_OF_SQUARES = 6;
    private boolean firstTimeBoard = true, firstTimeSubmarine = true;
    private Submarine s1, s2, s3, s4;
    private Context context;
    public boolean isGameStarted = false;
    private boolean isGameOver = false;
    public int gameId;
    public float gameScore;
    public String name;
    private float hitted;
    private float misses;

    public BoardGameView(Context context)
    {
        super(context);
        this.context = context;
    }

    @Override
    public void onDraw(@NonNull Canvas canvas)
    {
        initBoards(canvas);
        drawBoard(submarinesBoard, canvas);

        if (isGameStarted == true)
        {
            drawBoard(fireBoard, canvas); // אם המשחק התחיל או נגמר תצייר את הלוח יריות שלי
            drawFiredSquares(submarinesBoard, canvas); //מצייר את הבומים והאיקסים מעל ללוח
        }
        drawSubmarines(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            if (isGameStarted == true)
            { // אם המשחק התחיל הפעולה היחידה שאפשר לעשות זה יריות אז נקרא לפעולה של היריות
                fireOnSubmarineAt(x, y);
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            invalidate();
        }
        return true;
    }

    private void updateSubmarineAndBoard(Submarine submarine, int i, int j, int size)
    {
        Square square = submarinesBoard[i][j];
        submarine.updateLocation(square.getX(), square.getY()); // מעדכן את המיקום הסופי של הצוללת

        ArrayList<Square> occupiedSquares = new ArrayList<>();
        for(int k=i; k<i+size; k++)
        {
            submarinesBoard[k][j].setState(Square.OCCUPIED_BY_SUBMARINE);
            occupiedSquares.add(submarinesBoard[k][j]);
        }
        submarine.updateOccupiedSquares(occupiedSquares);
    }

    private boolean isInsideFireBoard(int x, int y)
    { // לוקח את המיקום של הירייה ובודק אם זה בתוך לוח היריות
        return x >= fireBoard[0][0].getX() && x <= fireBoard[0][NUM_OF_SQUARES - 1].getX() + Square.SQUARE_SIZE
                && y >= fireBoard[0][0].getY() && y <= fireBoard[NUM_OF_SQUARES - 1][0].getY() + Square.SQUARE_SIZE;
    }

    private void initSubmarines()
    {
        submarineArrayList = new ArrayList<>(4);
        s1 = new Submarine(submarinesBoard[5][1].getX(), submarinesBoard[5][1].getY() + Square.SQUARE_SIZE * 2,
                Square.SQUARE_SIZE, Square.SQUARE_SIZE * 2);
        submarineArrayList.add(s1);

        s2 = new Submarine(submarinesBoard[5][2].getX(), submarinesBoard[5][2].getY() + Square.SQUARE_SIZE * 2,
                Square.SQUARE_SIZE, Square.SQUARE_SIZE * 2);
        submarineArrayList.add(s2);

        s3 = new Submarine(submarinesBoard[5][3].getX(), submarinesBoard[5][3].getY() + Square.SQUARE_SIZE * 2,
                Square.SQUARE_SIZE, Square.SQUARE_SIZE * 3);
        submarineArrayList.add(s3);

        s4 = new Submarine(submarinesBoard[5][4].getX(), submarinesBoard[5][4].getY() + Square.SQUARE_SIZE * 2,
                Square.SQUARE_SIZE, Square.SQUARE_SIZE * 4);
        submarineArrayList.add(s4);
    }

    private void initBoards(@NonNull Canvas canvas)
    {
        if (firstTimeBoard)
        {
            Square.SQUARE_SIZE = canvas.getWidth() / 3 / NUM_OF_SQUARES + 95;

            initSubmarinesBoard();
            initFireBoard();
            firstTimeBoard = false;
        }
    }

    private void drawBoard(Square[][] board, Canvas canvas)
    {
        for(int i = 0; i < board.length; i++)
        {
            for (int j = 0; j < board.length; j++)
            {
                board[i][j].draw(canvas, context);
            }
        }
    }

    private void drawFiredSquares(Square[][] board, Canvas canvas)
    { // מצייר את הבומים מעל לצוללת
        for(int i = 0; i < board.length; i++)
        {
            for (int j = 0; j < board.length; j++)
            {
                if (board[i][j].getState() == Square.OCCUPIED_BY_SUBMARINE_AND_HIT)
                {
                    board[i][j].draw(canvas, context);
                }
            }
        }
    }

    private void initSubmarinesBoard()
    {
        submarinesBoard = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];

        int x1 = 0;
        int y1 = 0;
        int w1 = Square.SQUARE_SIZE;

        for (int i = 0; i < submarinesBoard.length; i++)
        {
            for (int j = 0; j < NUM_OF_SQUARES; j++)
            {
                submarinesBoard[i][j] = new Square(x1, y1, w1, w1);
                x1 = x1 + w1;
            }
            x1 = 0;
            y1 = y1 + w1;
        }
    }

    private void initFireBoard()
    {
        fireBoard = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];
        int x2 = 0;
        int y2 = submarinesBoard[5][5].getY() + 250;
        int w2 = Square.SQUARE_SIZE;

        for (int i = 0; i < fireBoard.length; i++)
        {
            for (int j = 0; j < NUM_OF_SQUARES; j++)
            {
                fireBoard[i][j] = new Square(x2, y2, w2, w2);
                x2 = x2 + w2;
            }
            x2 = 0;
            y2 = y2 + w2;
        }
    }

    private void drawSubmarines(Canvas layout)
    {
        if (firstTimeSubmarine)
        {
            initSubmarines();
            firstTimeSubmarine = false;
        }
        for (int i = 0; i < submarineArrayList.size(); i++)
        {
            if (submarineArrayList.get(i).isVisibale() == true)
                submarineArrayList.get(i).draw(layout, context);
        }
    }

    private void fireOnSubmarineAt(int x, int y)
    {
        if (!isGameOver)
        {
            if (isInsideFireBoard(x, y))
            {
                for (int i = 0; i < fireBoard.length; i++)
                {
                    for (int j = 0; j < fireBoard.length; j++)
                    {
                        if (fireBoard[i][j].didUserTouchMe(x, y))
                        {
                            if (fireBoard[i][j].getState() == Square.EMPTY)
                            {
                                if (submarinesBoard[i][j].getState() == Square.OCCUPIED_BY_SUBMARINE)
                                {
                                    fireBoard[i][j].setState(Square.OCCUPIED_BY_SUBMARINE_AND_HIT);
                                    submarinesBoard[i][j].setState(Square.OCCUPIED_BY_SUBMARINE_AND_HIT);

                                    hitted++;

                                    for (int k = 0; k < submarineArrayList.size(); k++)
                                    {
                                        if (submarineArrayList.get(k).isDestroyed())
                                        {
                                            submarineArrayList.get(k).setVisibale(true);
                                        }
                                    }
                                }
                                else
                                { // אם אין שם צוללות -
                                    fireBoard[i][j].setState(Square.MISS);
                                    submarinesBoard[i][j].setState(Square.MISS);
                                    misses++;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setupBoard(int num)
    {
        if (num == 0)
        {
            updateSubmarineAndBoard(s1, 0, 0, 2);
            updateSubmarineAndBoard(s2,3, 0, 2);
            updateSubmarineAndBoard(s3, 3, 3, 3);
            updateSubmarineAndBoard(s4, 0, 5, 4);
        }
        if (num == 1)
        {
            updateSubmarineAndBoard(s1, 0, 0, 2);
            updateSubmarineAndBoard(s2, 3, 1, 2);
            updateSubmarineAndBoard(s3, 3,3, 3);
            updateSubmarineAndBoard(s4, 0,5, 4);
        }
        if (num == 2)
        {
            updateSubmarineAndBoard(s1, 0,0, 2);
            updateSubmarineAndBoard(s2, 3,1, 2);
            updateSubmarineAndBoard(s3, 0,3, 3);
            updateSubmarineAndBoard(s4, 0,5, 4);
        }
        if (num == 3)
        {
            updateSubmarineAndBoard(s1, 0,0, 2);
            updateSubmarineAndBoard(s2, 4,2, 2);
            updateSubmarineAndBoard(s3, 0,3, 3);
            updateSubmarineAndBoard(s4, 2,5, 4);
        }
        // make all submarines invisible
        for (int i = 0; i < submarineArrayList.size(); i++)
        {
            submarineArrayList.get(i).setVisibale(false);
        }
    }

    public boolean isGameOver()
    {

        for (int i = 0; i < submarineArrayList.size(); i++)
        {
            if (submarineArrayList.get(i).isDestroyed() == false)
            {
                return false;
            }
        }
        return true;

    }
    public void makeAGameScore()
    {
        gameScore = (hitted/(hitted+misses))*100;
        hitted = 0;
        misses = 0;

    }
}
