package com.example.submarines;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EntranceActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnDone;
    private EditText editTextName;
    public String name;
    private TextView textView;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_entrance);

        btnDone = findViewById(R.id.btn_done);
        editTextName = findViewById(R.id.ed_name);
        btnDone.setOnClickListener(this);

    }

    @Override
    public void onClick(View v)
    {
        if (v == btnDone)
        {
            name = editTextName.getText().toString();
            Intent intent = new Intent();
            intent.putExtra("name",name);

            textView = findViewById(R.id.anim_starting);
            textView.setText("Starting...");
            animation = AnimationUtils.loadAnimation(this, R.anim.blink);
            textView.startAnimation(animation);

            Runnable stopAnimationRunnable = new Runnable() {
                @Override
                public void run() {
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            };
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(stopAnimationRunnable, 2000);

        }
    }
}