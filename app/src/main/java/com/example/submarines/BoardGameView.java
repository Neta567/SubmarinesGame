package com.example.submarines;

import static com.example.submarines.Square.SquareState.EMPTY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class BoardGameView extends View {

    protected Square[][] player1SubmarinesBoard, player1FireBoard; //לוח ספינות שלי ושל השני ולוח יריות שלי
    protected ArrayList<Submarine> submarineArrayList;
    public static final int NUM_OF_SQUARES = 6;
    private boolean firstTimeBoard = true;
    private boolean firstTimeSubmarine = true;
    protected Submarine s1, s2, s3, s4;
    private final FireBaseStore fireBaseStore = new FireBaseStore();
    private Context context1;
    public boolean isGameStarted = false;
    private boolean isGameOver = false;
    private Submarine currentSubmarine;
    public int gameId;
    public int gameScore;

    public BoardGameView(Context context) {
        super(context);
        context1 = context;

    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        initBoards(canvas);
        drawBoard(player1SubmarinesBoard, canvas);
        drawSubmarines(canvas);

        if(isGameStarted == true)
        {
            drawBoard(player1FireBoard, canvas); // אם המשחק התחיל או נגמר תצייר את הלוח יריות שלי
            drawFiredSquares(player1SubmarinesBoard, canvas); //מצייר את הבומים והאיקסים מעל ללוח
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            handleActionDownEvent(x, y); //אם לחצנו על המסך עוברים לפעול הזאת
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            handleActionMoveEvent(x, y); // אם הזיזו משהו אז עוברים לפעולה הזאת
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            handleActionUpEvent(x, y); // אם הרימו את הלחיצה עוברים לפעולה הזאת
        }
        return true;
    }

    private void handleActionUpEvent(int x, int y) {
        if (currentSubmarine != null) {
            updateSubmarineAndBoard(currentSubmarine, x, y); // מעדכנים את המיקום הסופי של הצוללת על המסך
        }
        invalidate();
    }

    private void handleActionMoveEvent(int x, int y) {
        if (currentSubmarine != null) {
            currentSubmarine.updateLocation(x, y); // מעדכנים את המיקום שלה על המסך
        }
        invalidate();
    }

    protected void handleActionDownEvent(int x, int y) {
        if (isGameStarted == true) { // אם המשחק התחיל הפעולה היחידה שאפשר לעשות זה יריות אז נקרא לפעולה של היריות
            fireOnSubmarineAt(x, y);
        } else { // אם המשחק לא התחיל אז נשמור את הצוללות האחרונה שנגעו בה
            if (s1.didUserTouchedMe(x, y)) {
                currentSubmarine = s1;
            }
            if (s2.didUserTouchedMe(x, y)) {
                currentSubmarine = s2;
            }
            if (s3.didUserTouchedMe(x, y)) {
                currentSubmarine = s3;
            }
            if (s4.didUserTouchedMe(x, y)) {
                currentSubmarine = s4;
            }
        }
    }

    public boolean validateCanStartTheGame() {
        boolean check = true;
        for (Submarine submarine : submarineArrayList) { // עובר על כל הצוללות ובודק אם הן מסודרות על הלוח ומחזיר ערך בולאני
            check &= submarine.isPlacedOnBoard();
        }
        return check;
    }

    public void resetSubmarineBoard() {
        for (int i = 0; i < player1SubmarinesBoard.length; i++) { // עובר על הלוח הדו מימדי של הצוללות
            for (int j = 0; j < player1SubmarinesBoard.length; j++) {
                player1SubmarinesBoard[i][j].setState(EMPTY);// הולך למודל ואז לשחקן שנמצא בו ומעדכן את כל הריבועים בלוח שלו לריקים
            }
        }
        for (Submarine submarine : submarineArrayList) { // עובר על כל הצוללות ומחזיר אותן למצבן ההתחלתי
            submarine.reset();
        }
        invalidate();
    }

    public void rotateSubmarine() {
        if (currentSubmarine != null) {
            currentSubmarine.rotateShape(); // מעביר אותה מאנכי לאופקי או להפך
            updateSubmarineAndBoard(currentSubmarine, currentSubmarine.getX(), currentSubmarine.getY()); // בעקבות הסיבוב צריך לעדכן את המיקום של הצוללת ואת הריבועים שנתפסים בהתאם
            invalidate();
        }
    }

    private void updateSubmarineAndBoard(Submarine submarine, int x, int y) {
        if (isInsideSubmarinesBoard(x, y)) { // אם הצוללת מונחת בתוך הלוח אז -
            for (int i = 0; i < player1SubmarinesBoard.length; i++) {
                for (int j = 0; j < player1SubmarinesBoard.length; j++) {
                    if (player1SubmarinesBoard[i][j].didUserTouchMe(x, y)) {
                        Square square = player1SubmarinesBoard[i][j];
                        //TODO: Fix bug when moving submarine to adjacent square it overrides occupied submarines
                        // and creates an ability for forbidden move - may be need to manage list of occupied submarines per square
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
        return isInsideBoard(player1SubmarinesBoard, x, y);
    }

    private boolean isInsideFireBoard(int x, int y) { // לוקח את המיקום של הירייה ובודק אם זה בתוך לוח היריות
        return isInsideBoard(player1FireBoard, x, y);
    }

    private boolean isInsideBoard(Square[][] boardPlayer, int x, int y) { // הפעולה שבודקת את הגבולות של הלוח יחד עם המיקום שהיא מקבלת

        return x >= boardPlayer[0][0].getX() && x <= boardPlayer[0][NUM_OF_SQUARES - 1].getX() + Square.SQUARE_SIZE
                && y >= boardPlayer[0][0].getY() && y <= boardPlayer[NUM_OF_SQUARES - 1][0].getY() + Square.SQUARE_SIZE;
    }

    private void updateOccupiedSquares(Submarine submarine) {
        ArrayList<Square> occupiedSquares = new ArrayList<>(); // יוצרים אריי ליסט של ריבועים תפוסים
        for (int i = 0; i < player1SubmarinesBoard.length; i++) { // עוברים על הלוח של הצוללות
            for (int j = 0; j < player1SubmarinesBoard.length; j++) {
                if (submarine.strictIntersectsWith(player1SubmarinesBoard[i][j])) { // אם הריבוע מוכל בצוללת שעליו אז -
                    player1SubmarinesBoard[i][j].setState(Square.SquareState.OCCUPIED_BY_SUBMARINE);
                    occupiedSquares.add(player1SubmarinesBoard[i][j]); // תוסיף אותו למערך הריבועים התפוסים
                    player1SubmarinesBoard[i][j].setOccupiedSubmarine(submarine); // כל ריבוע יודע איזה צוללת יש עליו אז זה מגדיר אותה
                } else if (submarine.intersectsWith(player1SubmarinesBoard[i][j])) { // אם הריבוע הוא מסביב לצוללת אז -
                    player1SubmarinesBoard[i][j].setState(Square.SquareState.OCCUPIED_BY_SUBMARINE_SURROUND);
                    occupiedSquares.add(player1SubmarinesBoard[i][j]);
                    player1SubmarinesBoard[i][j].setOccupiedSubmarine(submarine); // גם אם הריבוע מסביב לצוללת נגדיר שהצוללת על הריבוע הנל
                } else {
                    if (player1SubmarinesBoard[i][j].getOccupiedSubmarine() == submarine) { // אם הזזנו את הצוללת אז את הריבוע הקודם שהיא הייתה עליו נעביר לריק
                        player1SubmarinesBoard[i][j].setState(EMPTY);
                    }
                }
            }
        }
        submarine.updateOccupiedSquares(occupiedSquares); // לכל צוללת יש אריי של הריבועים שהיא תופסת אז זה מכניס לשם אותם
    }

    protected void initSubmarines() {
        submarineArrayList = new ArrayList<>(4);
        s1 = new Submarine(player1SubmarinesBoard[5][1].getX(), player1SubmarinesBoard[5][1].getY() + Square.SQUARE_SIZE * 2,
                Square.SQUARE_SIZE, Square.SQUARE_SIZE * 2);
        submarineArrayList.add(s1);

        s2 = new Submarine(player1SubmarinesBoard[5][2].getX(), player1SubmarinesBoard[5][2].getY() + Square.SQUARE_SIZE * 2,
                Square.SQUARE_SIZE, Square.SQUARE_SIZE * 2);
        submarineArrayList.add(s2);

        s3 = new Submarine(player1SubmarinesBoard[5][3].getX(), player1SubmarinesBoard[5][3].getY() + Square.SQUARE_SIZE * 2,
                Square.SQUARE_SIZE, Square.SQUARE_SIZE * 3);
        submarineArrayList.add(s3);

        s4 = new Submarine(player1SubmarinesBoard[5][4].getX(), player1SubmarinesBoard[5][4].getY() + Square.SQUARE_SIZE * 2,
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
                squares[j].draw(canvas,context1);
            }
        }
    }

    protected void drawFiredSquares(Square[][] boardPlayer, Canvas canvas) { // מצייר את הבומים מעל לצוללת
        for (Square[] squares : boardPlayer) {
            for (int j = 0; j < boardPlayer.length; j++) {
                if (squares[j].getState() == Square.SquareState.OCCUPIED_BY_SUBMARINE_AND_HIT) {
                    squares[j].draw(canvas,context1);
                }
            }
        }
    }

    public void initPlayer1SubmarinesBoard() {
        player1SubmarinesBoard = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];
        initSubmarinesBoard(player1SubmarinesBoard); // מאתחל את הלוח
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
        player1FireBoard = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];
        int x2 = 0;
        int y2 = player1SubmarinesBoard[5][5].getY() + 250;
        int w2 = Square.SQUARE_SIZE;

        for (int i = 0; i < player1FireBoard.length; i++) {
            for (int j = 0; j < NUM_OF_SQUARES; j++) {
                player1FireBoard[i][j] = new Square(x2, y2, w2, w2);
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
            submarineArrayList.get(i).draw(layout,context1);
        }
    }

    public void fireOnSubmarineAt(int x, int y) {
        if(!isGameOver) { // אם המשחק לא נגמר עדיין
            if (isInsideFireBoard(x, y)) { // אם לחצנו בתוך לוח היריות ולא בחוץ
                for (int i = 0; i < player1FireBoard.length; i++) {
                    for (int j = 0; j < player1FireBoard.length; j++) {
                        if (player1FireBoard[i][j].didUserTouchMe(x, y)) {
                            if(player1FireBoard[i][j].getState() == EMPTY) { // אם לא ירו לריבוע הזה כבר אז -
                                if (player1SubmarinesBoard[i][j].getState() == Square.SquareState.OCCUPIED_BY_SUBMARINE) { // אם יש צוללת בלוח צוללות של היריב
                                    player1FireBoard[i][j].setState(Square.SquareState.OCCUPIED_BY_SUBMARINE_AND_HIT);
                                    player1SubmarinesBoard[i][j].setState(Square.SquareState.OCCUPIED_BY_SUBMARINE_AND_HIT);
                                } else { // אם אין שם צוללות -
                                    player1FireBoard[i][j].setState(Square.SquareState.MISS);
                                    player1SubmarinesBoard[i][j].setState(Square.SquareState.MISS);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setupBoard() { // הכפתור המוזר ששם את הצוללות ישר על הלוח
        updateSubmarineAndBoard(s1, player1SubmarinesBoard[0][0].getX(), player1SubmarinesBoard[0][0].getY());
        updateSubmarineAndBoard(s2, player1SubmarinesBoard[3][0].getX(), player1SubmarinesBoard[3][0].getY());
        updateSubmarineAndBoard(s3, player1SubmarinesBoard[3][3].getX(), player1SubmarinesBoard[3][3].getY());
        updateSubmarineAndBoard(s4, player1SubmarinesBoard[0][5].getX(), player1SubmarinesBoard[0][5].getY());
    }
}
