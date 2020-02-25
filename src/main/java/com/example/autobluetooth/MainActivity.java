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

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.bluetooth.BluetoothAdapter;

import static com.example.autobluetooth.Utility.display;
import static com.example.autobluetooth.Utility.logI;

/**
 * Constructs the UI elements,
 * and starts / stops the foreground
 * service based on the switch value.
 */
public class MainActivity extends AppCompatActivity {

    // Declare service_intent as a shared variable
    // for onCreate() and onDestroy() methods.
    private Intent service_intent;
    /**
    * Checks where the service, is running.
    * Returns True if it is, false otherwise.
    * It also logs the service status,
    * when the Debug Flag is set.
    */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                logI ("Service status", "Running");
                return true;
            }
        }
        logI ("Service status", "Not running");
        return false;
    }

    /**
     * Creates the UI elements.
     * Implements the handler
     * for the switch change.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final HandleBluetoothByCallStateStatus service =
                new HandleBluetoothByCallStateStatus();
        final BluetoothAdapter bluetooth =
                BluetoothAdapter.getDefaultAdapter();
        final Context context =
                getApplicationContext();
        // Needs to be assigned after the setContentView call.
        final Switch autoSwitch;

        // Create and set content view and its switch.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        autoSwitch = findViewById(R.id.autoSwitch);

        //If the device doesn't support Bluetooth,
        // then do an early clean exit.
        if (bluetooth == null) {
            finishAndRemoveTask();
        }

        service_intent = new Intent(this, service.getClass());

        // If the service is running, set the switch ON.
        // Else turn the switch OFF.
        if (isMyServiceRunning(service.getClass())) {
            autoSwitch.setChecked(true);
        } else {
            autoSwitch.setChecked(false);
        }

        autoSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            /**
             * Checks for changes in the state of the switch.
             * The switch starts / stops the service, based on user interaction.
             */
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // If the switch is ON, start the service, if it is not running.
                    if (!isMyServiceRunning(service.getClass())) {
                        display("Starting Service...", context);
                        startService(service_intent);
                        display("Service has been started.", context);
                    }
                } else {
                    // If the switch is OFF, stop the service, if it is running.
                    if (isMyServiceRunning(service.getClass())) {
                        display("Stopping Service...", context);
                        stopService(service_intent);
                        display("Service has been stopped.", context);
                    }
                }
            }
        });
    }
    /**
     * Calls the onDestroy()
     * method of the parent class.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
