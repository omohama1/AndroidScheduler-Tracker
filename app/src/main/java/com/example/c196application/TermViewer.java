package com.example.c196application;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TermViewer extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int VIEWER_REQUEST_CODE = 3002;
    private static final int ADD_COURSE_CODE =3003 ;
    private String action;
    private String termFilter;
    private TextView termText;
    ListView courseList;
    CourseCursorAdapter courseAdapter;

    ArrayList<String> courses;
    DBOpenHelper dbHelper;
    SQLiteDatabase db;
    Uri uri;
    int termID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHelper = new DBOpenHelper(this, null, null, DBOpenHelper.DATABASE_VERSION);
        db= dbHelper.getWritableDatabase();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            termID = extras.getInt("TERM_ID");
            System.out.println("This is the term ID: "+termID);
        }


        Intent intent = getIntent();
        uri = intent.getParcelableExtra(TermProvider.CONTENT_ITEM_TYPE);
      //  String query = "SELECT * FROM " + dbHelper.TABLE_COURSES + " WHERE " + dbHelper.COURSE_TERM + " =2";//uri.getLastPathSegment();
      //  Cursor courseCursor = db.rawQuery(query,null);
       courseAdapter = new CourseCursorAdapter(this, null);

        termText = (TextView) findViewById(R.id.termView);
        System.out.println("TERM ID = " + uri.getLastPathSegment());

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            // setTitle(getString(R.string.new_note));
        } else {
            action = Intent.ACTION_EDIT;
            termFilter = DBOpenHelper.TERM_ID + "=" + uri.getLastPathSegment();

            String termQuery = "SELECT * FROM " + dbHelper.TABLE_TERMS + " WHERE " + dbHelper.TERM_ID + " = "+uri.getLastPathSegment();
            Cursor cursor = db.rawQuery(termQuery,null);
            cursor.moveToFirst();
            // SimpleDateFormat dtf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

            String termTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_TITLE));
            String termStartDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START));
            String termEndDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END));
            SimpleDateFormat dtf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            SimpleDateFormat dtf2= new SimpleDateFormat("EEE MMM dd yyyy");
            try {
                Date tempStartDate = dtf1.parse(termStartDate);
                Date tempEndDate = dtf1.parse(termEndDate);
                termStartDate = dtf2.format(tempStartDate);
                termEndDate= dtf2.format(tempEndDate);

                termText.setText("Term title: " + termTitle + "\n" +
                        "Term start: " + termStartDate + "\n" +
                        "Term end: " + termEndDate);

                //  courseAdapter = showCourse(uri.getLastPathSegment());

                courseList = (ListView) findViewById(R.id.termCourseView);
                //    ArrayAdapter<String> alternative = showCourses(uri.getLastPathSegment());
                courseList.setAdapter(courseAdapter);

                //Test method for showing correct courses per term
                // courseList.setAdapter(alternative);

                courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(TermViewer.this, CourseViewer.class);
                        Uri uri = Uri.parse(CourseProvider.CONTENT_URI + "/" + id);
                        intent.putExtra(CourseProvider.CONTENT_ITEM_TYPE, uri);

                        startActivityForResult(intent, VIEWER_REQUEST_CODE);
                    }
                });
                getLoaderManager().initLoader(0, null, this);

            }
            catch(ParseException e){e.printStackTrace();}
        }

    }


    private ArrayAdapter<String> showCourses (String termID){
        ArrayList<String> courseList = new ArrayList<>();
        String courseName = "";
        String courseTerm[] = {termID};
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT * FROM " + dbHelper.TABLE_COURSES + " WHERE  " +
                dbHelper.COURSE_TERM + " = " + uri.getLastPathSegment();
        Cursor cursor1 = db.query(dbHelper.TABLE_COURSES, new String[] {"_id","course_title",
                "course_start","course_end","course_notes","course_status","course_term","course_mentor",
                "course_alert"},"course_term =?",courseTerm,null,null,null,null);


        cursor1.moveToFirst();


       while (cursor1.isAfterLast() == false) {
            courseName = cursor1.getString(cursor1.getColumnIndex(dbHelper.COURSE_TITLE));
            courseList.add(courseName);
            cursor1.moveToNext();
        }
        ArrayAdapter<String> courseAAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        courseAAdapter.addAll(courseList);
        return courseAAdapter;
    }
    private CourseCursorAdapter showCourse(String termID) {
        courses = new ArrayList<>();
        String courseName = "";
        String courseTerm[] = {termID};
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT * FROM " + dbHelper.TABLE_COURSES + " WHERE  " +
                dbHelper.COURSE_TERM + " = " + uri.getLastPathSegment();
        System.out.println(query);
        //Cursor points to location in results.
/*
 "course_title";
    public static final String COURSE_START = "course_start";
    public static final String COURSE_END = "course_end";
    public static final String COURSE_NOTES = "course_notes";
    public static final String COURSE_STATUS = "course_status";
    public static final String COURSE_TERM = "course_term";
    public static final String COURSE_MENTOR = "course_mentor";
    public static final String COURSE_ALERT = "course_alert";
* */
        Cursor cursor1 = db.query(dbHelper.TABLE_COURSES, new String[] {"_id","course_title",
                "course_start","course_end","course_notes","course_status","course_term","course_mentor",
                "course_alert"},"course_term =?",courseTerm,null,null,null,null);


        cursor1.moveToFirst();


       /* while (cursor1.isAfterLast() == false) {
            courseName = cursor1.getString(cursor1.getColumnIndex(dbHelper.COURSE_TITLE));
            courses.add(courseName);
            cursor1.moveToNext();
        }*/
        courseAdapter = new CourseCursorAdapter(this, cursor1);

        return courseAdapter;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String termStr = uri.getLastPathSegment().toString();
        return new CursorLoader(this, CourseProvider.CONTENT_URI,
                null,  dbHelper.COURSE_TERM + "="+termStr,null , null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        courseAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        courseAdapter.swapCursor(null);

    }

    public void deleteTerm(View view) {
        String selectedTerm = uri.getLastPathSegment();
        boolean coursesPresent = false;
        db = dbHelper.getWritableDatabase();
        String query = "SELECT * FROM " + dbHelper.TABLE_COURSES + " WHERE " +
                dbHelper.COURSE_TERM + " = " + selectedTerm;
        System.out.println(query);
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            coursesPresent = true;
            Toast.makeText(this, "term cannot be deleted - there are courses present", Toast.LENGTH_SHORT).show();

        } else {
            db.delete(dbHelper.TABLE_TERMS, dbHelper.TERM_ID + "=?", new String[]
                    {selectedTerm});
            Toast.makeText(this, "Empty term deleted", Toast.LENGTH_SHORT).show();

        }
    }


    public void addCourseToTerm(View view) {
        Intent addCourseIntent = new Intent(TermViewer.this,NewCourse.class);
        addCourseIntent.putExtra("TERM_ID",termID);
        startActivityForResult(addCourseIntent, ADD_COURSE_CODE);
    }
}

