package com.example.submarines;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.submarines.databinding.ActivityJoinGameBinding;
import com.example.submarines.model.GameModel;

import java.util.Map;
import java.util.Objects;
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
            binding.textGameStarting.setText("Joining...\n");
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.blink);
            anim.setDuration(2000);
            binding.textGameStarting.startAnimation(anim);
            String player = binding.editTextUsername.getText().toString();

            FireBaseStore.INSTANCE.getOpenGameId(player, new FireBaseStore.Callback<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {

                    String gameId = String.valueOf(new Random().nextInt(10000));
                    boolean isExistingGame = (result != null && !result.isEmpty());
                    if (isExistingGame) {
                        // Open game found, use existing game ID
                        gameId = Objects.requireNonNull(result.get("gameId")).toString();
                    }
                    // No open game found, create a new game ID
                    GameModel.getInstance().setGameId(gameId);
                    GameModel.getInstance().setCurrentPlayer(player);

                    StringBuilder gameStatusBuilder = new StringBuilder();
                    gameStatusBuilder.append("Game ID: :").append(gameId).append("\n");
                    gameStatusBuilder.append("Player: ").append(player).append(" Joined.\n");
                    if(isExistingGame) {
                        String otherPlayer = Objects.requireNonNull(result.get("otherPlayer")).toString();

                        GameModel.getInstance().setOtherPlayer(otherPlayer);

                        gameStatusBuilder.append("Player: ").append(otherPlayer).append(" also joined\n");
                        binding.textGameStarting.setText("Starting...");
                        binding.textGameStarting.startAnimation(anim);
                        GameModel.getInstance().setGameState(GameModel.GameState.TWO_PLAYERS_JOINED);

                    } else {
                        gameStatusBuilder.append("No other player joined.");
                        binding.textGameStarting.setText("Waiting...");
                        binding.textGameStarting.startAnimation(anim);
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