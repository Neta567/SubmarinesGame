package com.example.submarines;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.submarines.databinding.ActivityGameBinding;

public class GameActivity extends AppCompatActivity {
    private ActivityGameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}