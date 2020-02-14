package com.example.autobluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Switch autoSwitch;
    BluetoothAdapter bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        autoSwitch = findViewById(R.id.autoSwitch);
        bluetooth = BluetoothAdapter.getDefaultAdapter();

        // set the switch to ON
        autoSwitch.setChecked(true);
        // attach a listener to check for changes in state
        autoSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "Enabling Bluetooth...",Toast.LENGTH_SHORT).show();
                    if (bluetooth == null) {
                        Toast.makeText(getApplicationContext(), "Error: Device doesn't support Bluetooth",Toast.LENGTH_SHORT).show();
                    } else if (!bluetooth.isEnabled()) {
                        // Bluetooth is disabled, enable it.
                        bluetooth.enable();
                        Toast.makeText(getApplicationContext(), "Bluetooth has been enabled!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Disabling Bluetooth...", Toast.LENGTH_SHORT).show();
                    if (bluetooth == null) {
                        Toast.makeText(getApplicationContext(), "Error: Device doesn't support Bluetooth",Toast.LENGTH_SHORT).show();
                    } else if (bluetooth.isEnabled()) {
                        // Bluetooth is enabled, disable it.
                        bluetooth.disable();
                        Toast.makeText(getApplicationContext(), "Bluetooth has been disabled!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}

