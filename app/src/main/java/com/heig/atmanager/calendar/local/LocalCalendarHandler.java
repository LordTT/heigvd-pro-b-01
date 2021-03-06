package com.heig.atmanager.calendar.local;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;

import androidx.core.app.ActivityCompat;

import com.heig.atmanager.BuildConfig;
import com.heig.atmanager.MainActivity;
import com.heig.atmanager.R;
import com.heig.atmanager.tasks.Task;

import java.util.TimeZone;


public class LocalCalendarHandler {

    public static final String TAG = "LocalCalendarHandler";

    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[]{
            Calendars._ID,                           // 0
            Calendars.ACCOUNT_NAME,                  // 1
            Calendars.CALENDAR_DISPLAY_NAME,         // 2
            Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
    public static final int MY_CAL_CREATE = 50;
    public static final int MY_CAL_CHECK = 52;
    public static final int MY_CAL_ADD_TASK = 53;

    //private Activity mActivity;
    private long calendarId;
    private Task task;


    private static class Instance {
        static final LocalCalendarHandler instance = new LocalCalendarHandler();
    }

    public LocalCalendarHandler() {
    }

    public static LocalCalendarHandler getInstance() {
        return Instance.instance;
    }


    /**
     * Create a new Calendar for our app
     * @param activity
     */
    public void createCalendar(Activity activity) {
        ContentValues values = new ContentValues();
        values.put(Calendars.ACCOUNT_NAME, MainActivity.getUser().getEmail());
        values.put(Calendars.ACCOUNT_TYPE, BuildConfig.APPLICATION_ID);
        values.put(Calendars.NAME, activity.getString(R.string.app_name));
        values.put(Calendars.CALENDAR_DISPLAY_NAME, activity.getString(R.string.app_name));
        values.put(Calendars.CALENDAR_COLOR, R.color.colorAccent);
        values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        values.put(Calendars.OWNER_ACCOUNT, MainActivity.getUser().getEmail());
        values.put(Calendars.SYNC_EVENTS, true);
        values.put(Calendars.VISIBLE, true);

        Uri uri = Calendars.CONTENT_URI;

        uri = uri.buildUpon()
                // Sync with server, didn't work...
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, MainActivity.getUser().getEmail())
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, BuildConfig.APPLICATION_ID)
                .build();

        // Need permission
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR}, MY_CAL_CHECK);
        }else {
            uri = activity.getContentResolver().insert(uri, values);
            calendarId = ContentUris.parseId(uri);
        }

    }

    /**
     * Read the user local db to find if our calendar already exist
     * @param activity
     * @return
     */
    public long checkIfCalendarExist(Activity activity) {
        Cursor cur = null;
        ContentResolver cr = activity.getContentResolver();

        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{MainActivity.getUser().getEmail(), BuildConfig.APPLICATION_ID,
                MainActivity.getUser().getEmail()};

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR}, MY_CAL_CHECK);

        }else {
            cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
            if (cur != null && cur.moveToFirst()) {
                return cur.getLong(PROJECTION_ID_INDEX);
            }
        }

        return -1;
    }

    public void setTask(Task task){
        this.task = task;
    }

    /**
     * Add in the calendar
     * @param activity
     */
    public void addTask( Activity activity) {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_CAL_ADD_TASK);

        }else {
            calendarId = checkIfCalendarExist(activity);

            if (calendarId == -1) {
                createCalendar(activity);
            }

            ContentResolver cr = activity.getContentResolver();
            ContentValues values = new ContentValues();

            values.put(Events.TITLE, task.getTitle());

            values.put(Events.DTSTART, task.getDueDate().getTime());
            values.put(Events.DTEND, task.getDueDate().getTime());
            values.put(Events.CALENDAR_ID, calendarId);
            values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());
            Uri uri = cr.insert(Events.CONTENT_URI, values);
        }
    }
}
