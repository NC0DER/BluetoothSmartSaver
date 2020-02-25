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

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

/**
 * Contains static utility methods,
 * for text output to the user's screen
 * or to the console for logging.
 * */
@SuppressWarnings("WeakerAccess")
public final class Utility {
    // Set logging, depending on the value of the debug flag.
    static final boolean LOG = BuildConfig.DEBUG;
    /** Uses Toast to display feedback to the user for a short length of time. */
    protected static void display(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /** If logging is enabled, then we print the log info messages.
     *  Otherwise, the compiler optimizes away the empty method.
     */
    protected static void logI(String tag, String msg){ if(LOG) Log.i(tag, msg); }
    /** Create a Service Status string based on specific inputs. */
    protected static Spannable createStatusSpannable(String text, Boolean started) {
        String status;
        int color;
        if (started) {
            status = "Started";
            color = Color.parseColor("#40ff40");
        } else {
            status = "Stopped";
            color = Color.parseColor("#ff1111");
        }
        String total = text + status;
        Spannable spannable = new SpannableString(total);
        spannable.setSpan(
                new ForegroundColorSpan(color),
                text.length(), total.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }
}
