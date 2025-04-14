package com.example.submarines;

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
import android.widget.Toast;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private final FireBaseStore fireBaseStore = new FireBaseStore();
    private boolean isMusicPlaying, isFirstTime = true;
    private ImageButton startGameButton, setupButton, startStopMusicButton;
    private MediaPlayer mediaPlayer;
    private Dialog winLooseDialog; // דיאלוג עבור המנצח
    private int opption;
    private BoardGameView boardGameView;
    private Button endGameButton;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game); // מפעיל את האקסמל על המסך

        boardGameView = new BoardGameView(this); // מייצר רכיב חדש המכיל את שתי הלוחות
        LinearLayout boardPlace = findViewById(R.id.boardsPlace);
        boardPlace.addView(boardGameView); // מוסיפים את הרכיב boargameview על המסך ומראים את 2 הלוחות

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        boardGameView.name = name;

        //startMusicService(); // מפעילים את המוזיקה

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
                Toast.makeText(boardGameView.getContext(), "Game Started", Toast.LENGTH_SHORT).show(); // טוסט של תחילת המסך
                fireBaseStore.returnBestScore(new FireBaseStore.Callback<Float>() {
                    @Override
                    public void onSuccess(Float result) {
                        boardGameView.bestScore = result;
                        //Toast.makeText(GameActivity.this, result.toString(), Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });

                boardGameView.isGameStarted = true;
                boardGameView.gameId = new Random().nextInt(100);
                boardGameView.invalidate();

                fireBaseStore.saveGame(boardGameView.gameId, boardGameView.gameScore,boardGameView.name); // שומרים את הנתונים החדשים בפייק סטור

                isFirstTime = false;
            }
            else
            {
                if (boardGameView.isGameOver() == true)
                {
                    boardGameView.makeAGameScore();
                    fireBaseStore.saveGame(boardGameView.gameId, boardGameView.gameScore,boardGameView.name);
                    boardGameView.gameScore=0;
                    Dialog dialog = getWinLooseDialog(boardGameView.getContext());
                    dialog.show();
                }
                else
                {
                    Toast.makeText(boardGameView.getContext(), "try more", Toast.LENGTH_SHORT).show(); // אם מצב הצוללות לא תקין אז תופעל הודעה
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
    { // מפעילים את המוזיקה כשאר עוברים לאקטיביטי הזה
        isMusicPlaying = true; // הופכים את המצב של המוזיקה לאמת
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