package com.example.submarines;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.LruCache;
import android.view.View;
import android.widget.Toast;
import com.example.submarines.boards.MyBoard;
import com.example.submarines.databinding.ActivityGameBinding;
import com.example.submarines.model.GameModel;

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
                GameModel.getInstance().setGameState(GameModel.GameState.STARTED);
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