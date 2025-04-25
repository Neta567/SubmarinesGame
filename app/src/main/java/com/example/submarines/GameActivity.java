package com.example.submarines;

import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private final FireBaseStore fireBaseStore = new FireBaseStore();
    private boolean isMusicPlaying, isFirstTime = true;
    private ImageButton startGameButton, setupButton, startStopMusicButton;
    private MediaPlayer mediaPlayer;
    private Dialog winLooseDialog;
    private int opption;
    private BoardGameView boardGameView;
    private Button endGameButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        boardGameView = new BoardGameView(this);
        LinearLayout boardPlace = findViewById(R.id.boardsPlace);
        boardPlace.addView(boardGameView);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        boardGameView.name = name;


        startGameButton = findViewById(R.id.startGameButton);
        setupButton = findViewById(R.id.setupButton);
        startStopMusicButton = findViewById(R.id.startStopMusicButton);

        startGameButton.setOnClickListener(this);
        setupButton.setOnClickListener(this);
        startStopMusicButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v == startGameButton)
        {
            if (isFirstTime == true)
            {
                Toast.makeText(boardGameView.getContext(), "Game Started", Toast.LENGTH_SHORT).show();

                boardGameView.isGameStarted = true;
                boardGameView.gameId = UUID.randomUUID().toString();
                boardGameView.invalidate();

                fireBaseStore.saveGame(boardGameView.gameId, boardGameView.gameScore,boardGameView.name);

                isFirstTime = false;
            }
            else
            {
                if (boardGameView.isGameOver() == true)
                {
                    boardGameView.makeAGameScore();

                    Dialog dialog = getWinLooseDialog(boardGameView.getContext());
                    fireBaseStore.updateScoresInDailog(dialog.findViewById(R.id.best_score));

                    TextView textView = dialog.findViewById(R.id.your_score);
                    textView.setText(String.valueOf(boardGameView.gameScore));
                    fireBaseStore.saveGame(boardGameView.gameId, boardGameView.gameScore,boardGameView.name);

                    boardGameView.gameScore=0;

                    dialog.show();
                }
                else
                {
                    Toast.makeText(boardGameView.getContext(), "try more", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (v == setupButton)
        {
            opption = new Random().nextInt(4);
            boardGameView.setupBoard(opption);
            boardGameView.invalidate();
        }

        if (v == startStopMusicButton)
        {
            if (isMusicPlaying)
            { // אם מופעל
                stopMusic(); // יכבה
            }
            else
            { // ולהפך
                startMusic();
            }
        }
        if (v == endGameButton)
        {
            winLooseDialog.dismiss();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }


    }

    private void startMusic()
    {
        isMusicPlaying = true;
        mediaPlayer = MediaPlayer.create(this, R.raw.submarine);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }

    private void stopMusic()
    {
        isMusicPlaying = false;
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private Dialog getWinLooseDialog(Context context)
    {
        if (winLooseDialog == null)
        {
            winLooseDialog = new Dialog(context);
            winLooseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            winLooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            winLooseDialog.setContentView(R.layout.game_over);

            endGameButton = winLooseDialog.findViewById(R.id.close_btn);
            endGameButton.setOnClickListener(this);

        }
        return winLooseDialog;
    }
}