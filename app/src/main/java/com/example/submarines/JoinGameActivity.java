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
import com.example.submarines.helpers.FireBaseStore;
import com.example.submarines.model.GameModel;
import java.util.Map;
import java.util.Random;

public class JoinGameActivity extends AppCompatActivity {

    private ActivityJoinGameBinding binding;
    private Context context;
    private final static FireBaseStore fireBaseStore = FireBaseStore.getInstance();
    private GameActivity gameActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJoinGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.context = this;

        int random = new Random().nextInt(100);
        binding.editTextUsername.setText("Piccachu" + random);

        binding.btnJoin.setOnClickListener(v -> {
            binding.btnJoin.setEnabled(false);
            binding.textGameStarting.setText("Joining...\n");

            Animation anim = AnimationUtils.loadAnimation(context, R.anim.blink);
            anim.setDuration(2000);
            binding.textGameStarting.startAnimation(anim);

            String player = binding.editTextUsername.getText().toString();
            fireBaseStore.getOpenGameId(player, new FireBaseStore.Callback<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {

                    String gameId = String.valueOf(new Random().nextInt(10000));
                    boolean isExistingGame = (result != null && !result.isEmpty());
                    if (isExistingGame) {
                        // Open game found, use existing game ID
                        gameId = result.get("gameId").toString();
                    }
                    // No open game found, create a new game ID
                    GameModel.getInstance().setGameId(gameId);
                    GameModel.getInstance().setCurrentPlayer(player);

                    StringBuilder gameStatusBuilder = new StringBuilder();
                    gameStatusBuilder.append("Game ID: :").append(gameId).append("\n");
                    gameStatusBuilder.append("Player: ").append(player).append(" Joined.\n");

                    if(isExistingGame) {
                        String otherPlayer = result.get("otherPlayer").toString();

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

                        fireBaseStore.subscribeForGameStateChange(
                                GameModel.getInstance().getGameId(), new FireBaseStore.Callback<Map<String, Object>>() {
                                    @Override
                                    public void onSuccess(Map<String, Object> result) {
                                        if (result != null && !result.isEmpty()) {
                                            if(result.containsKey(GameModel.GameModelFields.game_state.toString())) {
                                                GameModel.getInstance().setGameState(
                                                        GameModel.GameState.valueOf((String) result.get(GameModel.GameModelFields.game_state.toString())));

                                                if(GameModel.getInstance().getGameState() == GameModel.GameState.TWO_PLAYERS_JOINED) {
                                                    fireBaseStore.getOtherPlayerName(GameModel.getInstance().getGameId(),
                                                            GameModel.getInstance().getCurrentPlayerName(), new FireBaseStore.Callback<Map<String, Object>>() {
                                                                @Override
                                                                public void onSuccess(Map<String, Object> result) {
                                                                    GameModel.getInstance().setOtherPlayer(result.get("otherPlayer").toString());
                                                                    startGame();
                                                                }

                                                                @Override
                                                                public void onFailure(Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            });
                                                }
                                            };

                                        }
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.d("Join Game", "Failed to handle update in game state: ", e);
                                    }
                                });
                    }

                    binding.textGameStatus.setText(gameStatusBuilder.toString());
                    fireBaseStore.saveGame(GameModel.getInstance());

                    startGame();
                }
                @Override
                public void onFailure(Exception e) {
                    Log.d("Join Game", "Error getting game ids: ", e);
                }
            });
        });
    }

    private void startGame() {
        if(GameModel.getInstance().getGameState() == GameModel.GameState.TWO_PLAYERS_JOINED) {
            Handler handler = new Handler(Looper.getMainLooper());
            // Create a Runnable to start the new Activity
            Runnable startNewActivityRunnable = () -> {
                try {
                    Intent intent = new Intent(context, GameActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            // Delay the start of the new Activity by 2 seconds
            handler.postDelayed(startNewActivityRunnable, 2000);
        }
    }
}