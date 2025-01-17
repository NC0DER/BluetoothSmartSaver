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

package com.bluetoothsmartsaver;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import static com.bluetoothsmartsaver.Utility.logI;
/**
 * Manages the lifecycle of a foreground service,
 * which check the phone call status.
 * Depending on the status, this service
 * enables or disables bluetooth.
 * */
public class HandleBluetoothByCallStateStatus extends Service {
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
        // Initialize large bitmap icon, to be used in the notification.
        Bitmap large_icon = BitmapFactory.decodeResource(
                getResources(), R.drawable.launcher_icon_foreground);
        // Initialize the notification channel.
        NotificationChannel channel =
                new NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                        channelName,
                        NotificationManager.IMPORTANCE_NONE);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        // Create the notification channel.
        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(channel);

        // Build the notification.
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setSmallIcon(R.drawable.notification_icon)
                .setLargeIcon(large_icon)
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
                if(state == TelephonyManager.CALL_STATE_OFFHOOK ||
                        state == TelephonyManager.CALL_STATE_RINGING){
                    logI("State","Phone ringing or in-call");
                    // Enable bluetooth adapter, if it is disabled.
                    if (!bluetooth.isEnabled()) {
                        bluetooth.enable();
                    }
                }
                if(state == TelephonyManager.CALL_STATE_IDLE){
                    logI("State","Phone neither ringing nor in-call.");
                    // Disable bluetooth adapter, if it is enabled.
                    if (bluetooth.isEnabled()) {
                        bluetooth.disable();
                    }
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

