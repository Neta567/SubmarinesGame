package com.example.submarines;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.submarines.databinding.ActivityInstructionsBinding;

public class InstructionsActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityInstructionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInstructionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnReturn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == binding.btnReturn)
        {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }
}