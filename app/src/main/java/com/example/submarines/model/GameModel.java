package com.example.submarines.model;

public class GameModel {

    public static GameModel INSTANCE = new GameModel();

    public String gameId = "-1";
    public String gameResult = "";
    private String player1Name = "Player 1";
    private String player2Name = "Player 2";

    public String getCurrentPlayerName() {
        return player1Name;
    }
}
