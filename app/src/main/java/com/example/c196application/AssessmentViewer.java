package com.example.c196application;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AssessmentViewer extends AppCompatActivity {
    private static final int EDITOR_REQUEST_CODE =8001 ;
    private String action;
    private String assessmentFilter;
    private TextView assessmentView;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

         uri = intent.getParcelableExtra(AssessmentProvider.CONTENT_ITEM_TYPE);
        assessmentView = (TextView) findViewById(R.id.assessmentText);

        if (uri == null) {
            action = Intent.ACTION_INSERT;

        } else {
            action = Intent.ACTION_EDIT;
            assessmentFilter = DBOpenHelper.ASSESSMENT_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.ALL_ASSESSMENT_COLUMNS, assessmentFilter, null, null);
            cursor.moveToFirst();

            String assessmentTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TITLE));
            String assessmentDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DATE));
            String assessmentType = cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_TYPE));
            SimpleDateFormat dtf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            SimpleDateFormat dtf2= new SimpleDateFormat("EEE MMM dd yyyy");
            try {
                Date assessDate = dtf1.parse(assessmentDate);
                assessmentDate = dtf2.format(assessDate);

                assessmentView.setText("Assessment: " + assessmentTitle + "\n" +
                        "Date: " + assessmentDate + "\n" +
                        "Type: " + assessmentType);


            }
            catch(ParseException e){e.printStackTrace();}
        }
    }

    public void editAssessment(View view) {
        Intent editAssessment = new Intent(AssessmentViewer.this, NewAssessment.class);
        String assessmentID = uri.getLastPathSegment();
        editAssessment.putExtra("ASSESSMENT_ID",assessmentID);
        startActivityForResult(editAssessment, EDITOR_REQUEST_CODE);
    }
}
