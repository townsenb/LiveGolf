package com.example.livegolf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

public class MainActivity extends AppCompatActivity {

    private final int X = 0;
    private final int Y = 1;
    private final int Z = 2;

    private SensorManager sensorManager;
    private Sensor accel;
    private Sensor gyro;

    private TextView accelVal_text;
    private TextView gyroVal_text;

    private TextView distance;

    private double accelVals[];
    private double accelChange[];
    private double gyroVals[];
    private double accelMax = 0;

    private Button swing_btn;
    private TextView swing_type;

    private Button drive_btn;
    private Button iron_btn;
    private Button putt_btn;

    private Button left_btn;
    private Button right_btn;

    private Vibrator vibrator;

    private MediaPlayer drive_sound, iron_sound, putt_sound, hole_sound, ding_sound;

    private boolean windup_lock = true;
    private boolean drive, iron, putt = false;
    private boolean driverSelected, ironSelected, putterSelected = false;

    private DrawView map;

    private int angleStep = 20;
    private int angleOffset = 0;
    public TextView angleTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = findViewById(R.id.mapView);
        map.setBackgroundColor(Color.GRAY);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorManager.registerListener(accelerometerListener, accel , SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(gyroscopeListener, gyro , SensorManager.SENSOR_DELAY_GAME);

        accelVal_text = findViewById(R.id.acc_val);
        gyroVal_text = findViewById(R.id.gyro_val);
        angleTextView = findViewById(R.id.angleVal);

        distance = findViewById(R.id.distance);

        accelVals = new double[3];
        accelChange = new double[3];
        gyroVals = new double[3];

        drive_sound = MediaPlayer.create(this, R.raw.drive);
        iron_sound = MediaPlayer.create(this, R.raw.iron);
        putt_sound = MediaPlayer.create(this, R.raw.putt);
        hole_sound = MediaPlayer.create(this, R.raw.hole);
        ding_sound = MediaPlayer.create(this, R.raw.ding);

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

        swing_type = findViewById(R.id.Swing_pos);

        //Button listeners
        swing_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
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
                angleTextView.setText(String.valueOf(angleOffset));
            }
        });

        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angleOffset += -1 * angleStep;
                map.updateAngle(-1 * angleStep);
                map.invalidate();
                angleTextView.setText(String.valueOf(angleOffset));
            }
        });

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
            clubConst = 120;
            return sigmoid(power,0.8) * clubConst;
        }
        if(putterSelected) {
            clubConst = 2;
            return sigmoid(power * clubConst,2) * power * clubConst;
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
        displaySwingInfo("Driver: ",accelChange[Z],gyroVals[X],accelMax);
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
        displaySwingInfo("Iron: ",accelChange[Z],gyroVals[X],accelMax);
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
        displaySwingInfo("Putter: ",accelChange[Z],gyroVals[X],accelMax);
    }

    private void swingReset(){
        accelMax = 0;
        vibrate(25,255);
        windup_lock = false;
        drive = iron = putt = false;
        swing_type.setText("make a swing");
        accelVal_text.setText("0");
        gyroVal_text.setText("0");
        distance.setText("0 yards");
    }

    private void vibrate(int millis, int amp){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(millis, amp));
        } else {
            //deprecated in API 26
            vibrator.vibrate(millis);
        }
    }

    private void displaySwingInfo(String type, double accel, double gyro, double max){
        swing_type.setText(type+ " - max: " + new DecimalFormat("#.##").format(max));
        accelVal_text.setText(new DecimalFormat("#.##").format(accel));
        gyroVal_text.setText(new DecimalFormat("#.##").format(gyro));
        distance.setText(new DecimalFormat("#.##").format(calculateDistance(max)) + " yards");
    }

    private double sigmoid(double x, double rate) {

        return ((1.2)/( 1 + Math.pow(Math.E,((-1*rate)*x  + 5))));
    }


}
