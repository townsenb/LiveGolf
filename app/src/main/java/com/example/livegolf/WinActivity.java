package com.example.livegolf;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static java.lang.Integer.parseInt;

public class WinActivity extends AppCompatActivity {

    private TextView scoreTextView;
    private TextView descriptionTextView;

    private int score;

    private Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);
        score = getIntent().getIntExtra("SCORE",0);
        scoreTextView = findViewById(R.id.scoreTextView);
        descriptionTextView = findViewById(R.id.scoreDescription);

        returnButton = findViewById(R.id.return_btn);

        if(score >= 1) {
            scoreTextView.setText(String.valueOf(score));
        }else{
            scoreTextView.setText("☹️");
        }
        descriptionTextView.setText(scoreToDescription(score));


        returnButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String scoreToDescription(int score){
        String result = "error";

        switch (score){
            case -2:
                result = "Too Many Swings";
                break;
            case -1:
                result = "Out of Bounds";
                break;
            case 1:
                result = "Hole-In-One!";
                break;
            case 2:
                result = "Eagle!";
                break;
            case 3:
                result = "Birdie!";
                break;
            case 4:
                result = "Par";
                break;
            case 5:
                result = "Bogey";
                break;
            case 6:
                result = "Double Bogey";
                break;
            case 7:
                result = "Triple Bogey";
                break;
        }

        return result;
    }
}
