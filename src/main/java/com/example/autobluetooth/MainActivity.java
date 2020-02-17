/*
 * Copyright 2020 Nikolaos Giarelis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
