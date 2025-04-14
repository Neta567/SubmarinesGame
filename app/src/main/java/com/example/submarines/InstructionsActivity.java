package com.example.submarines;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Locale;


public class InstructionsActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    private Button btnReturn;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(this);

        textToSpeech = new TextToSpeech(this,this);

    }

    @Override
    public void onClick(View v)
    {
        if(v == btnReturn)
        {
            speakText("Returning to the main menu");
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onInit(int status)
    {
        if(status == TextToSpeech.SUCCESS)
        {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Log.e("TTS", "Language not supported");
            }
            else
            {
                speakText("You need to destroy all the hiden submarines to win the game");
            }
        } else
        {
            Log.e("TTS", "Initialization failed");
        }
    }

    private void speakText(String s)
    {
        textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null);

    }
}