package com.example.submarines;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.submarines.databinding.ActivityTwoPlayersBinding;

public class TwoPlayersActivity extends AppCompatActivity {

    private ActivityTwoPlayersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTwoPlayersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}