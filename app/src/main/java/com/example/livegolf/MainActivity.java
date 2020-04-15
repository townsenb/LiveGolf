package com.example.livegolf;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private ImageButton startButton;
    private ImageButton tutorialButton;

    private MediaPlayer hole_sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.start_btn);
        tutorialButton = findViewById(R.id.tutorial_btn);

        hole_sound = MediaPlayer.create(this, R.raw.hole);

        startButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                hole_sound.start();
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        tutorialButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                hole_sound.start();
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });
    }
}
