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

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import static android.content.Context.MODE_PRIVATE;

/**
 * Contains static utility methods,
 * that are required by other classes
 * of the app.
 * */
@SuppressWarnings("WeakerAccess")
public final class Utility {
    // Set logging, depending on the value of the debug flag.
    static final boolean debug = BuildConfig.DEBUG;
    /** Uses Toast to display feedback to the user for a short length of time. */
    protected static void display(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /** If logging is enabled, then we print the log info messages.
     *  Otherwise, the compiler optimizes away the empty method.
     */
    protected static void logI(String tag, String msg){ if(debug) Log.i(tag, msg); }

    /** Dynamically get the height of the Actionbar for scaling other UI elements. */
    protected static int getActionbarHeight(Context context){
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(
                android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(
                    tv.data, context.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    /**
     * Create a Service Status string based on specific inputs,
     * by utilizing the correct string resource from context. */
    protected static Spannable createStatusSpannable(Context context, Boolean started) {
        String text = context.getResources().getString(R.string.service_status);
        String status;
        int color;
        if (started) {
            status = context.getResources().getString(R.string.service_started);
            color = Color.parseColor("#40ff40");
        } else {
            status = context.getResources().getString(R.string.service_stopped);
            color = Color.parseColor("#ff1111");
        }
        String total = text + " " + status;
        Spannable spannable = new SpannableString(total);
        spannable.setSpan(
                new ForegroundColorSpan(color),
                text.length(), total.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    /**
     * Uses shared preferences to display a dialog,
     * on the first run of the app, which contains
     * the TNC of this app.
     */
    public static void displayTNConFirstRun(final Context context) {
        String TNC = context.getResources().getString(R.string.TNC);
        boolean isFirstRun = context
                .getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        if (isFirstRun) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Terms & Conditions")
                    .setMessage(TNC)
                    .setCancelable(false)
                    .setPositiveButton("Accept",
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Set isFirstRun to false,
                            // so this function executes only once.
                            context.getSharedPreferences(
                                    "PREFERENCE", MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("isFirstRun", false)
                                    .apply();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
