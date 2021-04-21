package com.example.match4game;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(v -> goPlay());

        Button btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> goSettings());

        Button btnExit = findViewById(R.id.btnExit);
        btnExit.setOnClickListener(v -> closeApp());
    }

    private void goPlay() {
        Intent intent = new Intent(MainActivity.this, PlayGame.class);
        this.startActivity(intent);
    }

    private void goSettings() {
        Intent intent = new Intent(MainActivity.this, Settings.class);
        this.startActivity(intent);
    }

    private void closeApp() {
        finish();
        moveTaskToBack(true);
    }
}