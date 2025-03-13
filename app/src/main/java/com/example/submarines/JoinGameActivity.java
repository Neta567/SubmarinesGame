package com.example.submarines;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.submarines.databinding.ActivityJoinGameBinding;
import com.example.submarines.model.GameModel;

import java.util.Random;

public class JoinGameActivity extends AppCompatActivity {

    private ActivityJoinGameBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJoinGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.context = this;

        binding.btnJoin.setOnClickListener(v -> {
            binding.btnJoin.setEnabled(false);
            binding.textGameStatus.setText("Joining...");
            FireBaseStore.INSTANCE.getOpenGameId(new FireBaseStore.Callback<String>() {
                @Override
                public void onSuccess(String result) {
                    String user = binding.editTextUsername.getText().toString();
                    String gameId = String.valueOf(new Random().nextInt(10000));
                    boolean isExistingGame = (result != null && !result.isEmpty());
                    if (isExistingGame) {
                        // Open game found, use existing game ID
                        gameId = result;
                    }
                    // No open game found, create a new game ID
                    GameModel.getInstance().setGameId(gameId);
                    GameModel.getInstance().setCurrentPlayer(user);

                    StringBuilder gameStatusBuilder = new StringBuilder();
                    gameStatusBuilder.append("User: ").append(user)
                            .append(" Joined. Game ID: ").append(gameId).append("\n");
                    if(isExistingGame) {
                        gameStatusBuilder.append("User: " + "Player2" + " also joined\n" + "Starting Game...");
                        GameModel.getInstance().setGameState(GameModel.GameState.TWO_PLAYERS_JOINED);

                    } else {
                        gameStatusBuilder.append("No other player joined. Waiting...");
                        GameModel.getInstance().setGameState(GameModel.GameState.ONE_PLAYER_JOINED);
                    }

                    binding.textGameStatus.setText(gameStatusBuilder.toString());
                    FireBaseStore.INSTANCE.saveGame(GameModel.getInstance());

                    if(GameModel.getInstance().getGameState() == GameModel.GameState.TWO_PLAYERS_JOINED) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        // Create a Runnable to start the new Activity
                        Runnable startNewActivityRunnable = () -> {
                            Intent intent = new Intent(context, GameActivity.class);
                            startActivity(intent);
                        };

                        // Delay the start of the new Activity by 2 seconds
                        handler.postDelayed(startNewActivityRunnable, 2000);
                    }
                }
                @Override
                public void onFailure(Exception e) {
                    Log.d("Join Game", "Error getting game ids: ", e);
                }
            });
        });
    }
}