package com.lionseun.lib8.nalarm;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import static com.lionseun.lib8.nalarm.AlarmDatabaseHelper.ALARMS_TABLE_NAME;

/**
 * Created by lingyang on 6/5/17.
 */
public class AlarmProvider extends ContentProvider {
    
    private AlarmDatabaseHelper mOpenHelper;


    private static final int ALARMS = 1;
    private static final int ALARMS_ID = 2;
    
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AlarmContract.AUTHORITY, "alarms", ALARMS);
        sURIMatcher.addURI(AlarmContract.AUTHORITY, "alarms/#", ALARMS_ID);
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new AlarmDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        
        switch (sURIMatcher.match(uri)) {
            case ALARMS: {
                qb.setTables(ALARMS_TABLE_NAME);
                break;
            }
            case ALARMS_ID: {
                qb.setTables(ALARMS_TABLE_NAME);
                qb.appendWhere(AlarmContract.AlarmsColumns._ID + "=");
                qb.appendWhere(uri.getLastPathSegment());
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        Cursor ret = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        if (ret == null) {
            LogUtils.e("Alarms.query: failed");
        } else {
            ret.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return ret;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sURIMatcher.match(uri);
        switch (match) {
            case ALARMS:
                return "vnd.android.cursor.dir/alarms";
            case ALARMS_ID:
                return "vnd.android.cursor.item/alarms";
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long rowId;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sURIMatcher.match(uri)) {
            case ALARMS: {
                rowId = mOpenHelper.fixAlarmInsert(values);
                break;
            }
            default:
                throw new IllegalArgumentException("Cannot insert from URI: " + uri);
        }
        Uri uriResult = ContentUris.withAppendedId(uri, rowId);
        notifyChange(getContext().getContentResolver(), uriResult);
        return uriResult;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String where, @Nullable String[] whereArgs) {
        int count;
        String primaryKey;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        
        switch (sURIMatcher.match(uri)) {
            case ALARMS:
                count = db.delete(ALARMS_TABLE_NAME, where, whereArgs);
                break;
            case ALARMS_ID:
                primaryKey = uri.getLastPathSegment();
                if (TextUtils.isEmpty(where)) {
                    where = AlarmContract.AlarmsColumns._ID + "=" + primaryKey;
                } else {
                    where = AlarmContract.AlarmsColumns._ID + "=" + primaryKey + " AND (" + where + ")";
                }
                count = db.delete(ALARMS_TABLE_NAME, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete from URI: " + uri);
        }

        notifyChange(getContext().getContentResolver(), uri);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String where, String[] whereArgs) {
        int count;
        String alarmId;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sURIMatcher.match(uri)) {
            case ALARMS_ID:
                alarmId = uri.getLastPathSegment();
                count = db.update(ALARMS_TABLE_NAME, values,
                        AlarmContract.AlarmsColumns._ID + "=" + alarmId,
                        null);
                break;
            default: {
                throw new UnsupportedOperationException("Cannot update URI: " + uri);
            }
        }
        LogUtils.v("*** notifyChange() id: " + alarmId + " url " + uri);
        notifyChange(getContext().getContentResolver(), uri);
        return count;
    }

    /**
     * Notify affected URIs of changes.
     */
    private void notifyChange(ContentResolver resolver, Uri uri) {
        resolver.notifyChange(uri, null);

        final int match = sURIMatcher.match(uri);
        // Also notify the joined table of changes to instances or alarms.
        if (match == ALARMS || match == ALARMS_ID) {
            resolver.notifyChange(AlarmContract.AlarmsColumns.CONTENT_URI, null);
        }
    }
}
