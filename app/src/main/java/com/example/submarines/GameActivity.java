package com.example.submarines;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.example.submarines.boards.MyNewBoard;
import com.example.submarines.databinding.ActivityGameBinding;
import com.example.submarines.model.GameModel;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private ActivityGameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GameModel.getInstance().setGameId(((Integer)new Random().nextInt(10000)).toString());
        binding.setViewModel(GameModel.getInstance());
        FireBaseStore.INSTANCE.saveGame(GameModel.getInstance());

        MyNewBoard myBoard = new MyNewBoard(this);
        myBoard.addPlayerShipsBoard(binding.playerShipsBoardLayout);
        myBoard.addPlayerFireBoard(binding.playerFireBoardLayout);
        binding.playerSubmarinesLayout.addView(myBoard);

        binding.startGameButton1.setOnClickListener(v -> {
            GameModel.getInstance().setGameState(GameModel.GameState.STARTED);
            binding.rotationButton1.setEnabled(false);
            binding.rotationButton1.setVisibility(View.INVISIBLE);
            binding.erasureButton1.setEnabled(false);
            binding.erasureButton1.setVisibility(View.INVISIBLE);
            FireBaseStore.INSTANCE.saveGame(GameModel.getInstance());
        });
        binding.rotationButton1.setOnClickListener(v -> {

        });
        binding.erasureButton1.setOnClickListener(v -> {

        });
    }
}