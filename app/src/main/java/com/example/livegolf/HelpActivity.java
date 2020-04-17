package com.example.livegolf;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {

    private Button backButton;
    private Button nextButton;
    private TextView progressText;
    private int progress;

    private TextView helpText;

    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        progress = 0;


        backButton = findViewById(R.id.back_btn);
        nextButton = findViewById(R.id.next_btn);
        progressText = findViewById(R.id.progressText);

        helpText = findViewById(R.id.helpText);

        image = findViewById(R.id.imageView);

        switchProgress();

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                progress--;
                if(progress < 0){
                    finish();
                }else{
                    switchProgress();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress++;
                if(progress > 3){
                    finish();
                }else{
                    switchProgress();
                }

            }
        });
    }



    private void switchProgress(){
        switch (progress){
            case 0:
                image.setImageResource(R.drawable.clubselect);
                helpText.setText(R.string.helpClub);
                nextButton.setText("NEXT");
                progressText.setText("O  o  o  o");
                break;
            case 1:
                image.setImageResource(R.drawable.turn);
                helpText.setText(R.string.helpTurn);
                nextButton.setText("NEXT");
                progressText.setText("o  O  o  o");
                break;
            case 2:
                image.setImageResource(R.drawable.position);
                helpText.setText(R.string.helpPosition);
                nextButton.setText("NEXT");
                progressText.setText("o  o  O  o");
                break;
            case 3:
                image.setImageResource(R.drawable.readyswing);
                helpText.setText(R.string.helpReady);
                nextButton.setText("RETURN");
                progressText.setText("o  o  o  O");
                break;
        }
    }



}
