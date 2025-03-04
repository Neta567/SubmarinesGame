package com.example.submarines.boards;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.submarines.R;
import com.example.submarines.model.Square;
import com.example.submarines.model.Submarine;

public class MyNewBoard extends BoardGame {

    public MyNewBoard(Context context) {

        super(context);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        drawSubmarines(canvas);
    }
    @Override
    protected void initSubmarines(Canvas canvas)
    {
        s1 = new Submarine(boardPlayer1[0][1].getX(),boardPlayer1[0][0].getY(), this.getResources(), squareSize, squareSize*2);
        submarineArrayList.add(s1);

        s2 = new Submarine(boardPlayer1[0][2].getX(),boardPlayer1[0][0].getY(), this.getResources(), squareSize, squareSize*2);
        submarineArrayList.add(s2);

        s3 = new Submarine(boardPlayer1[0][3].getX(),boardPlayer1[0][0].getY(), this.getResources(), squareSize, squareSize*3);
        submarineArrayList.add(s3);

        s4 = new Submarine(boardPlayer1[0][4].getX(),boardPlayer1[0][0].getY(), this.getResources(), squareSize, squareSize*4);
        submarineArrayList.add(s4);
    }
    public void addPlayerShipsBoard(GridLayout layout) {
        addPlayerBoard(layout, boardPlayer1, boardTextViewPlayer1);
    }

    public void addPlayerFireBoard(GridLayout layout) {
        addPlayerBoard(layout, boardPlayer2, boardTextViewPlayer2);
    }
    private void addPlayerBoard(GridLayout layout, Square[][] board, TextView[][] boardTextView) {

        //squareSize = layout.getRootView().getWidth() / 3 / NUM_OF_SQUARES + 80;
        squareSize = 150;
        int x1 = 0;
        int y1 = 50;
        int w1 = squareSize;

        for (int row = 0; row < NUM_OF_SQUARES; row++) {
            for (int col = 0; col < NUM_OF_SQUARES; col++) {
                board[row][col] = new Square(x1, y1, w1, w1);

                TextView square = new TextView(this.getContext());
                square.setWidth(w1); // Set the width of each square
                square.setHeight(w1); // Set the height of each square
                square.setGravity(Gravity.CENTER_HORIZONTAL);
                square.setTag(row + "_" + col);
                //square.setX(x1);
                //square.setY(y1);

                square.setBackgroundColor(Color.WHITE);
                square.setBackgroundResource(R.drawable.square_border);
                square.setAlpha(0.7F);

                boardTextView[row][col] = square;

                // Add the square to the GridLayout
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                square.setLayoutParams(params);
                layout.addView(square);
                x1 = x1+squareSize;
            }
            x1 = layout.getWidth()/9;
            y1 = y1 + w1;
        }
    }

}

