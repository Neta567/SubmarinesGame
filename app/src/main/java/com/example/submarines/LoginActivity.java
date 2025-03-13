package com.example.submarines;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.submarines.databinding.ActivityLoginBinding;
import com.example.submarines.model.GameModel;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnJoin.setOnClickListener(v -> {
            binding.btnJoin.setEnabled(false);
            binding.textGameStatus.setText("Joining...");;
            FireBaseStore.INSTANCE.getOpenGameId(new FireBaseStore.Callback<String>() {
                @Override
                public void onSuccess(String result) {
                    String user = binding.editTextUsername.getText().toString();
                    String gameId = String.valueOf(new Random().nextInt(10000));
                    boolean isExistingGame = result != null;
                    if (result != null) {
                        // Open game found, use existing game ID
                        gameId = result;
                    }
                    // No open game found, create a new game ID
                    GameModel.getInstance().setGameId(gameId);
                    GameModel.getInstance().setCurrentPlayer(user);

                    StringBuilder gameStatusBuilder = new StringBuilder();
                    gameStatusBuilder.append("User: " + user + " Joined. Game ID: " + gameId + "\n");
                    if(isExistingGame) {
                        gameStatusBuilder.append("Other player also joined. User: " + "Player2");
                    } else {
                        gameStatusBuilder.append("No other player joined. Waiting...");
                    }

                    binding.textGameStatus.setText(gameStatusBuilder.toString());;
                    // Set the game as open
                    //GameModel.getInstance().setOpen(true);
                }
                @Override
                public void onFailure(Exception e) {
                    Log.d("Join Game", "Error getting game ids: ", e);
                }
            });
        });
    }
}