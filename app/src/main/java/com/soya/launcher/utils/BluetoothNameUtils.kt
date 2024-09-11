/*
 * Copyright (C) 2014 The Android Open Source Project
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
package com.soya.launcher.utils

import android.text.TextUtils
import com.soya.launcher.bean.LedConfiguration
import java.util.regex.Pattern

/**
 * Helper functions and constants for reading the information passed via the Bluetooth
 * name of the hub.
 */
object BluetoothNameUtils {
    /*
     * matches string that
     *   - may or may not start with a one- or two-digit number followed by a space
     *   - a string surrounded by quotes
     *   - a string surrounded by parentheses
     */
    private val NAME_PATTERN: Pattern = Pattern.compile(
        "\"([0-9]{0,3}) ?(.*)\" \\((.*)\\)", Pattern.CASE_INSENSITIVE
    )
    private val COLOR_PATTERN: Pattern = Pattern.compile(
        "#([0-9a-f]{6})-#([0-9a-f]{6})(p?)(t?)(.*)", Pattern.CASE_INSENSITIVE
    )

    /**
     * Decode the setup type integer from the Bluetooth device name.
     * @param bluetoothName
     * @return The integer value of the setup code, or -1 if no code is present.
     */
    fun getSetupType(bluetoothName: String?): Int {
        val matcher = NAME_PATTERN.matcher(bluetoothName)
        if (!matcher.matches()) {
            return -1
        }

        val typeStr = matcher.group(1)

        return if (typeStr != null) {
            try {
                typeStr.toInt()
            } catch (e: NumberFormatException) {
                -1
            }
        } else {
            -1
        }
    }

    /**
     * Decode the LED configuration contained in the input string.
     * @param bluetoothName
     * @return The LedConfiguration or none if one can not be parsed from the string.
     */
    fun getColorConfiguration(bluetoothName: String?): LedConfiguration? {
        val matcher = NAME_PATTERN.matcher(bluetoothName)
        if (!matcher.matches()) {
            return null
        }

        val cs = matcher.group(3)
        if (TextUtils.isEmpty(cs)) {
            return null
        } else {
            val cm = COLOR_PATTERN.matcher(cs)
            if (!cm.matches()) {
                return null
            }
            val config = LedConfiguration(
                -0x1000000 or cm.group(1).toInt(16),
                -0x1000000 or cm.group(2).toInt(16),
                "p" == cm.group(3)
            )
            config.isTransient = "t" == cm.group(4)
            return config
        }
    }

    /**
     * Check if the name matches the expected format for a hub Bluetooth name.
     * @param name
     * @return true if the pattern matches, false if it doesn't.
     */
    fun isValidName(name: String?): Boolean {
        val matcher = NAME_PATTERN.matcher(name)
        return matcher.matches()
    }
}
