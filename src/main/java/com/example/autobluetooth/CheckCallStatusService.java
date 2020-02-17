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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import static com.example.autobluetooth.Utility.logI;
/**
 * Manages the lifecycle of a foreground service
 * which check and logs the phone call status.
 * */
public class CheckCallStatusService extends Service {
    /**
     * Called by the system when the service is first created.
     * The service is being started differently, depending
     * on android build version of the device.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    /**
     * Implements custom foreground service code,
     * for devices that have a build Version of
     * Android Oreo 8.0 and above, due to api changes
     * on the aforementioned version.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel channel =
                new NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                        channelName,
                        NotificationManager.IMPORTANCE_NONE);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(channel);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    /**
     * Called by the system, every time MainActivity explicitly
     * starts the service by calling the startService method.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startCallStateListener();
        return START_STICKY;
    }

    /** Called by the system, when the service is being stopped. */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /** Complementary method to manually start the PhoneStateListener. */
    public void startCallStateListener() {
        // Initialize the variable that manages phone call state.
        final TelephonyManager telephone =
            (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        final BluetoothAdapter bluetooth =
                BluetoothAdapter.getDefaultAdapter();


        PhoneStateListener callStateListener = new PhoneStateListener() {
            /** Detects the call state status, during changes in the call state. */
            public void onCallStateChanged(int state, String incomingNumber) {
                if(state == TelephonyManager.CALL_STATE_OFFHOOK){
                    // Bluetooth is disabled, enable it.
                    if (!bluetooth.isEnabled()) {
                        bluetooth.enable();
                    }
                    logI("State:","Phone is Currently in A call");
                }
                if(state == TelephonyManager.CALL_STATE_IDLE){
                    // Bluetooth is enabled, disable it.
                    if (bluetooth.isEnabled()) {
                        bluetooth.disable();
                    }
                    logI("State:","phone is neither ringing nor in a call");
                }
            }
        };
        telephone.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * The service is not bound, therefore the onBind()
     * method is implemented as a Nullable.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

