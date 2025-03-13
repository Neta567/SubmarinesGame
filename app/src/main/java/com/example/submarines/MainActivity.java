package com.example.submarines;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.submarines.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnStart.setOnClickListener(this);
        binding.btnInstructions.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == binding.btnStart)
        {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        if(v == binding.btnInstructions)
        {
            Intent i = new Intent(this, InstructionsActivity.class);
            startActivity(i);
        }
    }
}