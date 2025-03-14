package com.example.submarines.model;

import static com.example.submarines.boards.BoardGame.NUM_OF_SQUARES;

public class Player {
    private final String name;
    private final int[][] submarineBoardModel;
    private final int[][] fireBoardModel;

    public Player(String player) {
        this.name = player;

        submarineBoardModel = new int[NUM_OF_SQUARES][NUM_OF_SQUARES];
        fireBoardModel = new int[NUM_OF_SQUARES][NUM_OF_SQUARES];
    }

    public String getName() {
        return name;
    }
}
