package com.lionseun.lib8.nalarm;

import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lingyang on 6/1/17.
 */

public class AlarmInfo implements Parcelable, AlarmContract.AlarmsColumns {

    public long id;
    public String name;
    public boolean enabled;
    public int hour;
    public int minutes;
    public Weekdays daysOfWeek;
    public boolean vibrate;
    public String label;
    public Uri alert;

    AlarmInfo(String name, int hour, int minutes) {
        this.name = name;
        this.hour = hour;
        this.minutes = minutes;
    }
    
    AlarmInfo(Cursor c) {
        // TODO: 6/5/17 build alarminfo by Cursor 
    }
    
    protected AlarmInfo(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.enabled = in.readByte() != 0;
        this.hour = in.readInt();
        this.minutes = in.readInt();
        this.vibrate = in.readByte() != 0;
        this.label = in.readString();
        this.alert = in.readParcelable(Uri.class.getClassLoader());
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
        dest.writeByte((byte) (this.enabled ? 1 : 0));
        dest.writeInt(this.hour);
        dest.writeInt(this.minutes);
        dest.writeByte((byte) (this.vibrate ? 1 : 0));
        dest.writeString(this.label);
        dest.writeParcelable(this.alert, flags);
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "id=" + id +
                ", name=\"" + name + '\"' +
                ", enabled=" + enabled +
                ", hour=" + hour +
                ", minutes=" + minutes +
                ", daysOfWeek=" + daysOfWeek +
                ", vibrate=" + vibrate +
                ", label='" + label + '\"' +
                ", alert=" + alert +
                '}';
    }
}
