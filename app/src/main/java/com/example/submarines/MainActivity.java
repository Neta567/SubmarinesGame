package com.example.submarines;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnStart,btnInstructions;
    private ActivityResultLauncher <Intent> activityOnResult;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btnStart);
        btnInstructions = findViewById(R.id.btnInstructions);
        btnStart.setOnClickListener(this);
        btnInstructions.setOnClickListener(this);

        activityOnResult =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult o) {
                                String name = o.getData().getExtras().get("name").toString();
                                Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                                intent.putExtra("name",name);
                                startActivity(intent);
                            }
                        }
                );
    }

    @Override
    public void onClick(View v)
    {
        if(v == btnStart)

        {
            Intent i = new Intent(this, EntranceActivity.class);
            activityOnResult.launch(i);
        }
        if(v == btnInstructions)
        {
            Intent i = new Intent(this, InstructionsActivity.class);
            startActivity(i);
        }
    }
}