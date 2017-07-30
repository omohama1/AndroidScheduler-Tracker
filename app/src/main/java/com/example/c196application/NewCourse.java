package com.example.c196application;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class NewCourse extends AppCompatActivity {

    private static final int START_DIALOG_ID = 4;
    public static final String userPreferences = "userPrefs";
    Button startDateButton;
    Button endDateButton;
    int startYear;
    int startMonth;
    int startDay;
    int endYear;
    int endMonth;
    int endDay;
    Switch alarmSwitch;
    Date start;
    Date end;
    EditText startDate;
    EditText endDate;
    EditText courseTitle;
    ListView mentorList;
    String[] courseMentors;
    String courseMentorString;
    String titleString;
    String statusString;
    static final int END_DIALOG_ID = 6;
    DBOpenHelper dbHelper;
    SQLiteDatabase db;
    ArrayAdapter<String> mentorAdapter;
    Uri uri;
    int courseID;
    int termID;
    Spinner termSpinner;
    String term;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            courseID = extras.getInt("COURSE_ID");
            termID = extras.getInt("TERM_ID");
            System.out.println("This is the course ID: " + courseID);
        }


        dbHelper = new DBOpenHelper(this, null, null, DBOpenHelper.DATABASE_VERSION);
        db = dbHelper.getWritableDatabase();

        setContentView(R.layout.activity_new_course);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        showDialogOnStartButtonClick();
        showDialogOnEndButtonClick();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        uri = CourseProvider.CONTENT_URI;
        System.out.println("This is the uri " + uri.toString());
        termSpinner = (Spinner) findViewById(R.id.term_spinner);
        populateTerms();

        populateMentors();
        if (this.courseID != 0) {
            fillValues();
        } else {
        }

        //Attempting to use cursor to fill in values to edit previously entered course information

    }

    public void onClick(View view) {
        SparseBooleanArray checked = mentorList.getCheckedItemPositions();
        ArrayList<String> selectedItems = new ArrayList<String>();
        for (int i = 0; i < checked.size(); i++) {
            int position = checked.keyAt(i);
            if (checked.valueAt(i)) {
                selectedItems.add(mentorAdapter.getItem(position));
            }
        }
        courseMentors = new String[selectedItems.size()];
        for (int i = 0; i < selectedItems.size(); i++) {
            courseMentors[i] = selectedItems.get(i);
        }
    }

    public int getAlarmStatus() {
        SharedPreferences sharedPref = getSharedPreferences(userPreferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        alarmSwitch = (Switch) findViewById(R.id.course_alarm_switch);
        int alarmStatus = 0;
        if (alarmSwitch.isChecked()) {
            alarmStatus = 1;
            prefEditor.putInt("Alarm", 1);
            Toast.makeText(NewCourse.this, "Course alarm set", Toast.LENGTH_SHORT).show();
            return alarmStatus;
        } else {
            alarmStatus = 0;
            prefEditor.putInt("Alarm", 0);
            Toast.makeText(NewCourse.this, "Course alarm off", Toast.LENGTH_SHORT).show();
        }
        return alarmStatus;
    }

    private void fillValues() {
        //    int courseID = 2;  //TEST PARAMETER
        if (db.isOpen()) {
            db.close();
            db = dbHelper.getWritableDatabase();
            String query = "SELECT * FROM " + dbHelper.TABLE_COURSES + " WHERE " +
                    dbHelper.COURSE_ID + " = " + this.courseID;
            Cursor cursor1 = db.rawQuery(query, null);
            cursor1.moveToFirst();

            while (!cursor1.isAfterLast()) {
                if (cursor1.getInt(cursor1.getColumnIndex(dbHelper.COURSE_ID))
                        != courseID) {
                    cursor1.moveToNext();
                } else {
                    SimpleDateFormat dtf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                    SimpleDateFormat dtf2 = new SimpleDateFormat("EEE, dd-MMM-yyyy");
                    String startDateStr = cursor1.getString(cursor1.getColumnIndex(dbHelper.COURSE_START));
                    String endDateStr = cursor1.getString(cursor1.getColumnIndex(dbHelper.COURSE_END));
                    String titleStr = cursor1.getString(cursor1.getColumnIndex(dbHelper.COURSE_TITLE));
                    String statusStr = cursor1.getString(cursor1.getColumnIndex(dbHelper.COURSE_STATUS));
                    System.out.println();
                    System.out.println();
                    System.out.println();
                    try {
                        start = dtf1.parse(startDateStr);
                        end = dtf1.parse(endDateStr);
                        startDateStr = dtf2.format(start);
                        endDateStr = dtf2.format(end);

                        System.out.println(" Course info " + titleStr + " Course status: " + statusStr);
                        startDate = (EditText) findViewById(R.id.startDateText);
                        startDate.setText(startDateStr, TextView.BufferType.EDITABLE);
                        endDate = (EditText) findViewById(R.id.endDateText);
                        endDate.setText(endDateStr, TextView.BufferType.EDITABLE);
                        courseTitle = (EditText) findViewById(R.id.courseTitle);
                        courseTitle.setText(titleStr, TextView.BufferType.EDITABLE);
                        //    cursor.close();
                    } catch (ParseException e) {
                        e.printStackTrace();
                        ;
                    }
                    cursor1.moveToNext();

                }
            }
        }
    }


    public void showDialogOnStartButtonClick() {
        startDateButton = (Button) findViewById(R.id.startDateButton);
        startDateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(START_DIALOG_ID);
                    }
                }

        );
    }

    public void showDialogOnEndButtonClick() {
        endDateButton = (Button) findViewById(R.id.endDateButton);
        endDateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(END_DIALOG_ID);
                    }
                }

        );
    }

    protected Dialog onCreateDialog(int id) {
        if (id == START_DIALOG_ID) {
            return new DatePickerDialog(this, startDatePickerListener, 2017, 8, 7);
        } else if (id == END_DIALOG_ID) {
            return new DatePickerDialog(this, endDatePickerListener, 2017, 8, 7);
        } else {
            return null;
        }
    }

    private void populateMentors() {
        List<String> mentors = new ArrayList<>();

        Cursor cursor = db.query(dbHelper.TABLE_MENTORS, null, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String mentorName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_NAME));
            mentors.add(mentorName);
            cursor.moveToNext();
        }


        mentorAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_multiple_choice, mentors);
        mentorList = (ListView) findViewById(R.id.mentorSelectList);
        mentorList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mentorList.setAdapter(mentorAdapter);

    }

    private DatePickerDialog.OnDateSetListener startDatePickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            startDate = (EditText) findViewById(R.id.startDateText);
            startYear = year;
            startMonth = month + 1;
            startDay = dayOfMonth;
            Toast.makeText(NewCourse.this, "Start Year: " + startYear + " Month: " + startMonth + " Day: " + startDay, Toast.LENGTH_SHORT).show();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat newFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy");
            try {
                start = format.parse(startYear + "-" + startMonth + "-" + startDay);

                String startStr = newFormat.format(start);
                startDate.setText(startStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    private DatePickerDialog.OnDateSetListener endDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            endDate = (EditText) findViewById(R.id.endDateText);
            endYear = year;
            endMonth = month + 1;
            endDay = dayOfMonth;
            Toast.makeText(NewCourse.this, "End Year: " + endYear + " Month: " + endMonth + " Day: " + endDay, Toast.LENGTH_SHORT).show();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat newFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy");
            try {
                end = format.parse(endYear + "-" + endMonth + "-" + endDay);
                String endStr = newFormat.format(end);
                endDate.setText(endStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
    };

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        //Check which radio button is clicked
        switch (view.getId()) {
            case R.id.radio_completed:
                statusString = "Completed";
                Toast.makeText(NewCourse.this, "Status: " + statusString, Toast.LENGTH_SHORT).show();
                break;
            case R.id.radio_dropped:
                statusString = "Dropped";
                Toast.makeText(NewCourse.this, "Status: " + statusString, Toast.LENGTH_SHORT).show();
                break;
            case R.id.radio_progress:
                statusString = "In progress";
                Toast.makeText(NewCourse.this, "Status: " + statusString, Toast.LENGTH_SHORT).show();
                break;
            case R.id.radio_will_take:
                statusString = "Will take";
                Toast.makeText(NewCourse.this, "Status: " + statusString, Toast.LENGTH_SHORT).show();
                break;

        }
    }

    private void populateTerms() {
        List<String> terms = new ArrayList<>();
        Cursor cursor = db.query(dbHelper.TABLE_TERMS, null, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String termName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_TITLE));
            terms.add(termName);
            cursor.moveToNext();
        }


        ArrayAdapter<String> courseTermAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, terms);
        courseTermAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        termSpinner.setAdapter(courseTermAdapter);

    }

    public int fetchTerm() {
        termID = 0;
        String checkTermString = termSpinner.getSelectedItem().toString();
        if (checkTermString.isEmpty() || checkTermString == null) {
            Toast.makeText(NewCourse.this, "You must enter a term", Toast.LENGTH_SHORT).show();
        } else {
            term = checkTermString;

            Cursor cursor = db.query(dbHelper.TABLE_TERMS, null, null, null, null, null, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                String termName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_TITLE));
                if (termName.equals(checkTermString)) {
                    termID = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.TERM_ID));
                }

                cursor.moveToNext();
            }
        }


        return termID;

    }

    public int checkTerm() {

        int termID = fetchTerm();
        Cursor cursor;
        SimpleDateFormat dtf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

        String query = "SELECT * FROM " + dbHelper.TABLE_TERMS + " WHERE " + dbHelper.TERM_ID +
                "=" + termID;
        cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        Date termStartDate = new Date();
        Date termEndDate = new Date();
        if (checkDate()) {
            try {
                termStartDate = (dtf1.parse(cursor.getString(cursor.getColumnIndex(dbHelper.TERM_START))));
                termEndDate = dtf1.parse(cursor.getString(cursor.getColumnIndex(dbHelper.TERM_END)));

            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (start.before(termStartDate)) {
                Toast.makeText(this, "The course must start after the start of the term", Toast.LENGTH_LONG).show();
                termID =0;

            } else if (end.after(termEndDate)) {
                Toast.makeText(this, "The course must end before the end of the term", Toast.LENGTH_LONG).show();
                termID = 0;
            } else {
                termID = cursor.getInt(cursor.getColumnIndex(dbHelper.TERM_ID));

            }

        }

        return termID;
    }


    public boolean checkDate() {

        boolean dateOK = false;
        if (start == null || end == null) {
            Toast.makeText(NewCourse.this, "You must choose a start and end date", Toast.LENGTH_SHORT).show();
        } else if (start.after(end)) {
            Toast.makeText(NewCourse.this, "The start date must come before the end date", Toast.LENGTH_SHORT).show();
        } else {
            dateOK = true;
        }
        return dateOK;
    }

    public int[] fetchCourseMentor() {
        if (!db.isOpen()) {
            db = dbHelper.getWritableDatabase();
        }
            int mentorID[] = new int[courseMentors.length];
            for (int i = 0; i < courseMentors.length; i++) {
                String checkMentorString = courseMentors[i];
                if (checkMentorString.isEmpty() || checkMentorString == null) {
                    Toast.makeText(NewCourse.this, "You must enter a course mentor", Toast.LENGTH_SHORT).show();
                } else {
                    courseMentorString = checkMentorString;

                    Cursor cursor = db.query(dbHelper.TABLE_MENTORS, null, null, null, null, null, null);
                    cursor.moveToFirst();

                    while (!cursor.isAfterLast()) {
                        String mentorName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_NAME));
                        if (mentorName.equals(checkMentorString)) {
                            mentorID[i] = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.MENTOR_ID));
                        }

                        cursor.moveToNext();
                    }
                }
            }




        return mentorID;
    }



    public String fetchCourseTitle() {
        courseTitle = (EditText) findViewById(R.id.courseTitle);
        titleString = courseTitle.getText().toString();
        if (titleString == null || titleString.isEmpty()) {
            Toast.makeText(NewCourse.this, "You must enter a course title", Toast.LENGTH_SHORT).show();
            titleString = null;
        } else {

            titleString = courseTitle.getText().toString();
        }
        return titleString;

    }

    public String getStatusString() {
        if (statusString == null) {
            Toast.makeText(NewCourse.this, "You must select a course status", Toast.LENGTH_SHORT).show();

        } else {
            return statusString;

        }
        return statusString;

    }

    private boolean checkDuplicateCourse() {
        boolean duplicate = false;
        String subTitle = fetchCourseTitle();
        String dbString = "";
        String query = "SELECT * FROM " + dbHelper.TABLE_COURSES;
        //Cursor points to location in results.
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("course_title")) != null) {
                String mentor = c.getString(c.getColumnIndex("course_title"));
                if (mentor.equals(subTitle)) {
                    duplicate = true;
                    return duplicate;
                } else {
                    c.moveToNext();
                }
            }
        }

        return duplicate;
    }


    public void createCourse(View view) {
        onClick(view);
        Course course;
        String status = getStatusString();
        int mentor[] = fetchCourseMentor();
        String title = fetchCourseTitle();
        int alarmSet = getAlarmStatus();
        Date endDay = end;
        Date startDay = start;
        int termID = checkTerm();
        if (checkDuplicateCourse()) {
            Toast.makeText(NewCourse.this, "That course already exists", Toast.LENGTH_SHORT).show();
            if (status != null && (mentor.length > 0) && title != null && checkDate() && (termID != 0)) {

                Course oldCourse = new Course(title, status, startDay, endDay, termID, alarmSet);
                oldCourse.setCourseID(this.courseID);
                dbHelper.updateCourse(oldCourse);
                for (int i = 0; i < mentor.length; i++) {
                    if (oldCourse.getCourseID() != 0 && mentor.length != 0) {
                        dbHelper.addCourseMentor(oldCourse.getCourseID(), mentor[i]);

                    }
                }
                Toast.makeText(NewCourse.this, "Course updated", Toast.LENGTH_SHORT).show();
            } else {
            }
        } else {
            if (status != null && (mentor.length > 0) && title != null && checkDate() && (termID != 0)) {
                String courseStr = title + " " + status + " " + startDay.toString() + " " +
                        endDay.toString() + "Term =: " + termID;
                course = new Course(title, status, startDay, endDay, termID, alarmSet);
                dbHelper.addCourse(course);
                for (int i = 0; i < mentor.length; i++) {
                    if (course.getCourseID() != 0 && mentor.length != 0) {
                        dbHelper.addCourseMentor(course.getCourseID(), mentor[i]);
                        Toast.makeText(NewCourse.this, course.getCourseID(), Toast.LENGTH_SHORT).show();
                    }
                }
                Toast.makeText(NewCourse.this, courseStr, Toast.LENGTH_SHORT).show();

            } else {

            }
        }
        dbHelper.close();


    }

    public void deleteCourse(View view) {
        String courseIDStr = "" + courseID;
        if (courseID == 0) {
            Toast.makeText(NewCourse.this, "That course has not yet been created", Toast.LENGTH_SHORT).show();
        } else if (statusString == null) {
            Toast.makeText(NewCourse.this, "You must drop the course before you can delete it", Toast.LENGTH_SHORT).show();
        } else if (statusString.isEmpty() || !statusString.equals("Dropped")) {
            Toast.makeText(NewCourse.this, "You must drop the course before you can delete it", Toast.LENGTH_SHORT).show();
        } else {
            db.delete(dbHelper.TABLE_COURSES, dbHelper.COURSE_ID + "=?", new String[]
                    {courseIDStr});
            Toast.makeText(this, "Course deleted", Toast.LENGTH_SHORT).show();

        }
    }
}
