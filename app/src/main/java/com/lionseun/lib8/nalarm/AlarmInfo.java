package com.lionseun.lib8.nalarm;

import android.content.ContentValues;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lingyang on 6/1/17.
 */

public class AlarmInfo implements Parcelable, AlarmContract.AlarmsColumns {

    public long id;
    public String name;
    public int hour;
    public int minutes;
    public String label;
    private Uri ringtone;
    public Weekdays daysOfWeek;
    public boolean enabled;
    public boolean vibrate = true;

    private static final int ID_INDEX = 0;
    private static final int NAME_INDEX = 1;
    private static final int HOUR_INDEX = 2;
    private static final int MINUTES_INDEX = 3;
    private static final int LABEL_INDEX = 4;
    private static final int RINGTONE_INDEX = 5;
    private static final int DAYS_OF_WEEK_INDEX = 6;
    private static final int ENABLED_INDEX = 7;
    private static final int VIBRATE_INDEX = 8;

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
