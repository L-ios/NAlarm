package com.lionseun.lib8.nalarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmInfoActivity extends AppCompatActivity {

    private AlarmInfo mAlarmInfo;
    private boolean isSave = false;
    
    private TextView mAlarmName;
    private TextView mAlarmTime;
    private TextView mHour;
    private TextView mMinutes;
    private EditText mLabel;
    private CheckBox mEnable;
    private TextView mAlarmSound;
    private TextView mRepeat;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        
        mAlarmName = (TextView) findViewById(R.id.alarm_name);
        mAlarmTime = (TextView) findViewById(R.id.alarm_time);
        mAlarmTime.setOnClickListener((v) -> {
            int hourOfDay = 0;
            int minute = 0;
            if (mAlarmInfo != null) {
                hourOfDay = mAlarmInfo.hour;
                minute = mAlarmInfo.minutes;
            } else {
                
            }
            TimePickerDialog timePickDialog = new TimePickerDialog(AlarmInfoActivity.this, null, hourOfDay, minute, true);
            timePickDialog.setButton(Dialog.BUTTON_POSITIVE, "null, todo", (dialog, which) -> {
                if (which == Dialog.BUTTON_POSITIVE) {
                    // TODO: 6/5/17 update time , hour and minutes 
                    Toast.makeText(AlarmInfoActivity.this, "获取时间", Toast.LENGTH_LONG).show();
                    
                    updateActivity();
                }
            });
            timePickDialog.setCancelable(false);
            timePickDialog.show();
        });
        mLabel = (EditText) findViewById(R.id.alarm_label);
        mRepeat = (TextView) findViewById(R.id.alarm_rpt);
        mAlarmSound = (TextView) findViewById(R.id.alarm_sound);
        mEnable = (CheckBox) findViewById(R.id.is_vibrate);
        
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (isSave) {
            finish();
        } else {
            // TODO feature: 6/1/17 阻止退出，提示是否保存铃声 
            Toast.makeText(this, "完成未保存", Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("todo title");
            builder.setMessage("todo message builder");
            builder.setNegativeButton("todo message buttong", (dialog, which) -> {
                finish();
            });
            builder.setPositiveButton("todo positive buuton", (dialog, which) -> {
                dialog.dismiss();
            });
            builder.create().show();
        }
        return super.onSupportNavigateUp();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.txt.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.save_alarm: {
                Toast.makeText(this, "完善 Save Alarm", Toast.LENGTH_LONG).show();
                // TODO: 6/5/17 insert data to database 
                isSave = true;
                finish();
                return true;
            }
            case R.id.abandon_alarm: {
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
    
    public void updateActivity() {
        if (mAlarmInfo != null) {
            mEnable.setChecked(mAlarmInfo.enabled);
        }
        
    }
    
    public void saveAlarmInfo() {
        if (mAlarmInfo == null) {
            String time = (String) mAlarmTime.getText();
            mAlarmInfo = new AlarmInfo((String)mAlarmName.getText(), 0, 0);
        }
        // TODO: 6/5/17 get all info 
        
    }
}
