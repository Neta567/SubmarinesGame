package com.example.submarines;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.BreakIterator;
import java.util.Map;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    private final FireBaseStore fireBaseStore = new FireBaseStore();
    private boolean isMusicPlaying, isFirstTime = true;
    private ImageButton startGameButton, setupButton, startStopMusicButton;
    private MediaPlayer mediaPlayer;
    private Dialog winLooseDialog; // דיאלוג עבור המנצח
    private int opption;
    private BoardGameView boardGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game); // מפעיל את האקסמל על המסך

        boardGameView = new BoardGameView(this); // מייצר רכיב חדש המכיל את שתי הלוחות
        LinearLayout ll = findViewById(R.id.ll);
        ll.addView(boardGameView); // מוסיפים את הרכיב boargameview על המסך ומראים את 2 הלוחות

        //startMusicService(); // מפעילים את המוזיקה

        startGameButton = findViewById(R.id.startGameButton);
        setupButton = findViewById(R.id.setupButton);
        startStopMusicButton = findViewById(R.id.startStopMusicButton);

        startGameButton.setOnClickListener(this);
        setupButton.setOnClickListener(this);
        startStopMusicButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == startGameButton) {
            if (isFirstTime == true) {
                Toast.makeText(boardGameView.getContext(), "Game Started", Toast.LENGTH_SHORT).show(); // טוסט של תחילת המסך

                boardGameView.isGameStarted = true;
                boardGameView.gameId = new Random().nextInt(100);
                boardGameView.invalidate();

                fireBaseStore.saveGame(boardGameView.gameId, boardGameView.gameScore); // שומרים את הנתונים החדשים בפייק סטור

                isFirstTime = false;
            } else {
                if (boardGameView.isGameOver() == true) {
                    Dialog dialog = getWinLooseDialog(boardGameView.getContext());
                    dialog.show();
                } else {
                    Toast.makeText(boardGameView.getContext(), "try more", Toast.LENGTH_SHORT).show(); // אם מצב הצוללות לא תקין אז תופעל הודעה
                }
            }
        }

        if (v == setupButton) {
            opption = new Random().nextInt(4);
            boardGameView.setupBoard(opption);
            boardGameView.invalidate();
        }

        if (v == startStopMusicButton) {
            if (isMusicPlaying) { // אם מופעל
                stopMusicService(); // יכבה
            } else { // ולהפך
                startMusicService();
            }
        }

    }

    private void startMusicService() { // מפעילים את המוזיקה כשאר עוברים לאקטיביטי הזה
        isMusicPlaying = true; // הופכים את המצב של המוזיקה לאמת
        mediaPlayer = MediaPlayer.create(this, R.raw.submarine);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }

    private void stopMusicService() {
        isMusicPlaying = false;
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private Dialog getWinLooseDialog(Context context) {
        if (winLooseDialog == null) {
            winLooseDialog = new Dialog(context);
            winLooseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            winLooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            winLooseDialog.setContentView(R.layout.game_over);
            Button btn = winLooseDialog.findViewById(R.id.close_btn);
            btn.setOnClickListener(v -> {
                winLooseDialog.dismiss();
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            });

        }
        return winLooseDialog;
    }
}