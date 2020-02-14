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

    private void display(String text){
        //Use Toast to display feedback to the user.
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

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
                    display("Enabling Bluetooth...");
                    if (bluetooth == null) {
                        display("Error: Device doesn't support Bluetooth");
                    } else if (!bluetooth.isEnabled()) {
                        // Bluetooth is disabled, enable it.
                        bluetooth.enable();
                        display("Bluetooth has been enabled!");
                    }
                } else {
                    display("Disabling Bluetooth...");
                    if (bluetooth == null) {
                        display("Error: Device doesn't support Bluetooth");
                    } else if (bluetooth.isEnabled()) {
                        // Bluetooth is enabled, disable it.
                        bluetooth.disable();
                        display("Bluetooth has been disabled!");
                    }
                }
            }
        });

    }
}

