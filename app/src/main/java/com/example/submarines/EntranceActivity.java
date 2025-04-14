package com.example.submarines;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EntranceActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnDone;
    private EditText editTextName;
    public String name;

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
            setResult(Activity.RESULT_OK, intent);
            finish();

        }
    }
}