package com.example.submarines;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.submarines.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding; //מצהיר על ביינדינג
    private JoinGameActivity joinGameActivity;
    private InstructionsActivity instructionsActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater()); //מעביר אלמנטים מאקסמל לביינדינג
        setContentView(binding.getRoot());

        binding.btnStart.setOnClickListener(this);
        binding.btnInstructions.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == binding.btnStart)
        {
            Intent i = new Intent(this, JoinGameActivity.class);
            startActivity(i);
        }
        if(v == binding.btnInstructions)
        {

            Intent i = new Intent(this, InstructionsActivity.class);
            startActivity(i);
        }
    }
}