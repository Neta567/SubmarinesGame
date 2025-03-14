package com.example.submarines.model;

import static com.example.submarines.boards.BoardGame.NUM_OF_SQUARES;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.example.submarines.BR;
import com.google.firebase.firestore.Exclude;
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
    private final int[][] player1SubmarineBoardModel;
    private final int[][] player1FireBoardModel;
    private Square[][] player1SubmarineBoard;
    private Square[][] player1FireBoard;
    private ArrayList<Submarine> submarineArrayList;
    private GameState gameState = GameState.NOT_STARTED;
    private Submarine currentSubmarine;

    private GameModel() {

        int NUM_OF_SQUARES = 6;
        player1SubmarineBoardModel = new int[NUM_OF_SQUARES][NUM_OF_SQUARES];
        player1FireBoardModel = new int[NUM_OF_SQUARES][NUM_OF_SQUARES];

    }

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
        player1SubmarineBoardModel[i][j] = state.getValue();
        player1SubmarineBoard[i][j].setState(state);
    }

    public void setFireBoardSquareState(int i, int j, Square.SquareState state) {
        player1FireBoardModel[i][j] = state.getValue();
        player1FireBoard[i][j].setState(state);
    }

    public ArrayList<Submarine> initPlayer1Submarines() {
        if (submarineArrayList == null) {
            submarineArrayList = new ArrayList<>(4);
        }
        return submarineArrayList;
    }

    public Square[][] initPlayer1SubmarinesBoard() {
        if (player1SubmarineBoard == null) {
            player1SubmarineBoard = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];
        }
        return player1SubmarineBoard;
    }

    public Square[][] initPlayer1FireBoard() {
        if (player1FireBoard == null) {
            player1FireBoard = new Square[NUM_OF_SQUARES][NUM_OF_SQUARES];
        }
        return player1FireBoard;
    }

    @Exclude
    public boolean isGameOver() {
        return submarineArrayList.stream().allMatch(Submarine::isDestroyed);
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

        return player;
    }
}
