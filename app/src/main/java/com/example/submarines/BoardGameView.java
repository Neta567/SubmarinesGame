package com.example.submarines;

import static com.example.submarines.Square.SquareState.EMPTY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class BoardGameView extends View {

    protected Square[][] submarinesBoard, fireBoard; //לוח ספינות שלי ושל השני ולוח יריות שלי
    protected ArrayList<Submarine> submarineArrayList;
    public static final int NUM_OF_SQUARES = 6;
    private boolean firstTimeBoard = true, firstTimeSubmarine = true;
    protected Submarine s1, s2, s3, s4;
    private Context context;
    public boolean isGameStarted = false;
    private boolean isGameOver = false;
    public int gameId;
    public int gameScore;

    public BoardGameView(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        initBoards(canvas);
        drawBoard(submarinesBoard, canvas);

        if(isGameStarted == true)
        {
            drawBoard(fireBoard, canvas); // אם המשחק התחיל או נגמר תצייר את הלוח יריות שלי
            drawFiredSquares(submarinesBoard, canvas); //מצייר את הבומים והאיקסים מעל ללוח
        }
        drawSubmarines(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isGameStarted == true) { // אם המשחק התחיל הפעולה היחידה שאפשר לעשות זה יריות אז נקרא לפעולה של היריות
                fireOnSubmarineAt(x, y);
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            invalidate();
        }
        return true;
    }
    private void updateSubmarineAndBoard(Submarine submarine, int x, int y) {
        if (isInsideSubmarinesBoard(x, y)) { // אם הצוללת מונחת בתוך הלוח אז -
            for (int i = 0; i < submarinesBoard.length; i++) {
                for (int j = 0; j < submarinesBoard.length; j++) {
                    if (submarinesBoard[i][j].didUserTouchMe(x, y)) {
                        Square square = submarinesBoard[i][j];
                        if (square.getState() == EMPTY ||
                                square.getState() != Square.SquareState.OCCUPIED_BY_SUBMARINE
                                        && square.getOccupiedSubmarine() == submarine) { // אם שמו את הצוללת על ריבוע ריק או על ריבוע שאין בו צוללת אחרת והצוללת שיש שם זאת הצוללת הנוכחית אז -
                            submarine.updateLocation(square.getX(), square.getY()); // מעדכן את המיקום הסופי של הצוללת
                        } else if (square.getOccupiedSubmarine() != submarine) { // אם הריבוע תפוס עם צוללת אחרת אז תחזיר את הצוללת אחורה
                            submarine.reset();
                        }
                    }
                }
            }
            updateOccupiedSquares(submarine); // לעדכן את כל המצבים של הריבועים בלוח
        } else {
            submarine.reset();
        }
    }

    private boolean isInsideSubmarinesBoard(int x, int y) { // לוקח את המיקום ובודק אם הצוללת בתוך הלוח
        return isInsideBoard(submarinesBoard, x, y);
    }

    private boolean isInsideFireBoard(int x, int y) { // לוקח את המיקום של הירייה ובודק אם זה בתוך לוח היריות
        return isInsideBoard(fireBoard, x, y);
    }

    private boolean isInsideBoard(Square[][] boardPlayer, int x, int y) { // הפעולה שבודקת את הגבולות של הלוח יחד עם המיקום שהיא מקבלת

        return x >= boardPlayer[0][0].getX() && x <= boardPlayer[0][NUM_OF_SQUARES - 1].getX() + Square.SQUARE_SIZE
                && y >= boardPlayer[0][0].getY() && y <= boardPlayer[NUM_OF_SQUARES - 1][0].getY() + Square.SQUARE_SIZE;
    }

    private void updateOccupiedSquares(Submarine submarine) {
        ArrayList<Square> occupiedSquares = new ArrayList<>(); // יוצרים אריי ליסט של ריבועים תפוסים
        for (int i = 0; i < submarinesBoard.length; i++) { // עוברים על הלוח של הצוללות
            for (int j = 0; j < submarinesBoard.length; j++) {
                if (submarine.strictIntersectsWith(submarinesBoard[i][j])) { // אם הריבוע מוכל בצוללת שעליו אז -
                    submarinesBoard[i][j].setState(Square.SquareState.OCCUPIED_BY_SUBMARINE);
                    occupiedSquares.add(submarinesBoard[i][j]); // תוסיף אותו למערך הריבועים התפוסים
                    submarinesBoard[i][j].setOccupiedSubmarine(submarine); // כל ריבוע יודע איזה צוללת יש עליו אז זה מגדיר אותה
                } else if (submarine.intersectsWith(submarinesBoard[i][j])) { // אם הריבוע הוא מסביב לצוללת אז -
                    submarinesBoard[i][j].setState(Square.SquareState.OCCUPIED_BY_SUBMARINE_SURROUND);
                    submarinesBoard[i][j].setOccupiedSubmarine(submarine); // גם אם הריבוע מסביב לצוללת נגדיר שהצוללת על הריבוע הנל
                } else {
                    if (submarinesBoard[i][j].getOccupiedSubmarine() == submarine) { // אם הזזנו את הצוללת אז את הריבוע הקודם שהיא הייתה עליו נעביר לריק
                        submarinesBoard[i][j].setState(EMPTY);
                    }
                }
            }
        }
        submarine.updateOccupiedSquares(occupiedSquares); // לכל צוללת יש אריי של הריבועים שהיא תופסת אז זה מכניס לשם אותם
    }

    protected void initSubmarines() {
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

    protected void initBoards(@NonNull Canvas canvas) {
        if (firstTimeBoard) {
            Square.SQUARE_SIZE = canvas.getWidth() / 3 / NUM_OF_SQUARES + 95;

            initPlayer1SubmarinesBoard();
            initPlayer1FireBoard();
            firstTimeBoard = false;
        }
    }

    protected void drawBoard(Square[][] boardPlayer, Canvas canvas) {
        for (Square[] squares : boardPlayer) {
            for (int j = 0; j < boardPlayer.length; j++) {
                squares[j].draw(canvas, context);
            }
        }
    }

    protected void drawFiredSquares(Square[][] boardPlayer, Canvas canvas) { // מצייר את הבומים מעל לצוללת
        for (Square[] squares : boardPlayer) {
            for (int j = 0; j < boardPlayer.length; j++) {
                if (squares[j].getState() == Square.SquareState.OCCUPIED_BY_SUBMARINE_AND_HIT) {
                    squares[j].draw(canvas, context);
                }
            }
        }
    }

    public void initPlayer1SubmarinesBoard() {
        submarinesBoard = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];
        initSubmarinesBoard(submarinesBoard); // מאתחל את הלוח
    }

    private void initSubmarinesBoard(Square[][] board) {
        int x1 = 0;
        int y1 = 0;
        int w1 = Square.SQUARE_SIZE;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < NUM_OF_SQUARES; j++) {
                board[i][j] = new Square(x1, y1, w1, w1);
                x1 = x1 + w1;
            }
            x1 = 0;
            y1 = y1 + w1;
        }
    }

    private void initPlayer1FireBoard() {
        fireBoard = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];
        int x2 = 0;
        int y2 = submarinesBoard[5][5].getY() + 250;
        int w2 = Square.SQUARE_SIZE;

        for (int i = 0; i < fireBoard.length; i++) {
            for (int j = 0; j < NUM_OF_SQUARES; j++) {
                fireBoard[i][j] = new Square(x2, y2, w2, w2);
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
            if(submarineArrayList.get(i).isVisibale()==true)
                submarineArrayList.get(i).draw(layout, context);
        }
    }

    public void fireOnSubmarineAt(int x, int y) {
        if(!isGameOver) { // אם המשחק לא נגמר עדיין
            if (isInsideFireBoard(x, y)) { // אם לחצנו בתוך לוח היריות ולא בחוץ
                for (int i = 0; i < fireBoard.length; i++) {
                    for (int j = 0; j < fireBoard.length; j++) {
                        if (fireBoard[i][j].didUserTouchMe(x, y)) {
                            if(fireBoard[i][j].getState() == EMPTY) { // אם לא ירו לריבוע הזה כבר אז -
                                if (submarinesBoard[i][j].getState() == Square.SquareState.OCCUPIED_BY_SUBMARINE) { // אם יש צוללת בלוח צוללות של היריב
                                    fireBoard[i][j].setState(Square.SquareState.OCCUPIED_BY_SUBMARINE_AND_HIT);
                                    submarinesBoard[i][j].setState(Square.SquareState.OCCUPIED_BY_SUBMARINE_AND_HIT);

                                    for (int k = 0; k < submarineArrayList.size(); k++) {
                                        if (submarineArrayList.get(k).isDestroyed()) {
                                            submarineArrayList.get(k).setVisibale(true);
                                        }
                                    }
                                } else { // אם אין שם צוללות -
                                    fireBoard[i][j].setState(Square.SquareState.MISS);
                                    submarinesBoard[i][j].setState(Square.SquareState.MISS);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setupBoard(int num) { // הכפתור המוזר ששם את הצוללות ישר על הלוח
        if(num==0) {
            updateSubmarineAndBoard(s1, submarinesBoard[0][0].getX(), submarinesBoard[0][0].getY());
            updateSubmarineAndBoard(s2, submarinesBoard[3][0].getX(), submarinesBoard[3][0].getY());
            updateSubmarineAndBoard(s3, submarinesBoard[3][3].getX(), submarinesBoard[3][3].getY());
            updateSubmarineAndBoard(s4, submarinesBoard[0][5].getX(), submarinesBoard[0][5].getY());
        }
        if(num==1) {
            updateSubmarineAndBoard(s1, submarinesBoard[0][0].getX(), submarinesBoard[0][0].getY());
            updateSubmarineAndBoard(s2, submarinesBoard[3][1].getX(), submarinesBoard[3][1].getY());
            updateSubmarineAndBoard(s3, submarinesBoard[3][3].getX(), submarinesBoard[3][3].getY());
            updateSubmarineAndBoard(s4, submarinesBoard[0][5].getX(), submarinesBoard[0][5].getY());
        }
        if (num==2) {
            updateSubmarineAndBoard(s1, submarinesBoard[0][0].getX(), submarinesBoard[0][0].getY());
            updateSubmarineAndBoard(s2, submarinesBoard[3][1].getX(), submarinesBoard[3][1].getY());
            updateSubmarineAndBoard(s3, submarinesBoard[0][3].getX(), submarinesBoard[0][3].getY());
            updateSubmarineAndBoard(s4, submarinesBoard[0][5].getX(), submarinesBoard[0][5].getY());
        }
        if (num==3) {
            updateSubmarineAndBoard(s1, submarinesBoard[0][0].getX(), submarinesBoard[0][0].getY());
            updateSubmarineAndBoard(s2, submarinesBoard[4][2].getX(), submarinesBoard[4][2].getY());
            updateSubmarineAndBoard(s3, submarinesBoard[0][3].getX(), submarinesBoard[0][3].getY());
            updateSubmarineAndBoard(s4, submarinesBoard[2][5].getX(), submarinesBoard[2][5].getY());
        }
        // set all submarines to be invisible
        for (int i = 0; i < submarineArrayList.size(); i++) {
            submarineArrayList.get(i).setVisibale(false);
        }
    }
    public boolean isGameOver() {

        for (int i = 0; i < submarineArrayList.size(); i++) {
            if (submarineArrayList.get(i).isDestroyed() == false) {
                return false;
            }
        }
        return true;

    }
}
