/*
 * Copyright (C) 2013 The Android Open Source Project
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
package com.lionseun.lib8.nalarm;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * <p>
 * The contract between the clock provider and desk clock. Contains
 * definitions for the supported URIs and data columns.
 * </p>
 * <h3>Overview</h3>
 * <p>
 * AlarmContract defines the data model of clock related information.
 * This data is stored in a number of tables:
 * </p>
 * <ul>
 * <li>The {@link AlarmsColumns} table holds the user created alarms</li>
 * alarm in the AlarmsColumn table.
 * </li>
 * </ul>
 */
public final class AlarmContract {
    /**
     * This authority is used for writing to or querying from the clock
     * provider.
     */
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID;

    /**
     * This utility class cannot be instantiated
     */
    private AlarmContract() {}
    
    /**
     * Constants for the Alarms table, which contains the user created alarms.
     */
    protected interface AlarmsColumns extends BaseColumns {
        /**
         * The content:// style URL for this table.
         */
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/alarms");

        /**
         * Alarm name
         * 
         * <p>Type: STRING</p>
         */
        String NAME = "name";

        /**
         * Hour in 24-hour localtime 0 - 23.
         * <p>Type: INTEGER</p>
         */
        String HOUR = "hour";

        /**
         * Minutes in localtime 0 - 59.
         * <p>Type: INTEGER</p>
         */
        String MINUTES = "minutes";

        /**
         * Alarm label.
         *
         * <p>Type: STRING</p>
         */
        String LABEL = "label";
        
        /**
         * Audio alert to play when alarm triggers. Null entry
         * means use system default and entry that equal
         * Uri.EMPTY.toString() means no ringtone.
         *
         * <p>Type: STRING</p>
         */
        String RINGTONE = "ringtone";
        
        /**
         * Days of the week encoded as a bit set.
         * <p>Type: INTEGER</p>
         *
         * {@link com.lionseun.lib8.nalarm.Weekdays}
         */
        String DAYS_OF_WEEK = "daysofweek";

        /**
         * True if alarm is active.
         * <p>Type: BOOLEAN</p>
         */
        String ENABLED = "enabled";

        /**
         * True if alarm should vibrate
         * <p>Type: BOOLEAN</p>
         */
        String VIBRATE = "vibrate";
    }
}
