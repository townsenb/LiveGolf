package com.example.livegolf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

import static android.hardware.Sensor.TYPE_GYROSCOPE;

public class GameActivity extends AppCompatActivity {

    private final int X = 0;
    private final int Y = 1;
    private final int Z = 2;

    private int teeX = 383;
    private int teeY = 1045;
    private int holeX = 423;
    private int holeY = 135;
    private int x = teeX;
    private int y = teeY;

    private int swingCount = 0;

    private final int pixelsToYards = 2;

    private SensorManager sensorManager;
    private Sensor accel;
    private Sensor gyro;

    private double accelVals[];
    private double accelChange[];
    private double gyroVals[];
    private double accelMax = 0;

    private Button swing_btn;

    private Button drive_btn;
    private Button iron_btn;
    private Button putt_btn;

    private Button left_btn;
    private Button right_btn;

    private Button reset_btn;

    private Vibrator vibrator;

    private MediaPlayer drive_sound, iron_sound, putt_sound, hole_sound, ding_sound, fail_sound;

    private boolean windup_lock = true;
    private boolean drive, iron, putt = false;
    private boolean driverSelected, ironSelected, putterSelected = false;

    private DrawView map;

    private int angleStep = 30;
    private int angleOffset = 0;
    public TextView swingTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        map = findViewById(R.id.mapView);
        map.setBackgroundColor(Color.parseColor("#94E482"));
        map.resetHole();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorManager.registerListener(accelerometerListener, accel , SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(gyroscopeListener, gyro , SensorManager.SENSOR_DELAY_GAME);

        swingTextView = findViewById(R.id.swingCountTextView);

        accelVals = new double[3];
        accelChange = new double[3];
        gyroVals = new double[3];

        drive_sound = MediaPlayer.create(this, R.raw.drive);
        iron_sound = MediaPlayer.create(this, R.raw.iron);
        putt_sound = MediaPlayer.create(this, R.raw.putt);
        hole_sound = MediaPlayer.create(this, R.raw.hole);
        ding_sound = MediaPlayer.create(this, R.raw.ding);
        fail_sound = MediaPlayer.create(this,R.raw.fail);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        swing_btn = findViewById(R.id.Swing_btn);

        driverSelected = true;

        drive_btn = findViewById(R.id.Drive_btn);
        drive_btn.setBackgroundColor(Color.GREEN);
        iron_btn = findViewById(R.id.Iron_btn);
        iron_btn.setBackgroundColor(Color.LTGRAY);
        putt_btn = findViewById(R.id.Putt_btn);
        putt_btn.setBackgroundColor(Color.LTGRAY);

        left_btn = findViewById(R.id.left_btn);
        right_btn = findViewById(R.id.right_btn);

        reset_btn = findViewById(R.id.reset_btn);

        //Button listeners
        swing_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                swing_btn.setBackgroundColor(Color.GRAY);
                swingReset();
            }
        });

        drive_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                vibrate(30,100);
                driverSelected = true;
                ironSelected = putterSelected = false;
                swingReset();
                drive_btn.setBackgroundColor(Color.GREEN); //green - selected
                iron_btn.setBackgroundColor(Color.LTGRAY); //grey
                putt_btn.setBackgroundColor(Color.LTGRAY); //grey);
            }
        });

        iron_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                vibrate(30,100);
                ironSelected = true;
                driverSelected = putterSelected = false;
                swingReset();
                iron_btn.setBackgroundColor(Color.GREEN); //green - selected
                drive_btn.setBackgroundColor(Color.LTGRAY); //grey
                putt_btn.setBackgroundColor(Color.LTGRAY); //grey);
            }
        });

        putt_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                vibrate(30,100);
                putterSelected = true;
                driverSelected = ironSelected = false;
                swingReset();
                putt_btn.setBackgroundColor(Color.GREEN); //green - selected
                drive_btn.setBackgroundColor(Color.LTGRAY); //grey
                iron_btn.setBackgroundColor(Color.LTGRAY); //grey);
            }
        });

        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angleOffset += angleStep;
                map.updateAngle(angleStep);
                map.invalidate();
            }
        });

        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angleOffset += -1 * angleStep;
                map.updateAngle(-1 * angleStep);
                map.invalidate();
            }
        });

        reset_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                resetHole();
                map.resetHole();
                ding_sound.start();
            }
        });

    }

    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        map.width = map.getWidth();
        map.height = map.getHeight();
        resetHole();
        map.resetHole();
        map.resetDimens();
    }


    SensorEventListener accelerometerListener = new SensorEventListener(){
        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor sensor = event.sensor;
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelChange[X] = accelVals[X] - event.values[X];
                accelVals[X] = event.values[X];

                accelChange[Y] = accelVals[Y] - event.values[Y];
                accelVals[Y] = event.values[Y];

                accelChange[Z] = accelVals[Z] - event.values[Z];
                accelVals[Z] = event.values[Z];
                if(accelVals[Z] < accelMax){
                    accelMax = accelVals[Z];
                }
            }

            checkSwingPower();
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    };


    SensorEventListener gyroscopeListener = new SensorEventListener(){
        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor sensor = event.sensor;
            if (sensor.getType() == TYPE_GYROSCOPE) {
                gyroVals[X] = event.values[X];
                gyroVals[Y] = event.values[Y];
                gyroVals[Z] = event.values[Z];
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };



    private double calculateDistance(double power){
        double clubConst = 1;
        power = Math.abs(power);
        if(driverSelected){
            clubConst = 260;
            return sigmoid(power,0.45) * clubConst;
        }
        if(ironSelected){
            clubConst = 85;
            return sigmoid(power,0.8) * clubConst;
        }
        if(putterSelected) {
            clubConst = 14;
            return sigmoid(power * clubConst,0.2) * clubConst;
        }
        return 0;
    }

    private void checkSwingPower(){

        boolean unlocked = !drive && !iron && !putt;

        //somewhat pointed down
        if(accelVals[Y] <= -8 || putterSelected) {

            //Valid swing
            if(gyroVals[X] >= 0.1 && accelMax <= -0.8 && unlocked){
                if(driverSelected && accelMax <= -3){
                    swingDrive();
                }else if(ironSelected && accelMax <= -1){
                    swingIron();
                }else if(putterSelected){
                    swingPutt();
                }
            }
        }
    }

    private void swingDrive(){
        drive_sound.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                vibrate(120,255);
            }
        }, 180);
        drive = true;
        windup_lock = true;
        makeSwing();
    }

    private void swingIron(){
        iron_sound.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                vibrate(90,150);
            }
        }, 180);
        iron = true;
        windup_lock = true;
        makeSwing();
    }

    private void swingPutt(){
        putt_sound.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                vibrate(40,60);
            }
        }, 180);
        putt = true;
        windup_lock = true;
        makeSwing();
    }

    private void swingReset(){
        accelMax = 0;
        vibrate(25,255);
        windup_lock = false;
        drive = iron = putt = false;
    }

    private void resetHole(){
        driverSelected = true;
        ironSelected = false;
        putterSelected = false;
        drive_btn.setBackgroundColor(Color.GREEN); //green - selected
        iron_btn.setBackgroundColor(Color.LTGRAY); //grey
        putt_btn.setBackgroundColor(Color.LTGRAY); //grey);
        angleOffset = 0;
        swingCount = 0;
        swingTextView.setText(String.valueOf(swingCount));
        teeX = (int) (map.width * (383.0/770.0));
        teeY = (int) (map.height * (1046.0/1175.0));
        holeX = (int) (map.width * (423.0/770.0));
        holeY = (int) (map.height * (135.0/1175.0));
        x = teeX;
        y = teeY;
        map.resetHole();
        map.invalidate();
        swing_btn.setBackgroundColor(Color.LTGRAY);
    }


    private void makeSwing(){
        double dist = (calculateDistance(accelMax) * pixelsToYards);
        double angleConst = 0.001;
        x += (dist * angleOffset * angleConst);
        y -= (int) (calculateDistance(accelMax) * pixelsToYards);
        map.updatePosition(x,y);
        map.invalidate();
        swingCount++;
        swingTextView.setText(String.valueOf(swingCount));
        swing_btn.setBackgroundColor(Color.LTGRAY);
        if(getDistanceTo(x,y,holeX,holeY) <= 25 && swingCount <= 7){
            //Win condition
            hole_sound.start();
            gotoWinScreen(swingCount);
        }else if(y <= 120 || x <= 0 || x >= 775){
            //out of bounds
            fail_sound.start();
            gotoWinScreen(-1);
        }else if(swingCount > 7){
            //too many swings
            fail_sound.start();
            gotoWinScreen(-2);
        }
    }


    private void gotoWinScreen(int code){
        Intent intent = new Intent(GameActivity.this, WinActivity.class);
        intent.putExtra("SCORE", code);
        startActivity(intent);
        finish();
    }

    private void vibrate(int millis, int amp){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(millis, amp));
        } else {
            //deprecated in API 26
            vibrator.vibrate(millis);
        }
    }


    private double sigmoid(double x, double rate) {

        return ((1.2)/( 1 + Math.pow(Math.E,((-1*rate)*x  + 5))));
    }

    private double getDistanceTo(int x1, int y1, int x2, int y2){
        return Math.sqrt((Math.pow(x2-x1,2)+Math.pow(y2-y1,2)));
    }


}