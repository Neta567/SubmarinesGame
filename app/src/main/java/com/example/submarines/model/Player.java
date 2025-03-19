package com.example.submarines.model;

import static com.example.submarines.boards.BoardGame.NUM_OF_SQUARES;

import java.util.ArrayList;

public class Player {
    private final String name;
    private final int[][] submarinesBoardModel;
    private Square[][] submarinesBoard;
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
                submarinesBoard[i][j].setState(Square.SquareState.fromValue(subBoard[i][j]));
            }
        }
    }

    public void updateSubmarinesBoardAfterFire(int[][] fireBoard) {
        for (int i = 0; i < NUM_OF_SQUARES; i++) {
            for (int j = 0; j < NUM_OF_SQUARES; j++) {
                if(fireBoard[i][j] != Square.SquareState.EMPTY.getValue()) {
                    //submarinesBoardModel[i][j] = subBoard[i][j];
                    submarinesBoard[i][j].setState(Square.SquareState.fromValue(fireBoard[i][j]));
                }
            }
        }
    }

    public void setFireBoardSquareState(int i, int j, Square.SquareState state) {
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
