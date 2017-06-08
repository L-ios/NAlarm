/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lionseun.lib8.nalarm;

import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.List;

/**
 * API for asynchronously mutating a single alarm.
 */
public final class AlarmHandler {

    private final Context mAppContext;
    private final View mSnackbarAnchor;

    // For undo
    private AlarmInfo mDeletedAlarm;

    public AlarmHandler(Context context,
            ViewGroup snackbarAnchor) {
        mAppContext = context.getApplicationContext();
        mSnackbarAnchor = snackbarAnchor;
    }

    /**
     * Adds a new alarm on the background.
     *
     * @param alarm The alarm to be added.
     */
    public void asyncAddAlarm(final AlarmInfo alarm) {
        final AsyncTask<Void, Void, AlarmInstance> updateTask =
                new AsyncTask<Void, Void, AlarmInstance>() {
                    @Override
                    protected AlarmInstance doInBackground(Void... parameters) {
                        if (alarm != null) {
//                            Events.sendAlarmEvent(R.string.action_create, R.string.label_deskclock);
                            ContentResolver cr = mAppContext.getContentResolver();
                            
                            // Add alarm to db
                            AlarmInfo newAlarm = AlarmInfo.addAlarm(cr, alarm);

                            
                            // Create and add instance to db
                            if (newAlarm.enabled) {
                                return setupAlarmInstance(newAlarm);
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(AlarmInstance instance) {
                        if (instance != null) {
                            AlarmUtils.popAlarmSetSnackbar(
                                    mSnackbarAnchor, instance.getAlarmTime().getTimeInMillis());
                        }
                    }
                };
        updateTask.execute();
    }

    /**
     * Modifies an alarm on the background, and optionally show a toast when done.
     *
     * @param alarmInfo       The alarm to be modified.
     * @param popToast    whether or not a toast should be displayed when done.
     * @param minorUpdate if true, don't affect any currently snoozed instances.
     */
    public void asyncUpdateAlarm(final AlarmInfo alarmInfo, final boolean popToast,
            final boolean minorUpdate) {
        final AsyncTask<Void, Void, AlarmInstance> updateTask =
                new AsyncTask<Void, Void, AlarmInstance>() {
                    @Override
                    protected AlarmInstance doInBackground(Void... parameters) {
                        ContentResolver cr = mAppContext.getContentResolver();

                        // Update alarm
                        AlarmInfo.updateAlarm(cr, alarmInfo);

                        if (minorUpdate) {
                            // just update the instance in the database and update notifications.
                            final List<AlarmInstance> instanceList =
                                    AlarmInstance.getInstancesByAlarmId(cr, alarmInfo.id);
                            for (AlarmInstance instance : instanceList) {
                                // Make a copy of the existing instance
                                final AlarmInstance newInstance = new AlarmInstance(instance);
                                // Copy over minor change data to the instance; we don't know
                                // exactly which minor field changed, so just copy them all.
                                newInstance.mVibrate = alarmInfo.vibrate;
                                newInstance.mRingtone = alarmInfo.getRingtone();
                                newInstance.mLabel = alarmInfo.label;
                                // Since we copied the mId of the old instance and the mId is used
                                // as the primary key in the AlarmInstance table, this will replace
                                // the existing instance.
                                AlarmInstance.updateInstance(cr, newInstance);
                                // Update the notification for this instance.
                                //AlarmNotifications.updateNotification(mAppContext, newInstance);
                            }
                            return null;
                        }
                        // Otherwise, this is a major update and we're going to re-create the alarm
                        AlarmStateManager.deleteAllInstances(mAppContext, alarmInfo.id);

                        return alarmInfo.enabled ? setupAlarmInstance(alarmInfo) : null;
                    }

                    @Override
                    protected void onPostExecute(AlarmInstance instance) {
                        if (popToast && instance != null) {
                            AlarmUtils.popAlarmSetSnackbar(
                                    mSnackbarAnchor, instance.getAlarmTime().getTimeInMillis());
                        }
                    }
                };
        updateTask.execute();
    }

    /**
     * Deletes an alarm on the background.
     *
     * @param alarm The alarm to be deleted.
     */
    public void asyncDeleteAlarm(final AlarmInfo alarm) {
        final AsyncTask<Void, Void, Boolean> deleteTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... parameters) {
                // Activity may be closed at this point , make sure data is still valid
                if (alarm == null) {
                    // Nothing to do here, just return.
                    return false;
                }
                AlarmStateManager.deleteAllInstances(mAppContext, alarm.id);
                return AlarmInfo.deleteAlarm(mAppContext.getContentResolver(), alarm.id);
            }

            @Override
            protected void onPostExecute(Boolean deleted) {
                if (deleted) {
                    mDeletedAlarm = alarm;
                }
            }
        };
        deleteTask.execute();
    }

    /**
     * Show a toast when an alarm is predismissed.
     *
     * @param instance Instance being predismissed.
     */
    public void showPredismissToast(AlarmInstance instance) {
        final String time = DateFormat.getTimeFormat(mAppContext).format(
                instance.getAlarmTime().getTime());
        final String text = mAppContext.getString(R.string.alarm_is_dismissed, time);
        Snackbar.make(mSnackbarAnchor, text, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Hides any undo toast.
     */
    public void hideUndoBar() {
        mDeletedAlarm = null;
    }

    private AlarmInstance setupAlarmInstance(AlarmInfo alarmInfo) {
        final ContentResolver cr = mAppContext.getContentResolver();
        AlarmInstance newInstance = alarmInfo.createInstanceAfter(Calendar.getInstance());
        newInstance = AlarmInstance.addInstance(cr, newInstance);
        // Register instance to state manager
        AlarmStateManager.registerInstance(mAppContext, newInstance, true);
        return newInstance;
    }
}
