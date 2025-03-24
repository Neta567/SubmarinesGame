package com.example.submarines.model;

import static com.example.submarines.boards.BoardGame.NUM_OF_SQUARES;

import java.util.ArrayList;

public class Player {
    // מייצר את ה2 לוחות עבור השחקן שלי
    // משנה מצבים של הריבועים בלוח יריות שלי
    // משנה מצב של צוללות בלוח יריות שלי לפי לוח יריות של השני שנקבל
    private final String name;
    private final int[][] submarinesBoardModel; // מערך המיוצג על ידי אינטים של אינמים נשמר בפיירסבייס
    private Square[][] submarinesBoard; // מערך שמכיל ריבועים
    private final int[][] fireBoardModel; // מערך של אינטים ללוח יריות שלי?
    private Square[][] fireBoard;
    private ArrayList<Submarine> submarineArrayList; // מערך של הצוללות שלי

    private PlayerGameStatus gameStatus = PlayerGameStatus.NOT_STARTED; // מצב השחקן שלי

    public enum PlayerFields { // השדות שמופיעים בקולקשן של השחקן
        name,
        fire_board,
        submarines_board,
        game_status
    }

    public enum PlayerGameStatus { // המצבים שהשחקן יכול להיות בהם
        NOT_STARTED,
        STARTED,
        WON,
        LOOSE
    }
    public Player(String player) { // יצירת שחקן חדש
        this.name = player;

        submarinesBoardModel = new int[NUM_OF_SQUARES][NUM_OF_SQUARES]; // יוצר לוח צוללות חדש
        fireBoardModel = new int[NUM_OF_SQUARES][NUM_OF_SQUARES]; // יוצר לוח יריות חדש
    }

    public String getName() {
        return name;
    }

    public Square[][] initSubmarinesBoard() { // יוצר את לוח הצוללות
        if (submarinesBoard == null) {
            submarinesBoard = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];
        }
        return submarinesBoard;
    }

    public Square[][] initFireBoard() { // יוצר את לוח היריות
        if (fireBoard == null) {
            fireBoard = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];
        }
        return fireBoard;

    }

    public ArrayList<Submarine> initSubmarines() { // יוצר את המערך צוללות
        if (submarineArrayList == null) {
            submarineArrayList = new ArrayList<>(4);
        }
        return submarineArrayList;
    }

    public void setSubmarineBoardSquareState(int i, int j, Square.SquareState state) { // שולחים לו מיקום של ריבוע והוא משנה את המצב שלו בלוח
       submarinesBoardModel[i][j] = state.getValue();
       submarinesBoard[i][j].setState(state);
    }

    public void updateSubmarinesBoard(int[][] subBoard) { // אם קרה שינוי בלוח צוללות אז זה הולך לפה ושולחים לו את הלוח המעודכן והוא משנה בהתאם את הלוח האינטים והאינמים שלו
        for (int i = 0; i < NUM_OF_SQUARES; i++) {
            for (int j = 0; j < NUM_OF_SQUARES; j++) {
                submarinesBoardModel[i][j] = subBoard[i][j];
                submarinesBoard[i][j].setState(Square.SquareState.fromValue(subBoard[i][j])); // הופך את הלוח צוללות של השני לאינמים
            }
        }
    }

    public void updateSubmarinesBoardAfterFire(int[][] fireBoard) { // מקבל מערך של מספרים של הלוח יריות של השני
        for (int i = 0; i < NUM_OF_SQUARES; i++) { // עוברים עליו
            for (int j = 0; j < NUM_OF_SQUARES; j++) {
                if(fireBoard[i][j] != Square.SquareState.EMPTY.getValue()) { // מה שלא ריק בלוח יריות של השני - כלומר איפה שהוא ירה
                    //submarinesBoardModel[i][j] = subBoard[i][j];
                    submarinesBoard[i][j].setState(Square.SquareState.fromValue(fireBoard[i][j])); // מעבירים את המצב של הלוח יריות ללוח צוללות שלי - איפה שהוא ירה נסמן על הלוח שלי
                }
            }
        }
    }

    public void setFireBoardSquareState(int i, int j, Square.SquareState state) { // מעדכן מצב של ריבוע בלוח יריות שלי (פגעתי או לא)
        fireBoardModel[i][j] = state.getValue();
        fireBoard[i][j].setState(state);
    }

    public boolean isGameOver() { // בודק בסטרים אם כל הצוללות הרוסות כלומר כל הריבועים שהיא עליהם הם הרוסים ופגועים
        return submarineArrayList.stream().allMatch(Submarine::isDestroyed);
    }

    public int[][] getFireBoardModel() {
        return fireBoardModel;
    }

    public int[][] getSubmarinesBoardModel() {
        return submarinesBoardModel;
    }

    public void setGameStatus(PlayerGameStatus playerGameStatus) {
        this.gameStatus = playerGameStatus;
    }

    public PlayerGameStatus getGameStatus() {
        return this.gameStatus;
    }
}
