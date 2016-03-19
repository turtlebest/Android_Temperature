/**
 * AmbientSensor.java
 * The Ambient Sensor class to control and get data from the sensor device in the mobile
 *
 * Created by Jessica Huang.
 */
package nyu.cs.jessicahuang.temperaturec.Controller;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import nyu.cs.jessicahuang.temperaturec.MainActivity;

public class AmbientSensor implements SensorEventListener {
    private final String logId = "AmbientSensor";

    private final SensorManager sm;
    private final Sensor ambientTemperatureSensor;
    private final MainActivity ma;

    public AmbientSensor(SensorManager sm, MainActivity ma){
        this.sm = sm;
        this.ma = ma;

        ambientTemperatureSensor = sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        if (ambientTemperatureSensor == null) {
            //if not , broadcast and stop sensor service
            Log.i(logId, "ambient temperature sensor not found");
            ma.setAmbientSensorState(false);
        } else {
            Log.i(logId, "sensor started");
            //set the listener and update the temperature in SENSOR_DELAY_NORMAL time
            sm.registerListener(this, ambientTemperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i(logId, "onSensorChanged");
        // Here we call a method in MainActivity and pass it the value from the SensorChanged event,
        // the first number is the current temperature
        float ambientTemperature = event.values[0];
        ma.setAmbientTextViewValue(ambientTemperature);
    }


    public void onResume() {
        Log.i(logId, "onResume");
        sm.registerListener(this, ambientTemperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        Log.i(logId, "onPause");
        sm.unregisterListener(this);
    }
}