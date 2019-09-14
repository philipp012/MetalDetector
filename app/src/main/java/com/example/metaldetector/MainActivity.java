package com.example.metaldetector;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private TextView value;
    private SensorManager sensorManager;
    public static DecimalFormat DECIMAL_FORMATTER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        value = findViewById(R.id.value);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DECIMAL_FORMATTER = new DecimalFormat("#", symbols);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set Sensormanager to listen for the magnetic field sensor
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // deactivate sensors to prevent drain of battery
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            // magnetic field sensor returns 3 values for the 3 axis
            float magX = event.values[0];
            float magY = event.values[1];
            float magZ = event.values[2];

            // Compute the total magnitude of the 3 returned values
            double magnitude = Math.sqrt((magX * magX) + (magY * magY) + (magZ * magZ));

            // set Textfield to magnitude +
            value.setText(DECIMAL_FORMATTER.format(magnitude) + " \u00B5Tesla");
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
