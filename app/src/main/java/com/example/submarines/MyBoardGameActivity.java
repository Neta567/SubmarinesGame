package com.example.submarines;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.submarines.boards.MyBoard;
import com.example.submarines.databinding.ActivityBoardGameBinding;
import com.example.submarines.model.GameModel;

import java.util.Random;

public class MyBoardGameActivity extends AppCompatActivity {

    private ActivityBoardGameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBoardGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GameModel.INSTANCE.gameId = ((Integer)new Random().nextInt(10000)).toString();
        FireBaseStore.INSTANCE.saveGame(GameModel.INSTANCE);

        MyBoard myBoard = new MyBoard(this);
        LinearLayout ll = findViewById(R.id.ll);
        ll.addView(myBoard);
    }
}