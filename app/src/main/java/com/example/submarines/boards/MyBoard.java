package com.example.submarines.boards;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import androidx.annotation.NonNull;

import com.example.submarines.R;
import com.example.submarines.buttons.ErasureButton;
import com.example.submarines.buttons.GameButton;
import com.example.submarines.buttons.RotationButton;
import com.example.submarines.buttons.StartGameButton;

public class MyBoard extends BoardGame {

    public MyBoard(Context context) {

        super(context);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        initBoards(canvas);
        initButtons();
        //initShips(canvas);
        drawBoard1(canvas);
        //drawShips(canvas);
        drawSubmarines(canvas);
        drawButtons(canvas);

        if(isTwoPlayers)
        {
            // TODO: 14/01/2025 draw second board
            drawBoard2(canvas);
        }
    }

    private void initButtons() {

        if(firstTimeButtons) {
            Bitmap rotateBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rotation_button);
            rotationButton = new RotationButton(squareSize, squareSize, rotateBitmap);
            gameButtons.add(rotationButton);

            Bitmap erasueBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.trash_can);
            erasureButton = new ErasureButton(squareSize, squareSize, erasueBitmap);
            gameButtons.add(erasureButton);

            Bitmap startBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.check_mark1);
            startGameButton = new StartGameButton(squareSize,squareSize,startBitmap);
            gameButtons.add(startGameButton);

            firstTimeButtons = false;
        }
    }

    private void drawButtons(Canvas canvas) {

        int currentX = boardPlayer1[5][0].getX();
        for (GameButton button: gameButtons) {
            button.setX(currentX);
            button.setY(boardPlayer1[5][5].getY() + squareSize*9);
            button.draw(canvas);

            currentX += 2*squareSize;
        }
    }
}


