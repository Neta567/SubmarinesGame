package com.example.submarines;

import android.content.Context;
import android.graphics.Canvas;

import androidx.annotation.NonNull;

public class MyBoard extends BoardGame {

    public MyBoard(Context context) {
        super(context);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        initBoards(canvas);
        //initShips(canvas);
        drawBoard1(canvas);
        //drawShips(canvas);
        drawSubmarine(canvas);

        //initsubmarines(canvas);
        //drawSubmarine(canvas);
        drawRotationButton(canvas);
        //drawErasureButton(canvas);
        drawMoveButton(canvas);
        if(isTwoPlayers)
        {
            // TODO: 14/01/2025 draw second board
            drawBoard2(canvas);
        }
    }
}


