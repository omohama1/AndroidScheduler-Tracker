package com.example.c196application;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class NewAssessment extends AppCompatActivity {

    private static final int ASSESS_DIALOG_ID =5010 ;
    private DBOpenHelper dbHelper;
    SQLiteDatabase db;
    Spinner courseSpinner;
    int testYear;
    int testMonth;
    int testDay;
    TextView dateView;
    TextView assessTypeView;
    Date testDate;
    String course;
    String typeStr;
    Button dateButton;
    Switch alarmSwitch;
    String assessmentID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_assessment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dateView = (TextView) findViewById(R.id.dateText);
        assessTypeView=(TextView)findViewById(R.id.assessText);

        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            assessmentID = extras.getString("ASSESSMENT_ID");
            System.out.println("This is the assessment ID: "+assessmentID);
        }
        dbHelper = new DBOpenHelper(this, null, null, DBOpenHelper.DATABASE_VERSION);
        db = dbHelper.getWritableDatabase();
        fillValues();
        courseSpinner = (Spinner) findViewById(R.id.courseSpinner);
        showDialogOnDateButtonClick();
        populateCourses();
    }

    private DatePickerDialog.OnDateSetListener assessDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            testYear = year;
            testMonth = month + 1;
            testDay = dayOfMonth;
            Toast.makeText(NewAssessment.this, "Assessment year: " + testYear + " Month: " + testMonth + " Day: " + testDay, Toast.LENGTH_SHORT).show();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat newFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy");
            try {
                testDate = format.parse(testYear + "-" + testMonth + "-" + testDay);
                String endStr = newFormat.format(testDate);
                dateView.setText(endStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    protected Dialog onCreateDialog(int id) {
        if (id == ASSESS_DIALOG_ID) {
            return new DatePickerDialog(this, assessDatePicker, 2017, 8, 7);
        }  else {
            return null;
        }
    }

    private void fillValues() {
        //    int courseID = 2;  //TEST PARAMETER
        if (db.isOpen()) {
            db.close();
            db = dbHelper.getWritableDatabase();
            String query = "SELECT * FROM " + dbHelper.TABLE_ASSESSMENTS + " WHERE " +
                    dbHelper.ASSESSMENT_ID + " = " + this.assessmentID;
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            if(!cursor.isAfterLast()){
                String dateStr = cursor.getString(cursor.getColumnIndex(dbHelper.ASSESSMENT_DATE));
                String assessmentType = cursor.getString(cursor.getColumnIndex(dbHelper.ASSESSMENT_TYPE));
                SimpleDateFormat dtf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                SimpleDateFormat dtf2= new SimpleDateFormat("EEE MMM dd yyyy");
                try {
                    Date assessDate = dtf1.parse(dateStr);
                    dateStr = dtf2.format(assessDate);
                    dateView.setText(dateStr);
                    assessTypeView.setText(assessmentType);
                }
                catch(ParseException e){
                    e.printStackTrace();
                }


            }
            else{}

        }
    }

        public void showDialogOnDateButtonClick() {
        dateButton = (Button) findViewById(R.id.dateButton);
        dateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(ASSESS_DIALOG_ID);
                    }
                }

        );
    }

    private void populateCourses() {
        List<String> courses = new ArrayList<>();

        Cursor cursor = db.query(dbHelper.TABLE_COURSES, null, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String courseName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_TITLE));
            courses.add(courseName);
            cursor.moveToNext();
        }


        ArrayAdapter<String> mentorAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, courses);
        mentorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(mentorAdapter);

    }


    public boolean checkDate() {
        int courseID = fetchCourse();
        boolean dateOK = false;
        db = dbHelper.getWritableDatabase();
        String query = "SELECT * FROM " + dbHelper.TABLE_COURSES + " WHERE " + dbHelper.COURSE_ID + "=" + courseID;
       // Cursor cursor = db.query(dbHelper.TABLE_COURSES, null, null, null, null, null, dbHelper.COURSE_END);
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        Date end = new Date();
        Date start = new Date();
        while (cursor.isAfterLast() == false) {
            String endDateStr = cursor.getString(cursor.getColumnIndex(dbHelper.COURSE_END));
            String startDateStr=  cursor.getString(cursor.getColumnIndex(dbHelper.COURSE_START));
            try {
                SimpleDateFormat dtf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                end = dtf1.parse(endDateStr);
                start=dtf1.parse(startDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cursor.moveToNext();
        }
        if (testDate == null) {
            Toast.makeText(NewAssessment.this, "You must choose an assessment date", Toast.LENGTH_SHORT).show();
        } else if (testDate.after(end)) {
            Toast.makeText(NewAssessment.this, "The assessment must come before the end of the course", Toast.LENGTH_SHORT).show();
        }
        else if (testDate.before(start)) {
            Toast.makeText(NewAssessment.this, "The assessment must come after the start of the course", Toast.LENGTH_SHORT).show();
        }
        else {
            dateOK = true;
        }
        return dateOK;
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        //Check which radio button is clicked
        switch (view.getId()) {
            case R.id.radioObjective:
                typeStr = "Objective Assessment";
                Toast.makeText(NewAssessment.this, "Type: " + typeStr, Toast.LENGTH_SHORT).show();
                break;
            case R.id.radioPerformance:
                typeStr = "Performance Assessment";
                Toast.makeText(NewAssessment.this, "Type: " + typeStr, Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public int getAlarmStatus(){
        alarmSwitch = (Switch)findViewById(R.id.assess_alert_switch);
        int alarmStatus = 0;
        if(alarmSwitch.isChecked()){
            alarmStatus=1;
            Toast.makeText(NewAssessment.this,"Assessment alarm set", Toast.LENGTH_SHORT).show();
        }
        else{
            alarmStatus=0;
            Toast.makeText(NewAssessment.this,"Assessment alarm off", Toast.LENGTH_SHORT).show();
        }
        return alarmStatus;
    }

    public int fetchCourse() {
        int courseID = -1;
        String checkCourseString = courseSpinner.getSelectedItem().toString();
        if (checkCourseString.isEmpty() || checkCourseString == null) {
            Toast.makeText(NewAssessment.this, "You must enter a course", Toast.LENGTH_SHORT).show();
        } else {
            course = checkCourseString;

            Cursor cursor = db.query(dbHelper.TABLE_COURSES, null, null, null, null, null, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                String courseName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_TITLE));
                if (courseName.equals(checkCourseString)) {
                    courseID = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.COURSE_ID));
                }

                cursor.moveToNext();
            }
        }


        return courseID;

    }

    public String fetchAssessmentTitle() {
        String checkCourseString = courseSpinner.getSelectedItem().toString();
        String courseStr = checkCourseString+ " " + typeStr;
        return courseStr;
    }

    private boolean checkDuplicateAssessment(){
        boolean duplicate = false;
        String subTitle = fetchAssessmentTitle();
        String query = "SELECT * FROM " + dbHelper.TABLE_ASSESSMENTS;
        //Cursor points to location in results.
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("assessment_title")) != null) {
                String assess= c.getString(c.getColumnIndex("assessment_title"));
                if(assess.equals(subTitle)) {
                    duplicate=true;
                    return duplicate;
                }
                else {
                    c.moveToNext();
                }
            }
        }

        return duplicate;
    }

    public String getTypeString() {
        if ( typeStr == null) {
            Toast.makeText(NewAssessment.this, "You must select an assessment type", Toast.LENGTH_SHORT).show();
            typeStr = null;
        } else {

        }
        return typeStr;

    }
    public void createAssessment(View view) {
        Assessment assessment;
        int alert=getAlarmStatus();
        String type = getTypeString();
        int courseID = fetchCourse();

        String title = fetchAssessmentTitle();
        Date assessDate = testDate;
        if(checkDuplicateAssessment()){
            Toast.makeText(NewAssessment.this, "That assessment already exists", Toast.LENGTH_SHORT).show();

            if (type != null && title != null && checkDate() && (courseID != -1)) {
                int assessID = Integer.parseInt(assessmentID);
                Assessment oldCourse = new Assessment(title,type,assessDate,courseID,alert);
                oldCourse.setAssessmentID(assessID);
                dbHelper.updateAssessment(oldCourse);
                Toast.makeText(NewAssessment.this, "Assessment updated", Toast.LENGTH_SHORT).show();
            } else {
            }





        }
        else {
            if (type != null && (courseID != -1) && title != null && checkDate()) {
                String assessStr = "Course " + title + " Type: " + type + " Date:" +
                        assessDate.toString();
                assessment = new Assessment(title,type,assessDate,courseID,alert);
                dbHelper.addAssessment(assessment);
                Toast.makeText(NewAssessment.this, assessStr, Toast.LENGTH_SHORT).show();
            } else {

            }
        }


    }



    public void deleteAssessment(View view) {
        String assessIDStr=assessmentID;
        if(assessmentID.equals("0")){
            Toast.makeText(NewAssessment.this,"That assessment has not yet been created",Toast.LENGTH_SHORT).show();
        }
        else {
            db.delete(dbHelper.TABLE_ASSESSMENTS, dbHelper.ASSESSMENT_ID + "=?", new String[]
                    {assessmentID});
            Toast.makeText(this, "Assessment deleted", Toast.LENGTH_SHORT).show();

        }
    }

}
