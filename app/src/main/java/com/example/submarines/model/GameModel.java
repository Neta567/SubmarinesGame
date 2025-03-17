package com.example.submarines.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.example.submarines.BR;
import com.google.firebase.firestore.Exclude;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameModel extends BaseObservable {

    public enum GameState {
        NOT_STARTED,
        ONE_PLAYER_JOINED,
        TWO_PLAYERS_JOINED,
        STARTED,
        GAME_OVER
    }

    public enum GameModelFields {
        game_id,
        game_state,
        game_result
    }

    private static final GameModel INSTANCE = new GameModel();

    private String gameId = "-1";
    private String gameResult = "";
    private final Player[] players = new Player[2];
    private GameState gameState = GameState.NOT_STARTED;
    private Submarine currentSubmarine;

    private GameModel() {}

    public static GameModel getInstance() {
        return INSTANCE;
    }

    public String getGameId() {
        return gameId;
    }
    @Exclude
    public GameState getGameState() {
        return gameState;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        if (gameState == GameState.STARTED) {
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
        return players[0].getName();
    }

    @Exclude
    @Bindable
    public String getOtherPlayer() {
        return players[1].getName();
    }

    public void setCurrentPlayer(String player) {
        players[0] = new Player(player);
        notifyPropertyChanged(BR.currentPlayerName);
    }

    public void setOtherPlayer(String otherPlayer) {
        players[1] = new Player(otherPlayer);
        notifyPropertyChanged(BR.otherPlayer);
    }

    public void setCurrentSubmarine(Submarine submarine) {
        currentSubmarine = submarine;
    }
    @Exclude
    public Submarine getCurrentSubmarine() {
        return currentSubmarine;
    }

    public void setSubmarineBoardSquareState(int i, int j, Square.SquareState state) {
        players[0].setSubmarineBoardSquareState(i, j, state);
    }

    public void setFireBoardSquareState(int i, int j, Square.SquareState state) {
        players[0].setFireBoardSquareState(i, j, state);
    }

    public ArrayList<Submarine> initPlayer1Submarines() {
        return players[0].initSubmarines();
    }

    public Square[][] initPlayer1SubmarinesBoard() {
        return players[0].initSubmarinesBoard();
    }

    public Square[][] initPlayer1FireBoard() {
        return players[0].initFireBoard();
    }

    @Exclude
    public boolean isGameOver() {
        return players[0].isGameOver();
                //|| players[1].isGameOver();
    }

    public Map<String, Object> getGame() {
        HashMap<String, Object> game = new HashMap<>();
        //game.put("gameId", gameId);
        game.put(GameModelFields.game_state.toString(), gameState.toString());

        return game;
    }

    public Map<String, Object> getPlayer() {
        HashMap<String, Object> player = new HashMap<>();
        player.put("playerName", players[0].getName());

        Gson gson = new Gson();
        player.put("fireBoard", gson.toJson(players[0].getFireBoardModel()));
        player.put("submarinesBoard", gson.toJson(players[0].getSubmarinesBoardModel()));

        return player;
    }
}
