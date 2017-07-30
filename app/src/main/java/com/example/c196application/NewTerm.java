package com.example.c196application;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class NewTerm extends AppCompatActivity {

    private static final int END_DIALOG_ID = 2002;
    Button startDateButton;
    Button endDateButton;
    int startYear;
    int startMonth;
    int startDay;
    int endYear;
    int endMonth;
    int endDay;
    String title;
    EditText startDate;
    EditText endDate;
    EditText termTitle;
    Date start;
    Date end;
    DBOpenHelper dbHelper;
    private static final int START_DIALOG_ID = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_term);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = new DBOpenHelper(this,null,null,DBOpenHelper.DATABASE_VERSION);
        selectStartDate();
        selectEndDate();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void selectStartDate() {
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

    public void selectEndDate() {
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


    private DatePickerDialog.OnDateSetListener startDatePickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            startDate = (EditText) findViewById(R.id.startDate);
            startYear = year;
            startMonth = month + 1;
            startDay = dayOfMonth;
            Toast.makeText(NewTerm.this, "Start Year: " + startYear + " Month: " + startMonth + " Day: " + startDay, Toast.LENGTH_SHORT).show();

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
            endDate = (EditText) findViewById(R.id.endDate);
            endYear = year;
            endMonth = month + 1;
            endDay = dayOfMonth;
            Toast.makeText(NewTerm.this, "End Year: " + endYear +
                    " Month: " + endMonth + " Day: " + endDay, Toast.LENGTH_SHORT).show();

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

    public String fetchTitle() {
        termTitle = (EditText) findViewById(R.id.termTitle);
        String titleStr = termTitle.getText().toString();
        if (titleStr.isEmpty() || titleStr == null) {
            Toast.makeText(NewTerm.this, "You must enter a term title", Toast.LENGTH_SHORT).show();
            title = null;
        } else {
            title = termTitle.getText().toString();
        }
        return title;
    }

    public boolean checkDate() {
        boolean dateOK = false;
        if (start == null || end == null) {
            Toast.makeText(NewTerm.this, "You must choose a start and end date", Toast.LENGTH_SHORT).show();
        } else if (start.after(end)) {
            Toast.makeText(NewTerm.this, "The start date must come before the end date", Toast.LENGTH_SHORT).show();
        } else {
            dateOK = true;
        }
        return dateOK;
    }


    public void createNewTerm(View view) {

        String titleStr = fetchTitle();
        Term term = new Term(titleStr,start,end);
        if (checkDate() && titleStr != null) {
            Date startDate = start;
            Date endDate = end;

            String termStr = titleStr + " " + startDate.toString() + " " +
                    endDate.toString();
            Toast.makeText(NewTerm.this, termStr, Toast.LENGTH_SHORT).show();
            dbHelper.addTerm(term);
            Toast.makeText(NewTerm.this, "Term created", Toast.LENGTH_SHORT).show();

        }
        else
            {
            Toast.makeText(NewTerm.this, "Missing data", Toast.LENGTH_SHORT).show();

        }
    }
}
