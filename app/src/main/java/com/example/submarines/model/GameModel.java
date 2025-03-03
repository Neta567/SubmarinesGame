package com.example.submarines.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.submarines.BR;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GameModel extends BaseObservable {

    public enum SquareState {
        EMPTY(0),
        OCCUPIED_BY_SUBMARINE(2),
        OCCUPIED_BY_SUBMARINE_SURROUND(1);

        private final int value;

        SquareState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    };

    private static final GameModel INSTANCE = new GameModel();

    private String gameId = "-1";
    private String gameResult = "";
    private String currentPlayer = "Player 1";
    private int[][] boardModel1;
    private final int NUM_OF_SQUARES = 6;

    private GameModel() {

        boardModel1 = new int[NUM_OF_SQUARES][NUM_OF_SQUARES];
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
    @Bindable
    public String getGameResult() {
        return gameResult;
    }

    public void setGameResult(String gameResult) {
        this.gameResult = gameResult;
    }

    @Bindable
    public String getCurrentPlayerName() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String player) {
        currentPlayer = player;
        notifyPropertyChanged(BR.currentPlayerName);
    }

    public void setSquareState(int i, int j, SquareState state) {
        boardModel1[i][j] = state.getValue();
    }

    public String getPlayer1BoardState() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(boardModel1);
    }

}
