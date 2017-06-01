package com.lionseun.lib8.nalarm;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lingyang on 6/1/17.
 */

public class AlarmInfo implements Parcelable {

    protected AlarmInfo(Parcel in) {
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
    }
}
