package com.example.autobluetooth;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.autobluetooth.Utility.display;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.util.Log;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.bluetooth.BluetoothAdapter;

public class MainActivity extends AppCompatActivity {

    private Intent service_intent;
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final CheckCallStatusService service;
        final Switch autoSwitch;
        final BluetoothAdapter bluetooth;
        final Context context;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autoSwitch = findViewById(R.id.autoSwitch);
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        context = getApplicationContext();

        // If the device doesn't support Bluetooth, then do an early clean exit.
        //if (bluetooth == null) {
        //  finishAndRemoveTask();
        //}

        // set the switch to ON
        autoSwitch.setChecked(true);
        // attach a listener to check for changes in state
        autoSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    display("True", context);
            } else {
                if (bluetooth.isEnabled()) {
                    // Bluetooth is enabled, disable it.
                    bluetooth.disable();
                    }
                }
            }
        });

        service = new CheckCallStatusService();
        service_intent = new Intent(this, service.getClass());
        if (!isMyServiceRunning(service.getClass())) {
            startService(service_intent);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(service_intent);
        super.onDestroy();
    }
}
