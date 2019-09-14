package com.example.metaldetector;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

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

        // get scan button
        Button scanBtn = findViewById(R.id.scanButton);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.setPackage("com.google.zxing.client.android");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
            }
        });

    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                log(contents);
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
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

    private void log(String logmessage) {
        Intent intent = new Intent("ch.appquest.intent.LOG");

        if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
            Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
            return;
        }
        JSONObject result = new JSONObject();
        try {
            result.put("task", "Metalldetektor");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            result.put("solution", logmessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        intent.putExtra("ch.appquest.logmessage", result.toString());

        startActivity(intent);
    }
}
