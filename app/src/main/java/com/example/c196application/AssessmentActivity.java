package com.example.c196application;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class AssessmentActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ADD_ASSESSMENT_REQUEST_CODE =5001 ;
    private static final int VIEWER_REQUEST_CODE = 5002 ;
    private static final int DB_VERSION=DBOpenHelper.DATABASE_VERSION;
    private static final int BACK_TO_COURSE_CODE = 5003 ;
    private ArrayList<String> assessments;
    private DBOpenHelper dbHelper;
    private ListView assessmentList;
    private String courseID;
    private Uri uri;

    private AssessmentCursorAdapter assessmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle extras=getIntent().getExtras();

        if(extras!=null){
            courseID = extras.getString("COURSE_ID");
            System.out.println("This is the course ID: "+courseID);
        }
        Intent intent = getIntent();
        uri = intent.getParcelableExtra(CourseProvider.CONTENT_ITEM_TYPE);
        setContentView(R.layout.activity_assessment);
        assessmentAdapter = new AssessmentCursorAdapter(this,null,0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAssessment(view);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dbHelper = new DBOpenHelper(this, null, null, DBOpenHelper.DATABASE_VERSION);
        printDatabase();
        assessmentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(AssessmentActivity.this, AssessmentViewer.class);
                Uri uri = Uri.parse(AssessmentProvider.CONTENT_URI + "/" + id);
                intent.putExtra(AssessmentProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, VIEWER_REQUEST_CODE);
            }
        });
        getLoaderManager().initLoader(0, null, this);

    }


    //This method found on
    //https://stackoverflow.com/questions/26651602/display-back-arrow-on-toolbar-android
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        if(courseID!=null){
            Intent intent = new Intent(AssessmentActivity.this, CourseViewer.class);
            uri = Uri.parse(CourseProvider.CONTENT_URI + "/" + courseID);
            intent.putExtra(CourseProvider.CONTENT_ITEM_TYPE, uri);
            startActivityForResult(intent,BACK_TO_COURSE_CODE);
        }
    }

    public void addAssessment(View view) {
        Intent intent = new Intent(this, NewAssessment.class);
        startActivityForResult(intent, ADD_ASSESSMENT_REQUEST_CODE);
    }

    public void printDatabase() {
        assessmentList = (ListView) findViewById(R.id.assessmentList);
        assessmentList.setAdapter(assessmentAdapter);
        assessments = new ArrayList<>();
        String dbString = dbHelper.assessmentToString();
        assessments.add(dbString);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                assessments);

        System.out.println(dbString);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(courseID !=null) {
            return new CursorLoader(this, AssessmentProvider.CONTENT_URI,
                    null, dbHelper.ASSESSMENT_COURSE + "=" + courseID, null, null);
        }
        else{
            return new CursorLoader(this, AssessmentProvider.CONTENT_URI,
                    null, null, null, null);

        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        assessmentAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        assessmentAdapter.swapCursor(null);
    }
}

