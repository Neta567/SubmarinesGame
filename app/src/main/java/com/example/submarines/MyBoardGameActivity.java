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
        //BoardGame boardGame = new BoardGame(this);
        //OurBoard ourBoard = new OurBoard(this);



        LinearLayout ll = findViewById(R.id.ll);
        //ll.setBackgroundColor(Color.argb(150,200,255,255));
        ll.addView(myBoard);
        //ll.addView(ourBoard);



    }
}