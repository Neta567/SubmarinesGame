package com.example.submarines;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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
        setContentView(binding.rootLayout);

        GameModel.getInstance().setGameId(((Integer)new Random().nextInt(10000)).toString());
        binding.setViewModel(GameModel.getInstance());
        FireBaseStore.INSTANCE.saveGame(GameModel.getInstance());

        MyBoard myBoard = new MyBoard(this);
        binding.ll.addView(myBoard);
    }
}