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

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.bluetooth.BluetoothAdapter;
import android.widget.TextView;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;
import static android.view.View.TEXT_ALIGNMENT_TEXT_START;
import static com.bluetoothsmartsaver.Utility.createStatusSpannable;
import static com.bluetoothsmartsaver.Utility.display;
import static com.bluetoothsmartsaver.Utility.displayTNConFirstRun;
import static com.bluetoothsmartsaver.Utility.getActionbarHeight;
import static com.bluetoothsmartsaver.Utility.logI;

/**
 * Constructs the UI elements,
 * and starts / stops the foreground
 * service based on the switch value.
 */
public class MainActivity extends AppCompatActivity {

    // Declare service_intent as a shared variable
    // for onCreate() and onDestroy() methods.
    private Intent service_intent;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private ConstraintLayout parentLayout;
    private TextView popupTitle;
    private TextView popupText;
    private int parentSize;
    private int actionBarHeight;

    // Set logging, depending on the value of the debug flag.
    static final boolean debug = BuildConfig.DEBUG;

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
        // Need to be assigned after the setContentView call.
        final Switch autoSwitch;
        final TextView help;
        final TextView status;

        // Create, set content view and retrieve its elements.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        autoSwitch = findViewById(R.id.autoSwitch);
        help = findViewById(R.id.HelpTextView);
        status = findViewById(R.id.ServiceTextView);
        parentLayout = findViewById(R.id.layout);

        // Display EULA notice, if it is the first time the app runs.
        displayTNConFirstRun(MainActivity.this);

        help.setOnClickListener(new View.OnClickListener() {
            /**
             * Displays a popup, when the help TextView is pressed.
             * The popup can be dismissed when the user clicks elsewhere.
             */
            @Override
            public void onClick(View view) {
                // Initialize a new instance of LayoutInflater service.
                layoutInflater = (LayoutInflater)
                        context.getSystemService(LAYOUT_INFLATER_SERVICE);
                // Inflate the custom view.
                ViewGroup customView = (ViewGroup)
                        layoutInflater.inflate(R.layout.popup_window, null);

                // Get ActionBarHeight.
                actionBarHeight = getActionbarHeight(context);
                // Find Popup Title and text.
                popupTitle = customView.findViewById(R.id.PopupTitleView);
                popupText = customView.findViewById(R.id.PopupTextView);

                // Set text alignment of popup text.
                // For build version greater or equal to Android Oreo (API Level 26)
                // Full text justification is supported, and will also be used below.
                // For older builds, left align justification is used.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    popupText.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
                } else {
                    popupText.setTextAlignment(TEXT_ALIGNMENT_TEXT_START);
                }
                // Dynamically set padding of Popup title,
                // based to a fraction of the action bar height.
                popupTitle.setPadding(
                        0,
                        actionBarHeight / 3,
                        0,
                        actionBarHeight / 3);
                // Get view sizes from parent.
                parentSize = ViewPager.LayoutParams.MATCH_PARENT;
                // Create a focusable PopupWindow.
                popupWindow = new PopupWindow(
                        customView,
                        parentSize,
                        parentSize,
                        true);
                popupWindow.showAtLocation(parentLayout, Gravity.CENTER, 500, 500);
                popupWindow.setBackgroundDrawable(
                        new BitmapDrawable(context.getResources(), (Bitmap) null));
                customView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });

        service_intent = new Intent(this, service.getClass());

        // If the service is running, set the switch ON.
        // Else turn the switch OFF.
        // Also update the service status TextView.
        if (isMyServiceRunning(service.getClass())) {
            autoSwitch.setChecked(true);
            status.setText(
                    createStatusSpannable(context, true),
                    TextView.BufferType.SPANNABLE);
        } else {
            autoSwitch.setChecked(false);
            status.setText(
                    createStatusSpannable(context, false),
                    TextView.BufferType.SPANNABLE);
        }
        // Make TextView text bold, in both cases.
        status.setTypeface(status.getTypeface(), Typeface.BOLD);

        autoSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            /**
             * Checks for changes in the state of the switch.
             * The switch starts / stops the service, based on user interaction.
             */
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // If the switch is ON and the device does not support
                    // Bluetooth, then don't start the service,
                    // display an error message, and set the switch to OFF.
                    // Alternatively, if the switch is ON, and the service
                    // is not running, try to start the service,
                    // and set "Service status: Started"
                    // in the service TextView, if successful.

                    if (bluetooth == null) {
                        display(context.getResources().
                                        getString(R.string.error_bluetooth),
                                        context);
                        autoSwitch.setChecked(false);

                    }
                    else if (!isMyServiceRunning(service.getClass())) {
                        try{
                            startService(service_intent);
                        } catch (SecurityException sec) {
                            display(context.getResources().
                                    getString(R.string.error_permission), context);
                        } catch (IllegalStateException ise) {
                            display(context.getResources().
                                    getString(R.string.error_service_start), context);
                        }
                        status.setText(
                                createStatusSpannable(context, true),
                                TextView.BufferType.SPANNABLE);
                        status.setTypeface(status.getTypeface(), Typeface.BOLD);
                    }
                } else {
                    // If the switch is OFF, try to stop the service,
                    // if it is running, and set "Service status:
                    // Stopped" in the service TextView, if successful.
                    if (isMyServiceRunning(service.getClass())) {
                        try{
                            stopService(service_intent);
                        } catch (SecurityException sec) {
                            display(context.getResources().
                                    getString(R.string.error_permission), context);
                        } catch (IllegalStateException ise) {
                            display(context.getResources().
                                    getString(R.string.error_service_stop), context);
                        }
                        status.setText(
                                createStatusSpannable(context, false),
                                TextView.BufferType.SPANNABLE);
                        status.setTypeface(status.getTypeface(), Typeface.BOLD);
                    }
                }
            }
        });
    }

    /**
     * Call the onStart(), onStop(),
     * onDestroy() methods of the
     * parent class.
     */
    @Override
    public void onStart() { super.onStart(); }

    @Override
    public void onStop() { super.onStop(); }

    @Override
    protected void onDestroy() { super.onDestroy(); }
}
