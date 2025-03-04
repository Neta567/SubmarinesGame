package com.example.submarines;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
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

        binding.startGameButton2.setOnClickListener(v -> {
            if(myBoard.validateCanStartTheGame()) {
                Toast.makeText(myBoard.getContext(), "Game Started", Toast.LENGTH_SHORT).show();
                GameModel.getInstance().setGameState(GameModel.GameState.STARTED);
                binding.rotationButton2.setEnabled(false);
                binding.rotationButton2.setVisibility(View.INVISIBLE);
                binding.erasureButton2.setEnabled(false);
                binding.erasureButton2.setVisibility(View.INVISIBLE);
                FireBaseStore.INSTANCE.saveGame(GameModel.getInstance());
                myBoard.invalidate();
            } else {
                Toast.makeText(myBoard.getContext(), "Not all submarines are placed", Toast.LENGTH_SHORT).show();
            }
        });
        binding.rotationButton2.setOnClickListener(v -> myBoard.rotateSubmarine());
        binding.erasureButton2.setOnClickListener(v -> myBoard.resetBoard());
    }
}