package com.lionseun.lib8.nalarm;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmInfoActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    private AlarmInfo mAlarmInfo;
    private boolean is_new = true;
    private TextView mAlarmName;
    private TextView mAlarmTime;
    private EditText mLabel;
    private CheckBox mEnable;
    private TextView mAlarmSound;
    private TextView mRepeat;
    private static final int RESULT_RINGTONE = 0x2;

    public static String getName(Context context, Calendar calendar) {
        String[] names = context.getResources().getStringArray(R.array.alarm_name_list);
        int hour = 0;
        if (calendar != null) {
            hour = calendar.get(Calendar.HOUR_OF_DAY);
        }
        if (names.length > hour) {
            return names[hour];
        }else {
            return names[hour % names.length];
        }
    }
    
    
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
        
        Intent intent = getIntent();
        mAlarmInfo = intent.getParcelableExtra("alarminfo");
        if (mAlarmInfo == null) {
            is_new = false;
            final Calendar now = Calendar.getInstance();
            mAlarmInfo = new AlarmInfo(getName(this, now), now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
            mAlarmInfo.setRingtone(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
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
            TimePickerDialog timePickDialog = new TimePickerDialog(AlarmInfoActivity.this, this, hourOfDay, minute, true);
            timePickDialog.setCancelable(false);
            timePickDialog.show();
        });
        mLabel = (EditText) findViewById(R.id.alarm_label);
        mLabel.clearFocus();
        mLabel.setOnClickListener(v -> {
            mLabel.requestFocus();
        });
        mRepeat = (TextView) findViewById(R.id.alarm_rpt);
        mAlarmSound = (TextView) findViewById(R.id.alarm_sound);
        mAlarmSound.setOnClickListener(v -> {
            Intent ringtoneIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
//            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, mAlarmInfo.getRingtone());
            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
            // TODO: 17-6-6 picked ringtone 
            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, mAlarmInfo.getRingtone());
            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
//            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, mAlarmInfo.getRingtone());
            ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置闹铃");
            startActivityForResult(ringtoneIntent, RESULT_RINGTONE);
        });

        mEnable = (CheckBox) findViewById(R.id.is_vibrate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_OK:
            case RESULT_RINGTONE: {
                if (data == null) {
                    return;
                }
                mAlarmInfo.setRingtone(data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI));
                updateSound();
            }
            case RESULT_CANCELED: {
                return;
            }
            case RESULT_FIRST_USER: {
                return;
            }
            default: {
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateActivity();
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
            if (!is_new) {
                mAlarmName.clearFocus();
                mLabel.clearFocus();
            }
            mAlarmName.setText(mAlarmInfo.name);
            updateTime(mAlarmInfo.hour, mAlarmInfo.minutes);
            mLabel.setText(mAlarmInfo.label);
            updateSound(mAlarmInfo.getRingtone());
            updateRepeat(mAlarmInfo.daysOfWeek);
            mEnable.setChecked(mAlarmInfo.enabled);
        }
    }

    private void updateTime(int hourOfDay, int minute) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        mAlarmTime.setText(dateFormat.format(calendar.getTime()));
    }

    private void updateSound(Uri soundName) {
        mAlarmInfo.setRingtone(soundName);
        updateSound();
    }

    private void updateSound() {
        String ringtoneName;
        Uri ringtoneUri;
        if(mAlarmInfo.getRingtone() == null) {
            ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        } else {
            ringtoneUri = mAlarmInfo.getRingtone();
        }
        ringtoneName = RingtoneManager.getRingtone(this, ringtoneUri).getTitle(this);
        mAlarmSound.setText(ringtoneName);
    }

    private void updateRepeat(Weekdays daysOfWeek) {
        if (daysOfWeek == null) {
            return;
        }
        mRepeat.setText(daysOfWeek.toString(this, Weekdays.Order.MON_TO_SUN));
    }

    public void saveAlarmInfo() {
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
        // TODO: 6/7/17 插入有问题 
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
