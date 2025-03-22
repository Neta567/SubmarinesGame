package com.example.submarines.model;

import static com.example.submarines.boards.BoardGame.NUM_OF_SQUARES;

import java.util.ArrayList;

public class Player {
    private final String name;
    private final int[][] submarinesBoardModel; // מערך המיוצג על ידי אינטים של אינמים נשמר בפיירסבייס
    private Square[][] submarinesBoard; // מערך שמכיל ריבועים
    private final int[][] fireBoardModel;
    private Square[][] fireBoard;
    private ArrayList<Submarine> submarineArrayList;

    private PlayerGameStatus gameStatus = PlayerGameStatus.NOT_STARTED;

    public enum PlayerFields {
        name,
        fire_board,
        submarines_board,
        game_status
    }

    public enum PlayerGameStatus {
        NOT_STARTED,
        STARTED,
        WON,
        LOOSE
    }
    public Player(String player) {
        this.name = player;

        submarinesBoardModel = new int[NUM_OF_SQUARES][NUM_OF_SQUARES];
        fireBoardModel = new int[NUM_OF_SQUARES][NUM_OF_SQUARES];
    }

    public String getName() {
        return name;
    }

    public Square[][] initSubmarinesBoard() {
        if (submarinesBoard == null) {
            submarinesBoard = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];
        }
        return submarinesBoard;
    }

    public Square[][] initFireBoard() {
        if (fireBoard == null) {
            fireBoard = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];
        }
        return fireBoard;

    }

    public ArrayList<Submarine> initSubmarines() {
        if (submarineArrayList == null) {
            submarineArrayList = new ArrayList<>(4);
        }
        return submarineArrayList;
    }

    public void setSubmarineBoardSquareState(int i, int j, Square.SquareState state) {
       submarinesBoardModel[i][j] = state.getValue();
       submarinesBoard[i][j].setState(state);
    }

    public void updateSubmarinesBoard(int[][] subBoard) {
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
                if(fireBoard[i][j] != Square.SquareState.EMPTY.getValue()) { // מה שלא ריק
                    //submarinesBoardModel[i][j] = subBoard[i][j];
                    submarinesBoard[i][j].setState(Square.SquareState.fromValue(fireBoard[i][j])); // מעבירים את המצב של הלוח יריות ללוח צוללות שלי
                }
            }
        }
    }

    public void setFireBoardSquareState(int i, int j, Square.SquareState state) { // מעדכן מצב של ריבוע בלוח יריות שלי (פגעתי או לא)
        fireBoardModel[i][j] = state.getValue();
        fireBoard[i][j].setState(state);
    }

    public boolean isGameOver() {
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
