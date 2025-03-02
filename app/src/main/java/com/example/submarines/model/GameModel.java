package com.example.submarines.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.submarines.BR;

public class GameModel extends BaseObservable {

    public static GameModel INSTANCE = new GameModel();

    public String gameId = "-1";
    public String gameResult = "";
    private String currentPlayer = "Player 1";

    @Bindable
    public String getCurrentPlayerName() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String player) {
        currentPlayer = player;
        notifyPropertyChanged(BR.currentPlayerName);
    }
}
