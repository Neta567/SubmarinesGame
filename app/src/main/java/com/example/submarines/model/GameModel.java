package com.example.submarines.model;

import static com.example.submarines.boards.BoardGame.NUM_OF_SQUARES;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.submarines.BR;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class GameModel extends BaseObservable {

    public enum GameState {
        NOT_STARTED,
        STARTED,
        FINISHED
    }

    private static final GameModel INSTANCE = new GameModel();

    private String gameId = "-1";
    private String gameResult = "";
    private String currentPlayer = "Player 1";
    private final int[][] player1SubmarineBoardModel;
    private Square[][] player1SubmarineBoard;
    private Square[][] player1FireBoard;
    private ArrayList<Submarine> submarineArrayList;
    private GameState gameState = GameState.NOT_STARTED;

    private Submarine currentSubmarine;

    private GameModel() {

        int NUM_OF_SQUARES = 6;
        player1SubmarineBoardModel = new int[NUM_OF_SQUARES][NUM_OF_SQUARES];
    }

    public static GameModel getInstance() {
        return INSTANCE;
    }
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        if(gameState == GameState.STARTED) {
            currentSubmarine = null;
            notifyPropertyChanged(BR.gameStarted);
        }
    }
    @Bindable
    public String getGameResult() {
        return gameResult;
    }

    @Bindable
    public boolean isGameStarted() {
        return gameState == GameState.STARTED;
    }

    public void setGameResult(String gameResult) {
        this.gameResult = gameResult;
    }

    @Bindable
    public String getCurrentPlayerName() {
        return currentPlayer;
    }

    public void setCurrentSubmarine(Submarine submarine) {
        currentSubmarine = submarine;
    }

    public Submarine getCurrentSubmarine() {
        return currentSubmarine;
    }
    public void setCurrentPlayer(String player) {
        currentPlayer = player;
        notifyPropertyChanged(BR.currentPlayerName);
    }

    public void setSquareState(int i, int j, Square.SquareState state) {
        player1SubmarineBoardModel[i][j] = state.getValue();
    }

    public ArrayList<Submarine> initPlayer1Submarines() {
        if(submarineArrayList == null){
            submarineArrayList = new ArrayList<>(4);
        }
        return submarineArrayList;
    }
    public Square[][] initPlayer1SubmarinesBoard() {
        return initSquareBoard(player1SubmarineBoard);
    }
    public Square[][] initPlayer1FireBoard() {
        return initSquareBoard(player1FireBoard);
    }

    private Square[][] initSquareBoard(Square[][] squareBoard) {
        if(squareBoard == null){
            squareBoard = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];
        }
        return squareBoard;
    }

    public String getPlayer1BoardState() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(player1SubmarineBoardModel);
    }

}
