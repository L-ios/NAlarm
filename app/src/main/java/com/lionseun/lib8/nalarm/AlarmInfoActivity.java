package com.lionseun.lib8.nalarm;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmInfoActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    private AlarmInfo mAlarmInfo;

    private TextView mAlarmName;
    private TextView mAlarmTime;
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

        final Calendar now = Calendar.getInstance();
        // TODO: 17-6-5 need some list to alarm name.
        mAlarmInfo = new AlarmInfo(null, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));

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
            TimePickerDialog timePickDialog = new TimePickerDialog(AlarmInfoActivity.this, this, hourOfDay, minute, true);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(android.R.string.dialog_alert_title);
        builder.setMessage(R.string.alarm_quit_message);
        builder.setNegativeButton(android.R.string.ok, (dialog, which) -> {
            finish();
        });
        builder.setPositiveButton(android.R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(android.R.string.dialog_alert_title);
                builder.setMessage(R.string.alarm_save_message);
                builder.setNegativeButton(android.R.string.ok, (dialog, which) -> {
                    saveAlarmInfo();
                    finish();
                });
                builder.setPositiveButton(android.R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.create().show();
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
            mAlarmName.setText(mAlarmInfo.name);
            updateTime(mAlarmInfo.hour, mAlarmInfo.minutes);
            mLabel.setText(mAlarmInfo.label);
            updateSound(mAlarmInfo.alert);
            updateRepeat(mAlarmInfo.daysOfWeek);
            mEnable.setChecked(mAlarmInfo.enabled);
        }
    }

    private void updateTime(int hourOfDay, int minute) {
        mAlarmTime.setText("" + hourOfDay + " : " + minute);
    }

    private void updateSound(Uri soundName) {
        // TODO: 17-6-5 alarm sound
        mAlarmSound.setText(soundName.toString());
    }

    private void updateRepeat(Weekdays daysOfWeek) {
        // TODO: 17-6-5 repeat
        mRepeat.setText(daysOfWeek.toString());
    }

    public void saveAlarmInfo() {
        if (mAlarmInfo == null) {
            final Calendar now = Calendar.getInstance();
            // TODO: 17-6-5 need some list to alarm name.
            mAlarmInfo = new AlarmInfo(null, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
        }
        // TODO: 6/5/17 get all info
        asyncAddAlarmInfo(mAlarmInfo);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (mAlarmInfo != null) {
            mAlarmInfo.hour = hourOfDay;
            mAlarmInfo.minutes = minute;
        }
        updateActivity();
    }

    public void asyncAddAlarmInfo(final AlarmInfo alarmInfo) {
        // TODO: 17-6-5 temper for here
        final AsyncTask<Void, Void, Object> updateTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                ContentResolver cr = getApplication().getContentResolver();
                ContentValues values = alarmInfo.toContentValues();
                Uri uri = cr.insert(AlarmContract.AlarmsColumns.CONTENT_URI, values);
                ContentUris.parseId(uri);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                Toast.makeText(getApplicationContext(), "todo 添加alarmInfo成功", Toast.LENGTH_LONG).show();
            }
        };
        updateTask.execute();
    }


}
