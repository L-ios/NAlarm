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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class for opening the database from multiple providers.  Also provides
 * some common functionality.
 */
class AlarmDatabaseHelper extends SQLiteOpenHelper {
    /**
     * Alarm Database Version.
     **/
    private static final int VERSION_1 = 1;

    // This creates a default alarm at 8:30 for every Mon,Tue,Wed,Thu,Fri
    // TODO: 6/3/17 change this test data, alarm 1 and alarm 2 
    private static final String DEFAULT_ALARM_1 = "('get up', 8, 30, 31, 0, 1, '', NULL);";

    // This creates a default alarm at 9:30 for every Sat,Sun
    private static final String DEFAULT_ALARM_2 = "('work', 9, 00, 96, 0, 1, '', NULL);";

    // Database and table names
    static final String DATABASE_NAME = "alarms.db";
    static final String ALARMS_TABLE_NAME = "alarms";

    private static void createAlarmsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ALARMS_TABLE_NAME + " (" +
                AlarmContract.AlarmsColumns._ID + " INTEGER PRIMARY KEY," +
                AlarmContract.AlarmsColumns.NAME + " TEXT NOT NULL, " +
                AlarmContract.AlarmsColumns.HOUR + " INTEGER NOT NULL, " +
                AlarmContract.AlarmsColumns.MINUTES + " INTEGER NOT NULL, " +
                AlarmContract.AlarmsColumns.DAYS_OF_WEEK + " INTEGER NOT NULL, " +
                AlarmContract.AlarmsColumns.ENABLED + " INTEGER NOT NULL, " +
                AlarmContract.AlarmsColumns.VIBRATE + " INTEGER NOT NULL, " +
                AlarmContract.AlarmsColumns.LABEL + " TEXT NOT NULL, " +
                AlarmContract.AlarmsColumns.RINGTONE + " TEXT);");
        LogUtils.i("Alarms Table created");
    }

    public AlarmDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAlarmsTable(db);

        // insert default alarms
        LogUtils.i("Inserting default alarms");
        String cs = ", "; //comma and space
        String insertMe = "INSERT INTO " + ALARMS_TABLE_NAME + " (" +
                AlarmContract.AlarmsColumns.NAME + cs +
                AlarmContract.AlarmsColumns.HOUR + cs +
                AlarmContract.AlarmsColumns.MINUTES + cs +
                AlarmContract.AlarmsColumns.DAYS_OF_WEEK + cs +
                AlarmContract.AlarmsColumns.ENABLED + cs +
                AlarmContract.AlarmsColumns.VIBRATE + cs +
                AlarmContract.AlarmsColumns.LABEL + cs +
                AlarmContract.AlarmsColumns.RINGTONE +  ") VALUES ";
        db.execSQL(insertMe + DEFAULT_ALARM_1);
        db.execSQL(insertMe + DEFAULT_ALARM_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
        LogUtils.v("Upgrading alarms database from version %d to %d", oldVersion, currentVersion);
    }

    long fixAlarmInsert(ContentValues values) {
        // TODO: 6/5/17 need fixed this method 
        // Why are we doing this? Is this not a programming bug if we try to
        // insert an already used id?
        final SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        long rowId = -1;
        try {
            // Check if we are trying to re-use an existing id.
            final Object value = values.get(AlarmContract.AlarmsColumns._ID);
            if (value != null) {
                long id = (Long) value;
                if (id > -1) {
                    final String[] columns = {AlarmContract.AlarmsColumns._ID};
                    final String selection = AlarmContract.AlarmsColumns._ID + " = ?";
                    final String[] selectionArgs = {String.valueOf(id)};
                    try (Cursor cursor = db.query(ALARMS_TABLE_NAME, columns, selection,
                            selectionArgs, null, null, null)) {
                        if (cursor.moveToFirst()) {
                            // Record exists. Remove the id so sqlite can generate a new one.
                            values.putNull(AlarmContract.AlarmsColumns._ID);
                        }
                    }
                }
            }

            rowId = db.insert(ALARMS_TABLE_NAME, AlarmContract.AlarmsColumns.RINGTONE, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        if (rowId < 0) {
            throw new SQLException("Failed to insert row");
        }
        LogUtils.v("Added alarm rowId = " + rowId);

        return rowId;
    }
}
