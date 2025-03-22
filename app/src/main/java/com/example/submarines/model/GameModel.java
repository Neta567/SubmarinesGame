package com.example.submarines.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.example.submarines.BR;
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
        }
    }

    public String getGameResult() {

        String gameResult = "In Progress";
        if(gameState == GameState.GAME_OVER) {
            if (players[0].isGameOver())
                gameResult = players[1].getName() + " WON";
            else
                gameResult = players[0].getName() + " WON";
        }
        return gameResult;
    }

    public boolean isGameStarted() {
        return gameState == GameState.STARTED;
    }

    @Bindable
    public String getCurrentPlayerName() {
        return players[0].getName();
    }

    @Bindable
    public String getCurrentPlayerStatus() {
        return getCurrentPlayerGameStatus().toString();
    }

    public Player.PlayerGameStatus getCurrentPlayerGameStatus() {
        return players[0].getGameStatus();
    }

    @Bindable
    public String getOtherPlayer() {
        return players[1].getName();
    }

    public Player.PlayerGameStatus getOtherPlayerGameStatus() {
        return players[1].getGameStatus();
    }

    public void setCurrentPlayer(String player) {
        players[0] = new Player(player);
        notifyPropertyChanged(BR.currentPlayerName);
    }

    public void setCurrentPlayerGameStatus(Player.PlayerGameStatus playerGameStatus) {
        players[0].setGameStatus(playerGameStatus);
        notifyPropertyChanged(BR.currentPlayerStatus);
    }

    public void setOtherPlayer(String otherPlayer) {
        players[1] = new Player(otherPlayer);
        notifyPropertyChanged(BR.otherPlayer);
    }

    public void setOtherPlayerGameStatus(Player.PlayerGameStatus playerGameStatus) {
        players[1].setGameStatus(playerGameStatus);
    }

    public void setCurrentSubmarine(Submarine submarine) {
        currentSubmarine = submarine;
    }
    public Submarine getCurrentSubmarine() {
        return currentSubmarine;
    }

    public void setSubmarineBoardSquareState(int i, int j, Square.SquareState state) {
        players[0].setSubmarineBoardSquareState(i, j, state);
    }

    public void setPlayer2SubmarineBoardSquareState(int i, int j, Square.SquareState state) {
        players[1].setSubmarineBoardSquareState(i, j, state);
    }

    public void updatePlayer2SubmarinesBoard(int[][] subBoard) {
        players[1].updateSubmarinesBoard(subBoard);
    }

    public void updatePlayer1SubmarinesBoardAfterFire(int[][] fireBoard) {
        players[0].updateSubmarinesBoardAfterFire(fireBoard);
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

    public Square[][] initPlayer2SubmarinesBoard() {
        return players[1].initSubmarinesBoard();
    }

    public Square[][] initPlayer1FireBoard() {
        return players[0].initFireBoard();
    }

    public boolean isGameOver() {
        return players[0].isGameOver();
                //|| players[1].isGameOver();
    }

    public Map<String, Object> getGame() {
        HashMap<String, Object> game = new HashMap<>();
        game.put(GameModelFields.game_id.toString(), gameId);
        game.put(GameModelFields.game_state.toString(), gameState.toString());
        game.put(GameModelFields.game_result.toString(), getGameResult());

        return game;
    }

    public Map<String, Object> getPlayer() {
        HashMap<String, Object> player = new HashMap<>();
        player.put(Player.PlayerFields.name.toString(), players[0].getName());
        player.put(Player.PlayerFields.game_status.toString(), players[0].getGameStatus().toString());

        Gson gson = new Gson();
        player.put(Player.PlayerFields.fire_board.toString(),
                gson.toJson(players[0].getFireBoardModel()));
        player.put(Player.PlayerFields.submarines_board.toString(),
                gson.toJson(players[0].getSubmarinesBoardModel()));

        return player;
    }
}
