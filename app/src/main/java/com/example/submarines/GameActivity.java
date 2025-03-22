package com.example.submarines;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.submarines.boards.BoardGame;
import com.example.submarines.databinding.ActivityGameBinding;
import com.example.submarines.databinding.GameOverBinding;
import com.example.submarines.helpers.FireBaseStore;
import com.example.submarines.helpers.MusicService;
import com.example.submarines.model.GameModel;
import com.example.submarines.model.Player;
import com.google.gson.Gson;

import java.util.Map;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {

    private ActivityGameBinding binding;
    private GameOverBinding gameOverBinding;
    private Dialog turnDialog;
    private Dialog winLooseDialog;
    private boolean isMusicPlaying;
    private MusicService musicService;
    private final static FireBaseStore fireBaseStore = FireBaseStore.getInstance();

    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.rootLayout);

        turnDialog = new Dialog(GameActivity.this);
        turnDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        turnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        turnDialog.setContentView(R.layout.game_turn);

        gameOverBinding = GameOverBinding.inflate(getLayoutInflater());
        winLooseDialog = new Dialog(GameActivity.this);
        winLooseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        winLooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        winLooseDialog.setContentView(gameOverBinding.getRoot());

        binding.setViewModel(GameModel.getInstance());

        BoardGame myBoard = new BoardGame(this);
        myBoard.subscribeOnFireEvent(() -> {
            turnDialog.show();
            return null;
        });
        binding.ll.addView(myBoard);

        startMusicService();

        binding.startGameButton.setOnClickListener(v -> {
            if (myBoard.validateCanStartTheGame()) {
                Toast.makeText(myBoard.getContext(), "Game Started", Toast.LENGTH_SHORT).show();
                GameModel.getInstance().setCurrentPlayerGameStatus(Player.PlayerGameStatus.STARTED);

                binding.rotationButton.setEnabled(false);
                binding.rotationButton.setVisibility(View.INVISIBLE);
                binding.erasureButton.setEnabled(false);
                binding.erasureButton.setVisibility(View.INVISIBLE);

                if (GameModel.getInstance().getOtherPlayerGameStatus() == Player.PlayerGameStatus.STARTED) {
                    GameModel.getInstance().setGameState(GameModel.GameState.STARTED);
                }

                FireBaseStore.getInstance().saveGame(GameModel.getInstance());
                //myBoard.invalidate();
                turnDialog.show();

            } else {
                Toast.makeText(myBoard.getContext(), "Not all submarines are placed", Toast.LENGTH_SHORT).show();
            }
        });
        binding.rotationButton.setOnClickListener(v -> myBoard.rotateSubmarine());
        binding.erasureButton.setOnClickListener(v -> myBoard.resetSubmarineBoard());
        binding.setup.setOnClickListener(v -> {
                    myBoard.setupBoard();
                    myBoard.invalidate();
                }
        );
        binding.startStopMusicButton.setOnClickListener(v -> {
                    if(isMusicPlaying) {
                        stopMusicService();
                    } else {
                        startMusicService();
                    }
                }
        );


        fireBaseStore.subscribeForGameStateChange(
                GameModel.getInstance().getGameId(), new FireBaseStore.Callback<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        if (result != null && !result.isEmpty()) {
                            if (result.containsKey(GameModel.GameModelFields.game_state.toString())) {
                                GameModel.getInstance().setGameState(
                                        GameModel.GameState.valueOf((String) result.get(GameModel.GameModelFields.game_state.toString())));

                                if (GameModel.getInstance().getGameState() == GameModel.GameState.STARTED) {
                                    myBoard.invalidate();
                                } else if (GameModel.getInstance().getGameState() == GameModel.GameState.GAME_OVER) {
                                    String msg = Objects.requireNonNull(result.get(GameModel.GameModelFields.game_result.toString())).toString();
                                    endGame(msg);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("Join Game", "Failed to handle update in game state: ", e);
                    }
                });

        fireBaseStore.subscribeForPlayerStateChange(GameModel.getInstance().getGameId(),
                GameModel.getInstance().getOtherPlayer(), new FireBaseStore.Callback<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        //TODO: Fix board first update of the player that started the game last (other player board is not updated)
                        if (result != null && !result.isEmpty()) {
                            if (result.containsKey(Player.PlayerFields.game_status.toString())) {
                                Player.PlayerGameStatus gameStatus =
                                        Player.PlayerGameStatus.valueOf((String) result.get(Player.PlayerFields.game_status.toString()));

                                GameModel.getInstance().setOtherPlayerGameStatus(
                                        Player.PlayerGameStatus.valueOf((String) result.get(Player.PlayerFields.game_status.toString())));

                                if (gameStatus == Player.PlayerGameStatus.STARTED &&
                                        GameModel.getInstance().getCurrentPlayerGameStatus() == Player.PlayerGameStatus.STARTED) {
                                    turnDialog.dismiss();

                                    Gson Gs = new Gson();
                                    if (result.containsKey(Player.PlayerFields.submarines_board.toString())) {
                                        Object subBoardJson = result.get(Player.PlayerFields.submarines_board.toString());
                                        int[][] subBoard = Gs.fromJson(Objects.requireNonNull(subBoardJson).toString(), int[][].class);
                                        GameModel.getInstance().updatePlayer2SubmarinesBoard(subBoard);

                                        Object fireBoardJson = result.get(Player.PlayerFields.fire_board.toString());
                                        int[][] fireBoard = Gs.fromJson(Objects.requireNonNull(fireBoardJson).toString(), int[][].class);
                                        GameModel.getInstance().updatePlayer1SubmarinesBoardAfterFire(fireBoard);
                                        myBoard.invalidate();

                                        if (GameModel.getInstance().isGameOver()) {
                                            GameModel.getInstance().setGameState(GameModel.GameState.GAME_OVER);
                                            GameModel.getInstance().setCurrentPlayerGameStatus(Player.PlayerGameStatus.LOOSE);
                                            fireBaseStore.saveGame(GameModel.getInstance());

                                            endGame(GameModel.getInstance().getOtherPlayer() + " WON");
                                        }
                                    }
                                } else if (gameStatus == Player.PlayerGameStatus.LOOSE) {
                                    GameModel.getInstance().setGameState(GameModel.GameState.GAME_OVER);
                                    GameModel.getInstance().setCurrentPlayerGameStatus(Player.PlayerGameStatus.WON);
                                    fireBaseStore.saveGame(GameModel.getInstance());
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void endGame(String gameResult) {
        gameOverBinding.gameOverTxt.setText(gameResult);
        winLooseDialog.show();

        Handler handler = new Handler(Looper.getMainLooper());
        // Create a Runnable to start the new Activity
        Runnable startNewActivityRunnable = () -> {
            try {
                Intent intent = new Intent(binding.getRoot().getContext(), MainActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // Delay the start of the new Activity by 2 seconds
        handler.postDelayed(startNewActivityRunnable, 3000);
    }

    private void startMusicService() {
        isMusicPlaying = true;
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.setAction("PLAY");
        startService(serviceIntent);
    }

    private void stopMusicService() {
        isMusicPlaying = false;
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.setAction("STOP");
        startService(serviceIntent);
    }
}