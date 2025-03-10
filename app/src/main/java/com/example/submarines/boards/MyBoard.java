package com.example.submarines.boards;

import android.content.Context;
import android.graphics.Canvas;

import androidx.annotation.NonNull;

import com.example.submarines.model.GameModel;

public class MyBoard extends BoardGame {

    public MyBoard(Context context) {

        super(context);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        initBoards(canvas);
        drawBoard(boardPlayer1, canvas);
        drawSubmarines(canvas);

        if(GameModel.getInstance().isGameStarted())
        {
            drawBoard(boardPlayer2, canvas);
            drawFiredSquares(boardPlayer1, canvas);
        }
    }
}


