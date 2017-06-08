package com.lionseun.lib8.nalarm;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by lingyang on 6/1/17.
 */

public class AlarmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<AlarmInfo> mAlarmInfoList;
    private AlarmHandle mAlarmHandle;

    public AlarmAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mAlarmInfoList = new ArrayList<>();
        if (mContext instanceof AlarmHandle) {
            mAlarmHandle = (AlarmHandle) mContext;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new AlarmInfoViewHolder(mLayoutInflater.inflate(R.layout.alarm_item, viewGroup, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        // 用于数据和单个view的绑ListView中Adapter的getView
        if (viewHolder instanceof AlarmInfoViewHolder) {
            AlarmInfoViewHolder alarmInfoViewHolder = (AlarmInfoViewHolder) viewHolder;
            alarmInfoViewHolder.updateAlarmInfo(mAlarmInfoList.get(position));
            alarmInfoViewHolder.setClickListener();
        }
    }

    public AlarmAdapter setItemInfos(List<AlarmInfo> alarmInfoList) {
        List<AlarmInfo> oldAlarmInfoList = mAlarmInfoList;
        if (oldAlarmInfoList != alarmInfoList) {
            mAlarmInfoList = alarmInfoList;
            notifyDataSetChanged();
        }
        return this;
    }
    
    
    @Override
    public int getItemCount() {
        return mAlarmInfoList == null ? 0 : mAlarmInfoList.size();
    }

    public void startAlarmInfoActivity(AlarmInfo alarmInfo) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClass(mContext, AlarmInfoActivity.class);
        intent.putExtra(MainActivity.EXTRA_ALARM, alarmInfo);
        mContext.startActivity(intent);
    }


    public class AlarmInfoViewHolder extends RecyclerView.ViewHolder {
        View parent;
        AlarmInfo mAlarmInfo;
        TextView mAlarmIcon;
        TextView mAlarmTime;
        TextView mAlarmLabel;
        Switch mAlarmSwitch;
        TextView mAlarmRptInfo;
        TextView mAlarmRing;
        ImageView deleteView;


        AlarmInfoViewHolder(View view) {
            super(view);
            parent = view;
            mAlarmIcon = (TextView) view.findViewById(R.id.alarm_icon);
            mAlarmTime = (TextView) view.findViewById(R.id.alarm_time);
            mAlarmLabel = (TextView) view.findViewById(R.id.alarm_note);
            mAlarmSwitch = (Switch) view.findViewById(R.id.alarm_switch);
            mAlarmRptInfo = (TextView) view.findViewById(R.id.alarm_rpt_info);
            mAlarmRing = (TextView) view.findViewById(R.id.alarm_ring);
            deleteView = (ImageView) view.findViewById(R.id.delete);
        }
        
        void updateAlarmInfo(AlarmInfo info) {
            if (info == null) {
                return;
            }
            mAlarmInfo = info;
            updateIcon(info.name);
            updateTime(info.hour, info.minutes);
            updateLabel(info.label);
            mAlarmSwitch.setChecked(info.enabled);
            updateRptInfo(info.daysOfWeek);
            updateRingtone(info.getRingtone());
        }
        
        void setClickListener() {
            parent.setOnClickListener((v) -> {
                AlarmAdapter.this.startAlarmInfoActivity(this.mAlarmInfo);
            });

            /*parent.setOnLongClickListener((v) -> {
                // TODO: 6/6/17 进入编辑模式，进行删除
                // feature: 6/6/17 长按如同电脑右键
                Toast.makeText(mContext, "todo how to delete item", Toast.LENGTH_LONG).show();
                return true;
            });*/

            mAlarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                mAlarmInfo.enabled = isChecked;
                if (mContext instanceof AlarmHandle) {
                    ((AlarmHandle) mContext).enableAlarm(mAlarmInfo);
                }
            });
            
            deleteView.setClickable(true);
            deleteView.setOnClickListener(v -> {
                if (mContext instanceof AlarmHandle) {
                    ((AlarmHandle) mContext).deleteAlarm(mAlarmInfo);
                }
            });
        }

        private void updateLabel(String label) {
            if (TextUtils.isEmpty(label)) {
                mAlarmLabel.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
                mAlarmLabel.setText(R.string.no_label);
            } else {
                mAlarmLabel.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                mAlarmLabel.setText(label);
            }
        }
        
        
        private void updateIcon(String alarmName) {
            if (TextUtils.isEmpty(alarmName)) {
                mAlarmIcon.setText("");
            } else {
                mAlarmIcon.setText(alarmName.substring(0,1));
            }
        }
        
        private void updateTime(int hourOfDay, int minute) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            mAlarmTime.setText(dateFormat.format(calendar.getTime()));
        }
        
        private void updateRptInfo(Weekdays weekdays) {
            String rptStr = getRpt(weekdays);
            mAlarmRptInfo.setText(rptStr);
        }
        
        private void updateRingtone(Uri ringtone) {
            String ringName = getRingName(ringtone);
            mAlarmRing.setText(ringName);
        }
        
        private String getRpt(Weekdays weekdays) {
            return weekdays.toString(mContext,Weekdays.Order.MON_TO_SUN);
        }
        
        private String getRingName(Uri ringtoneUri) {
            String ringtoneName = RingtoneManager.getRingtone(mContext, ringtoneUri).getTitle(mContext);

            return ringtoneName;
        }
        
    }
    
    interface AlarmHandle {
        void deleteAlarm(AlarmInfo alarmInfo);
        void enableAlarm(AlarmInfo alarmInfo);
    }
    
}
