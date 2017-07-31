package com.example.c196application;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private  AlertDialog assessAlert;
    private NotificationCompat.Builder notification;
    private static final int TERMS_REQUEST_CODE = 1001;
    private static final int COURSE_REQUEST_CODE = 1002;
    private static final int MENTOR_REQUEST_CODE = 1003;
    private static final int ASSESSMENT_REQUEST_CODE = 1004;
    private static final int NOTIFICATION_ID = 1337;
    private DBOpenHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        assessAlert = new AlertDialog.Builder(MainActivity.this).create();
        dbHelper = new DBOpenHelper(this, null, null, DBOpenHelper.DATABASE_VERSION);
        db = dbHelper.getWritableDatabase();
        //   getNextCourseEnd();
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        triggerCourseNotification();


    }

    private ArrayList<String> getNextAssessment() {
        String assessment = "";
        String date = "";
        ArrayList<String> assessList = new ArrayList<>();
        GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
        GregorianCalendar cal2 = (GregorianCalendar) Calendar.getInstance();
        Cursor cursor = dbHelper.getAssessAlarmCursor();
        SimpleDateFormat dtf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String alarm = cursor.getString(cursor.getColumnIndex(dbHelper.ASSESSMENT_ALERT));
            assessment = cursor.getString(cursor.getColumnIndex(dbHelper.ASSESSMENT_TITLE));
            System.out.println(assessment + " Not in list yet.  Alarm status: " + alarm);
            date = cursor.getString(cursor.getColumnIndex(dbHelper.ASSESSMENT_DATE));
            try {
                cal2.setTime(dtf.parse(date));
                if (cal2.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR) < 10) {
                    assessList.add(assessment);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                cursor.moveToNext();
            }
        }
        //  cursor.close();
        return assessList;
    }

    private ArrayList<String> getNextCourseEnd() {
        String course = "";
        String date = "";
        ArrayList<String> courseList = new ArrayList<>();
        GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
        GregorianCalendar cal2 = (GregorianCalendar) Calendar.getInstance();
        Cursor cursor = dbHelper.getCourseAlarmCursor();
        SimpleDateFormat dtf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String alarm = cursor.getString(cursor.getColumnIndex(dbHelper.COURSE_ALERT));
            course = cursor.getString(cursor.getColumnIndex(dbHelper.COURSE_TITLE));
            System.out.println(course + " Not in list yet.  Alarm status: " + alarm);
            date = cursor.getString(cursor.getColumnIndex(dbHelper.COURSE_END));
            try {
                cal2.setTime(dtf.parse(date));
                if (cal2.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR) < 10) {
                    courseList.add(course);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                cursor.moveToNext();
            }
        }
        //  cursor.close();
        return courseList;
    }


    public void triggerCourseNotification() {
        ArrayList<String> courseList = getNextCourseEnd();
        ArrayList<String> assessList = getNextAssessment();
        String assessStr = "";
        String courseStr = "";
        for (int i = 0; i < assessList.size(); i++) {
            assessStr += assessList.get(i) + "\n";
        }
        for (int i = 0; i < courseList.size(); i++) {
            courseStr += courseList.get(i) + "\n";
        }
        if (courseList.size() > 0) {

            assessAlert = new AlertDialog.Builder(MainActivity.this).create();
            assessAlert.setTitle("Courses ending!");
            assessAlert.setMessage("These courses are ending soon: " + courseStr);
            assessAlert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            assessAlert.show();
            System.out.print("The courses are: " + courseStr);
            notification = new NotificationCompat.Builder(this);
            notification.setAutoCancel(true);
            notification.setSmallIcon(R.mipmap.ic_launcher);
            notification.setTicker("Your course is ending soon");
            notification.setContentTitle("Course ending");
            notification.setContentText("Your course is ending soon: \n" + courseStr);

            Intent intent = new Intent(this, CourseActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setContentIntent(pendingIntent);

            //Issue notification
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(NOTIFICATION_ID, notification.build());

        }
        if (assessList.size() > 0) {


            System.out.print("The courses are: " + assessStr);
            assessAlert = new AlertDialog.Builder(MainActivity.this).create();
            assessAlert.setTitle("Upcoming assessments!");
            assessAlert.setMessage("You have an assessment coming up: \n" + assessStr);
            assessAlert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            assessAlert.show();
            notification = new NotificationCompat.Builder(this);
            notification.setAutoCancel(true);
            notification.setSmallIcon(R.mipmap.ic_launcher);
            notification.setTicker("Your assessment is soon");
            notification.setContentTitle("Assessment soon");
            notification.setContentText("Your assessment is soon: " + assessStr);

            Intent intent = new Intent(this, AssessmentActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setContentIntent(pendingIntent);

            //Issue notification
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(NOTIFICATION_ID, notification.build());

        }

        /*
        if (cal3.get(Calendar.DAY_OF_YEAR)-cal.get(Calendar.DAY_OF_YEAR)>10) {
            notification.setSmallIcon(R.mipmap.ic_launcher);
            notification.setTicker("Your assessment is due soon");
            notification.setContentTitle("Assessment due");
            notification.setContentText("Your assessment is due soon.");

            Intent intent = new Intent(this, CourseActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setContentIntent(pendingIntent);

            //Issue notification
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(NOTIFICATION_ID, notification.build());

        }*/
        else {
            System.out.println("No courses and no assessments due yet");
        }

    }

    public void triggerTermNotification() {

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
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this,
                    "You selected settings",
                    Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_edit) {
            Toast.makeText(MainActivity.this,
                    "You selected edit",
                    Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(MainActivity.this,
                    "You selected notes",
                    Toast.LENGTH_SHORT).show();

        }

        return super.onOptionsItemSelected(item);
    }

    public void openTermsActivity(View view) {
        Intent intent = new Intent(this, TermActivity.class);
        startActivityForResult(intent, TERMS_REQUEST_CODE);
    }

    public void openCourseActivity(View view) {
        Intent intent = new Intent(this, CourseActivity.class);
        startActivityForResult(intent, COURSE_REQUEST_CODE);
    }

    public void openMentorActivity(View view) {
        Intent intent = new Intent(this, MentorActivity.class);
        startActivityForResult(intent, MENTOR_REQUEST_CODE);
    }

    public void openAssessmentActivity(View view) {
        Intent intent = new Intent(this, AssessmentActivity.class);
        startActivityForResult(intent, ASSESSMENT_REQUEST_CODE);
    }

}
