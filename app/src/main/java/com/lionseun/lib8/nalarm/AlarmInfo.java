package com.lionseun.lib8.nalarm;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by lingyang on 6/1/17.
 */

public class AlarmInfo implements Parcelable, AlarmContract.AlarmsColumns {

    public static final long INVALID_ID = -1;
    
    public long id;
    public String name;
    public int hour;
    public int minutes;
    public String label;
    private Uri ringtone;
    public Weekdays daysOfWeek;
    public boolean enabled;
    public boolean vibrate = true;

    public static final int ID_INDEX = 0;
    public static final int NAME_INDEX = 1;
    public static final int HOUR_INDEX = 2;
    public static final int MINUTES_INDEX = 3;
    public static final int LABEL_INDEX = 4;
    public static final int RINGTONE_INDEX = 5;
    public static final int DAYS_OF_WEEK_INDEX = 6;
    public static final int ENABLED_INDEX = 7;
    public static final int VIBRATE_INDEX = 8;

    private static final String[] QUERY_COLUMNS = {
            _ID,
            NAME,
            HOUR,
            MINUTES,
            LABEL,
            RINGTONE,
            DAYS_OF_WEEK,
            ENABLED,
            VIBRATE,
    };
    
    
    AlarmInfo(String name, int hour, int minutes) {
        if (name == null) {
            this.name = "";
        } else {
            this.name = name;
        }
        this.hour = hour;
        this.minutes = minutes;
        label = "";
        daysOfWeek = Weekdays.fromBits(0x1f);
        enabled = false;
    }
    
    AlarmInfo(Cursor c) {
        this.id = c.getLong(ID_INDEX);
        this.name = c.getString(NAME_INDEX);
        this.hour = c.getInt(HOUR_INDEX);
        this.minutes = c.getInt(MINUTES_INDEX);
        this.label = c.getString(LABEL_INDEX);
        String uriString = c.getString(RINGTONE_INDEX);
        if (uriString != null) {
            this.ringtone = Uri.parse(uriString);
        }
        this.daysOfWeek = Weekdays.fromBits(c.getInt(DAYS_OF_WEEK_INDEX));
        this.enabled = c.getInt(ENABLED_INDEX) != 0;
        this.vibrate = c.getInt(VIBRATE_INDEX) != 0;
    }

    public static AlarmInfo addAlarm(ContentResolver contentResolver, AlarmInfo alarmInfo) {
        ContentValues values = createContentValues(alarmInfo);
        Uri uri = contentResolver.insert(CONTENT_URI, values);
        alarmInfo.id = getId(uri);
        return alarmInfo;
    }

    public static boolean updateAlarm(ContentResolver contentResolver, AlarmInfo alarmInfo) {
        if (alarmInfo.id == INVALID_ID) return false;
        ContentValues values = createContentValues(alarmInfo);
        long rowsUpdated = contentResolver.update(getContentUri(alarmInfo.id), values, null, null);
        return rowsUpdated == 1;
    }

    /**
     * Get alarm by id.
     *
     * @param cr provides access to the content model
     * @param alarmId for the desired alarm.
     * @return alarm if found, null otherwise
     */
    public static AlarmInfo getAlarm(ContentResolver cr, long alarmId) {
        try (Cursor cursor = cr.query(getContentUri(alarmId), QUERY_COLUMNS, null, null, null)) {
            if (cursor.moveToFirst()) {
                return new AlarmInfo(cursor);
            }
        }

        return null;
    }
    
    public static Uri getContentUri(long alarmId) {
        return ContentUris.withAppendedId(CONTENT_URI, alarmId);
    }

    public static boolean deleteAlarm(ContentResolver contentResolver, long alarmId) {
        if (alarmId == INVALID_ID) return false;
        int deletedRows = contentResolver.delete(getContentUri(alarmId), "", null);
        return deletedRows == 1;
    }

    public AlarmInstance createInstanceAfter(Calendar time) {
        Calendar nextInstanceTime = getNextAlarmTime(time);
        AlarmInstance result = new AlarmInstance(nextInstanceTime, id);
        result.mVibrate = vibrate;
        result.mLabel = label;
        result.mRingtone = ringtone;
        return result;
    }

    public Calendar getNextAlarmTime(Calendar currentTime) {
        final Calendar nextInstanceTime = Calendar.getInstance(currentTime.getTimeZone());
        nextInstanceTime.set(Calendar.YEAR, currentTime.get(Calendar.YEAR));
        nextInstanceTime.set(Calendar.MONTH, currentTime.get(Calendar.MONTH));
        nextInstanceTime.set(Calendar.DAY_OF_MONTH, currentTime.get(Calendar.DAY_OF_MONTH));
        nextInstanceTime.set(Calendar.HOUR_OF_DAY, hour);
        nextInstanceTime.set(Calendar.MINUTE, minutes);
        nextInstanceTime.set(Calendar.SECOND, 0);
        nextInstanceTime.set(Calendar.MILLISECOND, 0);

        // If we are still behind the passed in currentTime, then add a day
        if (nextInstanceTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
            nextInstanceTime.add(Calendar.DAY_OF_YEAR, 1);
        }

        // The day of the week might be invalid, so find next valid one
        final int addDays = daysOfWeek.getDistanceToNextDay(nextInstanceTime);
        if (addDays > 0) {
            nextInstanceTime.add(Calendar.DAY_OF_WEEK, addDays);
        }

        // Daylight Savings Time can alter the hours and minutes when adjusting the day above.
        // Reset the desired hour and minute now that the correct day has been chosen.
        nextInstanceTime.set(Calendar.HOUR_OF_DAY, hour);
        nextInstanceTime.set(Calendar.MINUTE, minutes);

        return nextInstanceTime;
    }


    protected AlarmInfo(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.hour = in.readInt();
        this.minutes = in.readInt();
        this.daysOfWeek = Weekdays.fromBits(in.readInt());
        this.label = in.readString();
        this.ringtone = in.readParcelable(Uri.class.getClassLoader());
        this.enabled = in.readByte() != 0;
        this.vibrate = in.readByte() != 0;
    }

    public Uri getRingtone() {
        if (ringtone == null) {
            return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }
        return ringtone;
    }

    public void setRingtone(Uri ringtone) {
        if (ringtone == null) {
            this.ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }
        this.ringtone = ringtone;
    }

    public static final Creator<AlarmInfo> CREATOR = new Creator<AlarmInfo>() {
        @Override
        public AlarmInfo createFromParcel(Parcel in) {
            return new AlarmInfo(in);
        }

        @Override
        public AlarmInfo[] newArray(int size) {
            return new AlarmInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.hour);
        dest.writeInt(this.minutes);
        dest.writeInt(this.daysOfWeek.getBits());
        dest.writeString(this.label);
        dest.writeParcelable(this.ringtone, flags);
        dest.writeByte((byte) (this.enabled ? 1 : 0));
        dest.writeByte((byte) (this.vibrate ? 1 : 0));
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "id=" + id +
                ", name=\"" + name + '\"' +
                ", hour=" + hour +
                ", minutes=" + minutes +
                ", daysOfWeek=" + daysOfWeek +
                ", label='" + label + '\"' +
                ", ringtone=" + ringtone +
                ", enabled=" + enabled +
                ", vibrate=" + vibrate +
                '}';
    }

    public static ContentValues createContentValues(AlarmInfo alarmInfo) {
        ContentValues values = new ContentValues();
        values.put(NAME, alarmInfo.name == null? "": alarmInfo.name);
        values.put(ENABLED, alarmInfo.enabled);
        values.put(HOUR, alarmInfo.hour);
        values.put(MINUTES, alarmInfo.minutes);
        values.put(DAYS_OF_WEEK, alarmInfo.daysOfWeek.getBits());
        values.put(VIBRATE, alarmInfo.vibrate ? 1 : 0);
        values.put(LABEL, alarmInfo.label);
        if (alarmInfo.ringtone == null) {
            values.putNull(RINGTONE);
        } else {
            values.put(RINGTONE, alarmInfo.ringtone.toString());
        }
        return values;
    }

    public static long getId(Uri contentUri) {
        return ContentUris.parseId(contentUri);
    }


    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(NAME, name == null? "": name);
        values.put(ENABLED, enabled);
        values.put(HOUR, hour);
        values.put(MINUTES, minutes);
        values.put(DAYS_OF_WEEK, daysOfWeek.getBits());
        values.put(VIBRATE, vibrate ? 1 : 0);
        values.put(LABEL, label);
        if (ringtone == null) {
            values.putNull(RINGTONE);
        } else {
            values.put(RINGTONE, ringtone.toString());
        }
        return values;
    }
}
