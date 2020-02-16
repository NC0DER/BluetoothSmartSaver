package com.example.autobluetooth;

import android.content.Context;
import android.widget.Toast;

@SuppressWarnings("WeakerAccess")
public final class Utility {

    protected static void display(String text, Context context) {
        //Use Toast to display feedback to the user.
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
