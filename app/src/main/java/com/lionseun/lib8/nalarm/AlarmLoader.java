package com.lionseun.lib8.nalarm;

import android.content.Context;
import android.content.CursorLoader;

/**
 * Created by lingyang on 6/5/17.
 */

public class AlarmLoader implements AlarmContract.AlarmsColumns {

    private static final String DEFAULT_SORT_ORDER =
            AlarmDatabaseHelper.ALARMS_TABLE_NAME + "." + HOUR + ", " +
                    AlarmDatabaseHelper.ALARMS_TABLE_NAME + "." +  MINUTES + " ASC" + ", " +
                    AlarmDatabaseHelper.ALARMS_TABLE_NAME + "." + AlarmContract.AlarmsColumns._ID + " DESC";
    
    private static final String[] QUERY_ALARMS_COLUMNS = {
            AlarmDatabaseHelper.ALARMS_TABLE_NAME + "." + _ID,
            AlarmDatabaseHelper.ALARMS_TABLE_NAME + "." + NAME,
            AlarmDatabaseHelper.ALARMS_TABLE_NAME + "." + HOUR,
            AlarmDatabaseHelper.ALARMS_TABLE_NAME + "." + MINUTES,
            AlarmDatabaseHelper.ALARMS_TABLE_NAME + "." + LABEL,
            AlarmDatabaseHelper.ALARMS_TABLE_NAME + "." + RINGTONE,
            AlarmDatabaseHelper.ALARMS_TABLE_NAME + "." + DAYS_OF_WEEK,
            AlarmDatabaseHelper.ALARMS_TABLE_NAME + "." + ENABLED,
            AlarmDatabaseHelper.ALARMS_TABLE_NAME + "." + VIBRATE,

    };
    
    public static CursorLoader getAlarmsCursorLoader(Context context) {
        return new CursorLoader(context, AlarmContract.AlarmsColumns.CONTENT_URI, QUERY_ALARMS_COLUMNS, null, null, DEFAULT_SORT_ORDER){
        };
    }
}
