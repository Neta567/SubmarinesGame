package com.example.submarines.helpers;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import com.example.submarines.R;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case "PLAY":
                    startMusic();
                    break;
                case "STOP":
                    stopMusic();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    private void startMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.submarine);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }

    private void stopMusic() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
