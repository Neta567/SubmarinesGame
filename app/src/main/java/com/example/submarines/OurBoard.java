package com.example.submarines;

import android.content.Context;
import android.graphics.Canvas;

import androidx.annotation.NonNull;

public class OurBoard extends BoardGame {
    public OurBoard(Context context) {
        super(context);


    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        initBoards(canvas);
        drawBoard1(canvas);
        drawBoard2(canvas);
    }
}
