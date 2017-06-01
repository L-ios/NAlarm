package com.lionseun.lib8.nalarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by lingyang on 6/1/17.
 */

public class AlarmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private String[] mTitles;

    public AlarmAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mTitles = mContext.getResources().getStringArray(R.array.demo_titles);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new AlarmInfoViewHolder(mLayoutInflater.inflate(R.layout.item_text, viewGroup, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
    }

    @Override
    public int getItemCount() {
        return mTitles == null ? 0 : mTitles.length;
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
