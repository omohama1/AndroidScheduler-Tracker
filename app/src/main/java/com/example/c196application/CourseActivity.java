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
import android.widget.RadioButton;

import java.util.ArrayList;

public class CourseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ADD_COURSE_REQUEST_CODE = 3001;
    private static final int VIEWER_REQUEST_CODE = 3002;
    private static final int DB_VERSION =DBOpenHelper.DATABASE_VERSION ;
    private ArrayList<String> courses;
    private DBOpenHelper dbHelper;
    private ListView courseList;
    private CourseCursorAdapter courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        courseAdapter = new CourseCursorAdapter(this, null);
        dbHelper = new DBOpenHelper(this, null, null, DBOpenHelper.DATABASE_VERSION);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCourse(view);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        printDatabase();
        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(CourseActivity.this, CourseViewer.class);
                Uri uri = Uri.parse(CourseProvider.CONTENT_URI + "/" + id);
                intent.putExtra(CourseProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, VIEWER_REQUEST_CODE);
            }
        });
        getLoaderManager().initLoader(0, null, this);

    }

    public void addCourse(View view) {
        Intent intent = new Intent(this, NewCourse.class);
        startActivityForResult(intent, ADD_COURSE_REQUEST_CODE);
    }

    public void printDatabase() {
        courseList = (ListView) findViewById(R.id.courseListView);
        courseList.setAdapter(courseAdapter);
        courses = new ArrayList<>();
        String dbString = dbHelper.courseToString();
        courses.add(dbString);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CourseProvider.CONTENT_URI,
                null, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        courseAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        courseAdapter.swapCursor(null);

    }

}
