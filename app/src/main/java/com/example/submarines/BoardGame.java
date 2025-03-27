package com.example.submarines;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class BoardGame extends View {

    protected GameModel model = GameModel.getInstance(); //מקבל מודל אחד ויחיד
    protected Square[][] player1SubmarinesBoard, player2SubmarinesBoard, player1FireBoard; //לוח ספינות שלי ושל השני ולוח יריות שלי
    protected ArrayList<Submarine> submarineArrayList;
    public static final int NUM_OF_SQUARES = 6;
    private boolean firstTimeBoard = true;
    private boolean firstTimeSubmarine = true;
    protected Submarine s1, s2, s3, s4;
    private Callable<Void> onFireEventCallable; // מייצר קריאה בשביל להפעיל א הדיאלוג
    private final FireBaseStore fireBaseStore = new FireBaseStore();
    private Context context1;

    public BoardGame(Context context) {
        super(context);
        context1 = context;

    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        initBoards(canvas);
        drawBoard(player1SubmarinesBoard, canvas);
        drawSubmarines(canvas);

        if(GameModel.getInstance().isGameStarted() ||
                GameModel.getInstance().getGameState() == GameModel.GameState.GAME_OVER )
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
        Submarine submarine = GameModel.getInstance().getCurrentSubmarine(); // לוקחים את הצוללת האחרונה שנגעו בה
        if (submarine != null) {
            updateSubmarineAndBoard(submarine, x, y); // מעדכנים את המיקום הסופי של הצוללת על המסך
        }
        invalidate();
    }

    private void handleActionMoveEvent(int x, int y) {
        Submarine submarine = GameModel.getInstance().getCurrentSubmarine(); // לוקחים את הצוללת האחרונה שנגעו בה
        if (submarine != null) {
            submarine.updateLocation(x, y); // מעדכנים את המיקום שלה על המסך
        }
        invalidate();
    }

    protected void handleActionDownEvent(int x, int y) {
        if (GameModel.getInstance().isGameStarted()) { // אם המשחק התחיל הפעולה היחידה שאפשר לעשות זה יריות אז נקרא לפעולה של היריות
            fireOnSubmarineAt(x, y);
        } else { // אם המשחק לא התחיל אז נשמור את הצוללות האחרונה שנגעו בה
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
        for (Submarine submarine : submarineArrayList) { // עובר על כל הצוללות ובודק אם הן מסודרות על הלוח ומחזיר ערך בולאני
            check &= submarine.isPlacedOnBoard();
        }
        return check;
    }

    public void resetSubmarineBoard() {
        for (int i = 0; i < player1SubmarinesBoard.length; i++) { // עובר על הלוח הדו מימדי של הצוללות
            for (int j = 0; j < player1SubmarinesBoard.length; j++) {
                model.setSubmarineBoardSquareState(i, j, Square.SquareState.EMPTY); // הולך למודל ואז לשחקן שנמצא בו ומעדכן את כל הריבועים בלוח שלו לריקים
            }
        }
        for (Submarine submarine : submarineArrayList) { // עובר על כל הצוללות ומחזיר אותן למצבן ההתחלתי
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
        fireBaseStore.saveGame(model);
        invalidate();
    }

    public void rotateSubmarine() {
        Submarine submarine = GameModel.getInstance().getCurrentSubmarine(); // לוקח את הצוללת האחרונה שנגעו בה
        if (submarine != null) {
            submarine.rotateShape(); // מעביר אותה מאנכי לאופקי או להפך
            updateSubmarineAndBoard(submarine, submarine.getX(), submarine.getY()); // בעקבות הסיבוב צריך לעדכן את המיקום של הצוללת ואת הריבועים שנתפסים בהתאם
            invalidate();
        }
    }

    private void updateSubmarineAndBoard(Submarine submarine, int x, int y) {

        if (isInsideSubmarinesBoard(x, y)) { // אם הצוללת מונחת בתוך הלוח אז -

            findSquareAtBoardAndApplyAction(player1SubmarinesBoard, x, y, (i, j) -> { // מוצאים באיזה ריבוע הייתה נגיעה האחרונה כלומר זה הריבוע הראשון של הצוללת
                Square square = player1SubmarinesBoard[i][j];
                //TODO: Fix bug when moving submarine to adjacent square it overrides occupied submarines
                // and creates an ability for forbidden move - may be need to manage list of occupied submarines per square
                if (square.getState() == Square.SquareState.EMPTY ||
                        square.getState() != Square.SquareState.OCCUPIED_BY_SUBMARINE
                                && square.getOccupiedSubmarine() == submarine) { // אם שמו את הצוללת על ריבוע ריק או על ריבוע שאין בו צוללת אחרת והצוללת שיש שם זאת הצוללת הנוכחית אז -
                    submarine.updateLocation(square.getX(), square.getY()); // מעדכן את המיקום הסופי של הצוללת
                } else if (square.getOccupiedSubmarine() != submarine) { // אם הריבוע תפוס עם צוללת אחרת אז תחזיר את הצוללת אחורה
                    submarine.reset();
                }
            });
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
                    model.setSubmarineBoardSquareState(i, j, Square.SquareState.OCCUPIED_BY_SUBMARINE); // תשנה את המצב של הריבוע לתפוס על ידיי צוללת
                    occupiedSquares.add(player1SubmarinesBoard[i][j]); // תוסיף אותו למערך הריבועים התפוסים
                    player1SubmarinesBoard[i][j].setOccupiedSubmarine(submarine); // כל ריבוע יודע איזה צוללת יש עליו אז זה מגדיר אותה
                } else if (submarine.intersectsWith(player1SubmarinesBoard[i][j])) { // אם הריבוע הוא מסביב לצוללת אז -
                    model.setSubmarineBoardSquareState(i, j, Square.SquareState.OCCUPIED_BY_SUBMARINE_SURROUND); // תשנה את המצב שלו שהוא המסגרת של הצוללת
                    player1SubmarinesBoard[i][j].setOccupiedSubmarine(submarine); // גם אם הריבוע מסביב לצוללת נגדיר שהצוללת על הריבוע הנל
                } else {
                    if (player1SubmarinesBoard[i][j].getOccupiedSubmarine() == submarine) { // אם הזזנו את הצוללת אז את הריבוע הקודם שהיא הייתה עליו נעביר לריק
                        model.setSubmarineBoardSquareState(i, j, Square.SquareState.EMPTY);
                    }
                }
            }
        }
        submarine.updateOccupiedSquares(occupiedSquares); // לכל צוללת יש אריי של הריבועים שהיא תופסת אז זה מכניס לשם אותם
    }

    protected void initSubmarines() {
        submarineArrayList = GameModel.getInstance().initPlayer1Submarines(); // הולכים למודל ולוקחים משם את המצב ההתחלתי של הצוללות

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
            initPlayer2SubmarinesBoard();
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
        player1SubmarinesBoard = GameModel.getInstance().initPlayer1SubmarinesBoard(); // לוקח את הלוח של הצוללות שלי
        initSubmarinesBoard(player1SubmarinesBoard); // מאתחל את הלוח
    }

    public void initPlayer2SubmarinesBoard() {
        player2SubmarinesBoard = GameModel.getInstance().initPlayer2SubmarinesBoard(); // לוקח את לוח הצוללות של השחקן השני
        initSubmarinesBoard(player2SubmarinesBoard); // מאתחל את הלוח
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
        player1FireBoard = GameModel.getInstance().initPlayer1FireBoard();
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
        if(!GameModel.getInstance().isGameOver()) { // אם המשחק לא נגמר עדיין
            if (isInsideFireBoard(x, y)) { // אם לחצנו בתוך לוח היריות ולא בחוץ
                findSquareAtBoardAndApplyAction(player1FireBoard, x, y, (i, j) -> { // מפעולה זו נקבל את המיקום של הריבוע ואז תתבצע הפעולה הבאה -
                    if(player1FireBoard[i][j].getState() == Square.SquareState.EMPTY) { // אם לא ירו לריבוע הזה כבר אז -
                        if (player2SubmarinesBoard[i][j].getState() == Square.SquareState.OCCUPIED_BY_SUBMARINE) { // אם יש צוללת בלוח צוללות של היריב
                            model.setFireBoardSquareState(i, j, Square.SquareState.OCCUPIED_BY_SUBMARINE_AND_HIT); // תהפוך את המצב של הריבוע בלוח יריות של מי שירה לפגיעה
                            model.setPlayer2SubmarineBoardSquareState(i, j, Square.SquareState.OCCUPIED_BY_SUBMARINE_AND_HIT); // תעדכן את מצב הריבוע בלוח הצוללות של מי שנפגע
                        } else { // אם אין שם צוללות -
                            model.setFireBoardSquareState(i, j, Square.SquareState.MISS); // תעדכן אצל מי שירה שהוא פספס
                            model.setPlayer2SubmarineBoardSquareState(i, j, Square.SquareState.MISS); // תעדכן אצל מי שירו עליו שירו אבל פספסו
                        }
                        try {
                            onFireEventCallable.call(); // מודיעים שקרה ירי ואז מציגים את הדיאלוג ומעבירים את התור לשחקן השני
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        fireBaseStore.saveGame(GameModel.getInstance()); // לשמור את השינויים שקרו בלוחות
                    }
                });
            }
        }
    }

    public void findSquareAtBoardAndApplyAction(Square[][] board, int x, int y, SquareActionCallback callback) { // פעולה שמחזירה את המיקום של הריבוע שירו עליו
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j].didUserTouchMe(x, y)) {
                    callback.performAction(i, j);
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

    public void subscribeOnFireEvent(Callable<Void> callable) { // נרשם לכל פעולה שהשחקן השני עושה בלוח יריות שלו ומגיב בהתאם
        this.onFireEventCallable = callable;
    }
}
