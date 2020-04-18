# Motion sensors with Android

## Overview

In this tutorial we will be showing you how to access and implement the motion sensors of an android device into an android studio project, with an example of golfing.



## Step by Step process

### 1) 
Download Android studio [here](https://developer.android.com/studio)

### 2)
Start a new project, and choose an empty activity.

### 3) 
We will only use a single TextView to show the distance of the swing.
Since the empty activy starts with a "Hello World" TextView, we will simply hijack that. Change it's ID in the upper right to something relating to distance, such as `distance_TextView`.

### 4) 
Define your TextView in `MainActivity.java` like this:
```
public class MainActivity extends AppCompatActivity {

    private TextView distanceTextView;
    
    ...
}
```

### 5) 
Instantiate your textView by calling `distanceTextView = findViewById(R.id.distance_TextView);findViewById()` inside the `onCreate()` method provided.

### 6)
Now we will move on to accessing sensors.
add these imports to the top of your java class:
```
import static android.hardware.Sensor.TYPE_GYROSCOPE;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
```
These will let you access the sensors provided to android via the device's hardware.

### 7) 
Define some sensor stuff for use later at the top of your class:
```
private SensorManager sensorManager;
private Sensor accel;
private Sensor gyro;
```

### 8)
... and instantiate them inside `onCreate()`
```
sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
```

### 9) 
Now we will implement an accelerometer listener.
Create a new method inside `MainActivity` like so:
```
 SensorEventListener accelerometerListener = new SensorEventListener(){
        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor sensor = event.sensor;
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    };
}
```
don't worry about leaving `onAccuracyChanged()` blank, we won't need it.

### 10) 
let's make the same thing for the gyroscope.
```
SensorEventListener gyroscopeListener = new SensorEventListener(){
        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor sensor = event.sensor;
            if (sensor.getType() == TYPE_GYROSCOPE) {

            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
};
```

### 11) 
You can see the space inside 
```
if (sensor.getType() == TYPE_GYROSCOPE) {

}
```
is left blank. This is where we will be accessing motion sensor data.
instantiate some arrays for the class so we can access the data from anywhere else in the class.
```
private double accelVals[];
private double gyroVals[];
```

### 12)
And instantiate them inside `onCreate()` to be arrays of size 3, one index for each axis (X, Y, Z) of each sensor.

```
accelVals = new double[3];
gyroVals = new double[3];
```

### 13)
Now we can go in to the `onSensorChanged()` and update the values arrays.
```
if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
    accelVals[X] = event.values[X];
    accelVals[Y] = event.values[Y];
    accelVals[Z] = event.values[Z];
}
```
0 for X, 1 for Y, 2 for Z. You can define these as constants to make it easier, as I did, if you wish.

### 14)
Update the gyroscope values the same way:
```
if (sensor.getType() == TYPE_GYROSCOPE) {
    gyroVals[X] = event.values[X];
    gyroVals[Y] = event.values[Y];
    gyroVals[Z] = event.values[Z];
}
```
### 15)
Now hook up the `sensorEventListener`s to the `sensorManager`
```
sensorManager.registerListener(accelerometerListener, accel , SensorManager.SENSOR_DELAY_GAME);
sensorManager.registerListener(gyroscopeListener, gyro , SensorManager.SENSOR_DELAY_GAME);
```
This will update the sensors quite quickly.


### 16)
Ok great! now what do we do with all this data now?
Well now we can get into detecting a swing. The main driving force of the swing is negative acceleration in the X axis (assuming the screen is facing the direction of your swing);

```
private void checkSwingPower(){

        //somewhat pointed down
        if(accelVals[Y] <= -8) {

            //Valid swing
            if(gyroVals[X] >= 0.1 accelVals[X] <= -1){
                    swing();
            }
        }
    }
}
```
This is the method I came up with to detect a swing. Basically, It checks if the phone is pointed downward (which it detects because of gravity). Then it measures a slight rotation as your phone starts coming up, and the acceleration in the -X direction is larger than a certain constant. 
Feel free to play around with the numbers!

### 17)
ok so now that we can see if the user made a swing, let's update the TextView with the 'distance' of their swing.

make a swing() method that calls a new method to calculate the distance based on the X acceleration and sets our TextView to that value.

```
private void swing(){
    distanceTextView.setText(String.valueOf(calculateDistance(accelVals[X])) + " yards");
}
```

### 18) 
Then we implement `calculateDistance()`

```
private double calculateDistance(){
        clubConst = 85;
        power = Math.abs(accelVals[X]);
        return sigmoid(power, 0.8) * clubConst;
}

private double sigmoid(double x, double rate) {
    return ((1.2)/( 1 + Math.pow(Math.E,((-1*rate)*x  + 5))));
}
```
I found that a value of `85` is a good value to have for the iron club. We use a sigmoid function so there is a maximum distance possible. Again, play around with the numbers!

### 19)

So now we can get the distance of the swing, but that doesn't really give us a realistic golf feeling, so let's add a vibration to make it feel like we just hit a golf ball!

### 20)

import these packages:

```
import android.os.VibrationEffect;
import android.os.Vibrator;
```

### 21)
define a vibrator
`private Vibrator vibrator;`
and instantiate it
`vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);`

### 22)
implement a `vibrate()` method for easy use:
```
private void vibrate(int millis, int amp){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
       vibrator.vibrate(VibrationEffect.createOneShot(millis, amp));
    } else {
        //deprecated in API 26
        vibrator.vibrate(millis);
    }
}
```
### 23)
Then in the `swing()` method, let's add a vibration! 
This will give the user some haptic feedback to let them know that they hit the ball!

```
private void swing(){
    new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                vibrate(90,255);
            }
        }, 180);
    distanceTextView.setText(String.valueOf(calculateDistance(accelVals[X])) + " yards");
}
```
Once again, adjust and tweak these numbers. Not all hardware is made equal and things likely won't be the same for your device.


## Conclusion
It is surprisingly easy to access the motion sensors of your device within android. 
We just did a simple golf swing in this tutorial, but the possibilities are pretty much endless.
Motion sensors create a fun fusion between technology and the physical world. Gaming systems like the wii proved this. 
Keep playing around with motion sensors on android, and make some other games! Maybe tennis, or bowling, or driving a car!

Check out https://github.com/townsenb/LiveGolf to see how these ideas can be expanded upon.
