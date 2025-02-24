package com.example.submarines;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Instructions extends AppCompatActivity implements View.OnClickListener {
    Button btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        btnReturn = findViewById(R.id.btnReturn);
        btnReturn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==btnReturn)
        {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }
}