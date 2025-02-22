package com.example.submarines;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

public class MyBoardGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_game);

        MyBoard myBoard = new MyBoard(this);
        LinearLayout ll = findViewById(R.id.ll);
        ll.addView(myBoard);
    }
}