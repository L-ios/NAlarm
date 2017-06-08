package com.lionseun.lib8.nalarm;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AlarmAdapter.AlarmHandle{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private FirebaseAnalytics mFirebaseAnalytics;
    
    private RecyclerView mAlarmRecyclerView;
    private FloatingActionButton mAlarmAdd;
    private AlarmAdapter mAlarmAdapter;
    private AlarmHandler mAlarmHandler;
    public static final int RESULT_UPDATA_ALARM = 2;
    public static final int RESULT_NEW_ALARM = 3;
    public static final String EXTRA_ALARM = "alarm.info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAlarmAdd = (FloatingActionButton) findViewById(R.id.alarm_add);
        mAlarmAdd.setOnClickListener((v) -> {
            editAlarmInfo(null);
        });
        
        mAlarmRecyclerView = (RecyclerView) findViewById(R.id.alarms_recycler_view);
        mAlarmRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAlarmAdapter = new AlarmAdapter(this);
        mAlarmRecyclerView.setAdapter(mAlarmAdapter);
        getLoaderManager().initLoader(0, null, this);
        
        
        mAlarmHandler = new AlarmHandler(this,mAlarmRecyclerView );
        FBLog("onCreate");
    }
    
    private void editAlarmInfo(AlarmInfo alarmInfo) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClass(getApplicationContext(), AlarmInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_ALARM, alarmInfo);
        startActivityForResult(intent, 0,bundle);
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.txt.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_settings: {
                Toast.makeText(this, "完成设置", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
                return true;
            }
            case R.id.action_about: {
                Toast.makeText(this, "完善About", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
            }
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return AlarmLoader.getAlarmsCursorLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // 加载完成后的操作
        List<AlarmInfo> alarmInfoList = new ArrayList<>(data.getCount());
        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            AlarmInfo alarmInfo = new AlarmInfo(data);
            alarmInfoList.add(alarmInfo);
        }
        mAlarmAdapter.setItemInfos(alarmInfoList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // nothing to do for temper
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
            case RESULT_UPDATA_ALARM: {
                AlarmInfo alarmInfo = data.getParcelableExtra(EXTRA_ALARM);
                mAlarmHandler.asyncUpdateAlarm(alarmInfo, false, false);
                break;
            }
            case RESULT_NEW_ALARM: {
                AlarmInfo alarmInfo = data.getParcelableExtra(EXTRA_ALARM);
                mAlarmHandler.asyncAddAlarm(alarmInfo);
                break;
            }
            case RESULT_CANCELED: {
                break;
            }
            case RESULT_FIRST_USER: {
                break;
            }
            default: {
                break;
            }
        }
    }

    AlarmHandler getAlarmHandler() {
        return mAlarmHandler;
    }
    
    @Override
    public void deleteAlarm(AlarmInfo alarmInfo) {
        mAlarmHandler.asyncDeleteAlarm(alarmInfo);
    }

    @Override
    public void enableAlarm(AlarmInfo alarmInfo) {
        mAlarmHandler.asyncUpdateAlarm(alarmInfo, false, false);
    }
    
    private void FBLog(String msg) {

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, BuildConfig.APPLICATION_ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, LOG_TAG);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "log");
        bundle.putString(FirebaseAnalytics.Param.CONTENT, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
}
