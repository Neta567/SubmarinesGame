package com.example.submarines;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Toast;
import com.example.submarines.boards.MyBoard;
import com.example.submarines.databinding.ActivityGameBinding;
import com.example.submarines.model.GameModel;
import com.example.submarines.model.Player;
import com.google.gson.Gson;

import java.util.Map;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {

    private ActivityGameBinding binding;
    public LruCache<String, Bitmap> bitmapLruCache = new LruCache<>(100);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.rootLayout);

        binding.setViewModel(GameModel.getInstance());

        MyBoard myBoard = new MyBoard(this);
        binding.ll.addView(myBoard);

        binding.startGameButton.setOnClickListener(v -> {
            if(myBoard.validateCanStartTheGame()) {
                Toast.makeText(myBoard.getContext(), "Game Started", Toast.LENGTH_SHORT).show();
                if(GameModel.getInstance().getOtherPlayerGameStatus() == Player.PlayerGameStatus.STARTED) {
                    GameModel.getInstance().setGameState(GameModel.GameState.STARTED);
                }
                GameModel.getInstance().setCurrentPlayerGameStatus(Player.PlayerGameStatus.STARTED);

                binding.rotationButton.setEnabled(false);
                binding.rotationButton.setVisibility(View.INVISIBLE);
                binding.erasureButton.setEnabled(false);
                binding.erasureButton.setVisibility(View.INVISIBLE);
                FireBaseStore.INSTANCE.saveGame(GameModel.getInstance());
                myBoard.invalidate();
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

        FireBaseStore.INSTANCE.subscribeForGameStateChange(
                GameModel.getInstance().getGameId(), new FireBaseStore.Callback<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        if (result != null && !result.isEmpty()) {
                            if(result.containsKey(GameModel.GameModelFields.game_state.toString())) {
                                GameModel.getInstance().setGameState(
                                        GameModel.GameState.valueOf((String) result.get(GameModel.GameModelFields.game_state.toString())));

                                if(GameModel.getInstance().getGameState() == GameModel.GameState.STARTED) {
                                    myBoard.invalidate();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("Join Game", "Failed to handle update in game state: ", e);
                    }
                });

        FireBaseStore.INSTANCE.subscribeForPlayerStateChange(GameModel.getInstance().getGameId(),
                GameModel.getInstance().getOtherPlayer(), new FireBaseStore.Callback<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        if(result != null && !result.isEmpty()) {
                            if(result.containsKey(Player.PlayerFields.game_status.toString())) {
                                Player.PlayerGameStatus gameStatus =
                                        Player.PlayerGameStatus.valueOf((String) result.get(Player.PlayerFields.game_status.toString()));

                                GameModel.getInstance().setOtherPlayerGameStatus(
                                        Player.PlayerGameStatus.valueOf((String) result.get(Player.PlayerFields.game_status.toString())));

                                if(gameStatus == Player.PlayerGameStatus.STARTED &&
                                        GameModel.getInstance().getCurrentPlayerGameStatus() == Player.PlayerGameStatus.STARTED) {
                                    Gson Gs = new Gson();
                                    if (result.containsKey(Player.PlayerFields.submarines_board.toString())) {
                                        Object subBoardJson = result.get(Player.PlayerFields.submarines_board.toString());
                                        int[][] subBoard = Gs.fromJson(Objects.requireNonNull(subBoardJson).toString(), int[][].class);
                                        GameModel.getInstance().updatePlayer2SubmarinesBoard(subBoard);

                                        Object fireBoardJson = result.get(Player.PlayerFields.fire_board.toString());
                                        int[][] fireBoard = Gs.fromJson(Objects.requireNonNull(fireBoardJson).toString(), int[][].class);
                                        GameModel.getInstance().updatePlayer1SubmarinesBoardAfterFire(fireBoard);
                                        myBoard.invalidate();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            bitmapLruCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return bitmapLruCache.get(key);
    }
}