package com.example.submarines;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnStart, btnInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);

        btnInstructions= findViewById(R.id.btnInstructions);
        btnInstructions.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==btnStart)
        {
            Intent i = new Intent(this, MyBoardGameActivity.class);
            startActivity(i);
        }
        if(v==btnInstructions)
        {
            Intent i = new Intent(this, InstructionsActivity.class);
            startActivity(i);
        }
    }
}