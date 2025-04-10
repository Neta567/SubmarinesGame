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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.BreakIterator;
import java.util.Map;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private final FireBaseStore fireBaseStore = new FireBaseStore();
    private boolean isMusicPlaying;
    private ImageButton startGameButton,rotationButton,erasureButton, setupButton,startStopMusicButton;
    private MediaPlayer mediaPlayer;
    private Dialog winLooseDialog; // דיאלוג עבור המנצח


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game); // מפעיל את האקסמל על המסך

        BoardGameView boardGameView = new BoardGameView(this); // מייצר רכיב חדש המכיל את שתי הלוחות
        LinearLayout ll = findViewById(R.id.ll);
        ll.addView(boardGameView); // מוסיפים את הרכיב boargameview על המסך ומראים את 2 הלוחות

        //startMusicService(); // מפעילים את המוזיקה

        startGameButton = findViewById(R.id.startGameButton);
        rotationButton = findViewById(R.id.rotationButton);
        erasureButton = findViewById(R.id.erasureButton);
        setupButton = findViewById(R.id.setupButton);
        startStopMusicButton = findViewById(R.id.startStopMusicButton);

        startGameButton.setOnClickListener(v -> { // במידה ולוחצים על הכפתור של התחילת משחק (וי) -
            if (boardGameView.validateCanStartTheGame()) { // בודק אם ברכיב הזה הלוח של הצוללות מסודר תקין וכל הצוללות על המסך
                Toast.makeText(boardGameView.getContext(), "Game Started", Toast.LENGTH_SHORT).show(); // טוסט של תחילת המסך

                boardGameView.isGameStarted = true;
                boardGameView.gameId = new Random().nextInt(100);
                boardGameView.invalidate();

                rotationButton.setEnabled(false); // אי אפשר כבר ללחוץ על זה
                rotationButton.setVisibility(View.INVISIBLE); // לא רואים את הכפתור יותר
                erasureButton.setEnabled(false); // כנל על המחיקה
                erasureButton.setVisibility(View.INVISIBLE);

                fireBaseStore.saveGame(boardGameView.gameId, boardGameView.gameScore); // שומרים את הנתונים החדשים בפייק סטור

            } else {
                Toast.makeText(boardGameView.getContext(), "Not all submarines are placed", Toast.LENGTH_SHORT).show(); // אם מצב הצוללות לא תקין אז תופעל הודעה
            }
        });
        rotationButton.setOnClickListener(v -> boardGameView.rotateSubmarine()); // אם לחצו על סיבוב צוללת אז היא תסתובב
        erasureButton.setOnClickListener(v -> boardGameView.resetSubmarineBoard()); // כנל על מחיקה
        setupButton.setOnClickListener(v -> { // אם לוחצים על סידור קבוע אז זה יסדר
                    boardGameView.setupBoard();
                    boardGameView.invalidate();
                }
        );
        startStopMusicButton.setOnClickListener(v -> { // אם לוחצים על כפתורים המוזיקה
                    if(isMusicPlaying) { // אם מופעל
                        stopMusicService(); // יכבה
                    } else { // ולהפך
                        startMusicService();
                    }
                }
        );
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
    public Dialog getWinLooseDialog(Context context) {
        if(winLooseDialog == null) {
            winLooseDialog = new Dialog(context);
            winLooseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            winLooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            winLooseDialog.setContentView(R.layout.game_over);
        }
        return winLooseDialog;
    }

}