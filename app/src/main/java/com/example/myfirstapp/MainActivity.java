package com.example.myfirstapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Time;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    @TargetApi(Build.VERSION_CODES.M)
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.Message";
    public static final String[] EVENT_PROJECTION = new String[]{
            Calendars._ID,                           // 0
            Calendars.ACCOUNT_NAME,                  // 1
            Calendars.CALENDAR_DISPLAY_NAME,         // 2
            Calendars.OWNER_ACCOUNT                  // 3
    };
    String[] reminder = { "<Select Reminder Type>", "URGENT", "NORMAL", "MINIMUM" };
    int selected_reminder = 0;
    int[] reminder_times = {0,0,0,0,0};


    // The indices for the projection array above.
    private static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText start_time = findViewById(R.id.starttime_field);
        EditText end_time = findViewById(R.id.endtime_field);
        EditText start_date = findViewById(R.id.startdate_field);
        EditText end_date = findViewById(R.id.enddate_field);
        Date a = Calendar.getInstance().getTime();
        SimpleDateFormat time_formatter = new SimpleDateFormat("hh:mm");
        SimpleDateFormat date_formatter = new SimpleDateFormat("dd/MM/YY");
        String dateString = time_formatter.format(a);
        start_time.setText(dateString);
        String hour = dateString.substring(0,2);
        if (hour.substring(0,1).equals("0") && !(hour.substring(1,2).equals("9")))
            hour = "0" + Integer.toString(Integer.parseInt(hour)+1);
        else
            hour = Integer.toString(Integer.parseInt(hour)+1);
        dateString = hour + dateString.substring(2);
        end_time.setText(dateString);
        dateString = date_formatter.format(a);
        start_date.setText(dateString);
        end_date.setText(dateString);

        Spinner spin = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, reminder);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);
    }
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        selected_reminder = position;
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO - Custom Code
    }

    public void clear(View view) {
        EditText name = findViewById(R.id.name_field);
        EditText desc = findViewById(R.id.description_field);
        EditText start_time = findViewById(R.id.starttime_field);
        EditText end_time = findViewById(R.id.endtime_field);
        name.setText("");
        desc.setText("");
        start_time.setText("00:00");
        end_time.setText("00:00");
    }

    public void testCalendar(View view) {
        long calID = 1; //Calendar ID
        Cursor cur = null;
        //ContentResolver cr = getContentResolver();
        //Uri uri = Calendars.CONTENT_URI;
        EditText name = findViewById(R.id.name_field);
        EditText description = findViewById(R.id.description_field);
        EditText start_time = findViewById(R.id.starttime_field);
        EditText end_time = findViewById(R.id.endtime_field);
        EditText start_date = findViewById(R.id.startdate_field);
        EditText end_date = findViewById(R.id.enddate_field);


        if (checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CALENDAR)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                requestPermissions(
                        new String[]{Manifest.permission.READ_CALENDAR}, MY_PERMISSIONS_REQUEST_READ_CALENDAR);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }


        //Extract string from textfield
        String name_s = name.getText().toString();
        String description_s = description.getText().toString();
        String starttime_s = start_time.getText().toString();
        String endtime_s = end_time.getText().toString();
        String startdate_s = start_date.getText().toString();
        String enddate_s = end_date.getText().toString();

        //Extract individual time units from date and time
        int start_day = Integer.parseInt(startdate_s.substring(0,2));
        int start_month = Integer.parseInt(startdate_s.substring(3,5));
        int start_year = Integer.parseInt(startdate_s.substring(startdate_s.length()-2));
        int end_day = Integer.parseInt(enddate_s.substring(0,2));
        int end_month = Integer.parseInt(enddate_s.substring(3,5));
        int end_year = Integer.parseInt(enddate_s.substring(enddate_s.length()-2));
        int start_hour = Integer.parseInt(starttime_s.substring(0,2));
        int start_minute = Integer.parseInt(starttime_s.substring(3,5));
        int end_hour = Integer.parseInt(endtime_s.substring(0,2));
        int end_minute = Integer.parseInt(endtime_s.substring(3,5));

        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();

        //Sample log message
        //Log.i("MyActivity", date);
        beginTime.set(start_year + 2000, start_month - 1, start_day, start_hour, start_minute);
        startMillis = beginTime.getTimeInMillis();
        endTime.set(end_year + 2000, end_month - 1, end_day, end_hour, end_minute);
        endMillis = endTime.getTimeInMillis();
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, name_s);
        values.put(CalendarContract.Events.DESCRIPTION, description_s);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        //Convert timeinmillis to normal date format
        //SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        //String dateString = formatter.format(new Date(startMillis));
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());

        values = new ContentValues();
        if (selected_reminder == 0){//Default
            reminder_times[0] = 5;
        }
        else if (selected_reminder == 1){//Urgent
            reminder_times[0] = 10080;
            reminder_times[1] = 2880;
            reminder_times[2] = 1440;
            reminder_times[3] = 240;
            reminder_times[4] = 60;

        }else if (selected_reminder == 2){//Normal
            reminder_times[0] = 60;
            reminder_times[1] = 30;
            reminder_times[2] = 15;

        }else{//Minimal
            reminder_times[0] = 60;
            reminder_times[1] = 30;
        }
        for (int times: reminder_times){
            if (times == 0) continue;
            values.put(CalendarContract.Reminders.MINUTES, times);
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
        }

        Toast.makeText(getApplicationContext(), "Event " + name_s + " added with Reminder Type " + reminder[selected_reminder] ,Toast.LENGTH_SHORT).show();

        //TroubleShooting codes
        cur = cr.query(Calendars.CONTENT_URI, null, null,null,null);
        cur.moveToNext();
        int k = cur.getColumnIndex(Calendars.MAX_REMINDERS);
        String t = cur.getString(k);
        Log.i("Column Number", t);
    }
}

