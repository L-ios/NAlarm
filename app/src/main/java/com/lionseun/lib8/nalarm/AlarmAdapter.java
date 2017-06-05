package com.lionseun.lib8.nalarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingyang on 6/1/17.
 */

public class AlarmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<AlarmInfo> mAlarmInfoList;

    public AlarmAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mAlarmInfoList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new AlarmInfoViewHolder(mLayoutInflater.inflate(R.layout.alarm_item, viewGroup, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        // 用于数据和单个view的绑ListView中Adapter的getView
        // TODO: 6/5/17 改善view的监听 
        ((AlarmInfoViewHolder)viewHolder).updateAlarmInfo(mAlarmInfoList.get(position));
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

    public void startAlarmInfoActivity() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClass(mContext, AlarmInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("alarminfo", null);
        mContext.startActivity(intent, bundle);
    }


    public class AlarmInfoViewHolder extends RecyclerView.ViewHolder {
        ImageView mAlarmIcon;
        TextView mAlarmTime;
        TextView mAlarmNote;
        Switch mAlarmSwitch;
        TextView mAlarmRptInfo;
        TextView mAlarmRing;


        AlarmInfoViewHolder(View view) {
            super(view);
            mAlarmIcon = (ImageView) view.findViewById(R.id.alarm_icon);
            mAlarmTime = (TextView) view.findViewById(R.id.alarm_time);
            mAlarmNote = (TextView) view.findViewById(R.id.alarm_note);
            mAlarmSwitch = (Switch) view.findViewById(R.id.alarm_switch);
            mAlarmRptInfo = (TextView) view.findViewById(R.id.alarm_rpt_info);
            mAlarmRing = (TextView) view.findViewById(R.id.alarm_ring);
            view.setOnClickListener((v) -> {
                changeStatus();
            });

            view.setOnLongClickListener((v) -> {
                AlarmAdapter.this.startAlarmInfoActivity();
                return true;
            });
        }
        
        private void updateAlarmInfo(AlarmInfo info) {
            if (info == null) {
                return;
            }
            // TODO: 6/3/17 build picture 
            //mAlarmIcon.setImageURI(info.);
            // TODO: 6/3/17 update time show 
            mAlarmTime.setText("" + info.hour + " : " + info.minutes);
            mAlarmNote.setText(info.label);
            mAlarmSwitch.setChecked(info.enabled);
            // TODO: 6/3/17 repeat text 
            mAlarmRptInfo.setText("TODO");
            // TODO: 6/3/17 ring text 
            mAlarmRing.setText("TODO");
        }

        /**
         * 更新每个闹铃的状态
         */
        private void changeStatus() {
            if (mAlarmSwitch != null) {
                if (mAlarmSwitch.isChecked()) {
                    mAlarmSwitch.setChecked(false);
                } else {
                    mAlarmSwitch.setChecked(true);

                }
            }
        }
    }
}
