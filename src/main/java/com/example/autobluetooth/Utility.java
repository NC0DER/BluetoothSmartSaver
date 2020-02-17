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
import android.widget.Toast;

@SuppressWarnings("WeakerAccess")
public final class Utility {

    protected static void display(String text, Context context) {
        //Use Toast to display feedback to the user.
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
