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
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mAlarmRecyclerView;
    private FloatingActionButton mAlarmAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAlarmAdd = (FloatingActionButton) findViewById(R.id.alarm_add);
        mAlarmAdd.setOnClickListener((v) -> {
            editAlarmInfo(null);
        });
        
        mAlarmRecyclerView = (RecyclerView) findViewById(R.id.alarms_recycler_view);
        mAlarmRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAlarmRecyclerView.setAdapter(new AlarmAdapter(this));
    }
    
    private void editAlarmInfo(AlarmInfo alarmInfo) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClass(getApplicationContext(), AlarmInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("alarminfo", alarmInfo);
        startActivity(intent, bundle);
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
        // TODO: 6/3/17 onCreateLoaser 
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // TODO: 6/3/17 onLoadFinished 
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // TODO: 6/3/17 onLoaderReset 

    }
}
