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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CourseViewer extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int VIEWER_REQUEST_CODE =4003 ;
    private static final int NOTES_REQUEST_CODE =4004 ;
    private static final int EDITOR_REQUEST_CODE =4005 ;
    private static final int ASSESSMENT_REQUEST_CODE =4006 ;
    TextView courseText;
    TextView statusText;
    TextView startText;
    TextView endText;
    String action;
    String courseFilter;
    Uri uri;
    DBOpenHelper dbHelper;
    ArrayList<String> mentors;
    ListView mentorList;
    MentorCursorAdapter mentorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        dbHelper = new DBOpenHelper(this, null, null, DBOpenHelper.DATABASE_VERSION);
        uri = intent.getParcelableExtra(CourseProvider.CONTENT_ITEM_TYPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        showTermCourses();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_notes:
                openNotes();
                break;
            case R.id.action_edit:
                goToEditor();
                break;
            case R.id.action_view_assessments:
                viewAssessments();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    public void viewAssessments(){
        Intent intentAssess = new Intent(CourseViewer.this,AssessmentActivity.class);
        intentAssess.putExtra("COURSE_ID",uri.getLastPathSegment());
        startActivityForResult(intentAssess,ASSESSMENT_REQUEST_CODE);
    }
    public void openNotes( ){
        Intent intentNotes = new Intent(CourseViewer.this, NotesActivity.class);
        intentNotes.putExtra("COURSE_ID",uri.getLastPathSegment());
        startActivityForResult(intentNotes, NOTES_REQUEST_CODE);

    }
    public void goToEditor(){
        Intent editCourse = new Intent(CourseViewer.this, NewCourse.class);
        int courseID = Integer.parseInt(uri.getLastPathSegment());
        editCourse.putExtra("COURSE_ID",courseID);
        startActivityForResult(editCourse, EDITOR_REQUEST_CODE);

    }


    private ArrayList<String> showMentor() {
        mentors = new ArrayList<>();
        String mentorName = "";

        System.out.println("Uri path is " +uri.getLastPathSegment());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM " +dbHelper.TABLE_MENTORS + " JOIN " + dbHelper.TABLE_CMS +
                " ON " + dbHelper.TABLE_MENTORS + "." + dbHelper.MENTOR_ID + "=" +
                dbHelper.TABLE_CMS + "." + dbHelper.MENTOR +  " JOIN " + dbHelper.TABLE_COURSES + " ON " +
                dbHelper.TABLE_CMS + "."+dbHelper.COURSE + "="+dbHelper.TABLE_COURSES+"."+dbHelper.COURSE_ID
                +" WHERE " + dbHelper.TABLE_CMS + "."+dbHelper.COURSE +"="+uri.getLastPathSegment();

        Cursor cursor1 = db.rawQuery(query,null);

        cursor1.moveToFirst();
        System.out.println(query);
        while (!cursor1.isAfterLast()) {
            mentorName = cursor1.getString(cursor1.getColumnIndex(dbHelper.MENTOR_NAME));
            String mentorPhone = cursor1.getString(cursor1.getColumnIndex(dbHelper.MENTOR_PHONE));
            String mentorEmail = cursor1.getString(cursor1.getColumnIndex(dbHelper.MENTOR_EMAIL));
            mentors.add(mentorName + "\n" + mentorPhone + "\n" + mentorEmail);
            System.out.println(mentorName);
            cursor1.moveToNext();


        }
        cursor1.close();
        return mentors;

}

    private void showTermCourses() {
        courseText = (TextView) findViewById(R.id.courseTitleText);
        startText = (TextView) findViewById(R.id.startDateText);
        endText = (TextView) findViewById(R.id.endDateText);
        statusText = (TextView) findViewById(R.id.courseStatusText);
        if (uri == null) {
            action = Intent.ACTION_INSERT;
        } else {

            action = Intent.ACTION_EDIT;
            courseFilter = DBOpenHelper.COURSE_ID + "=" + uri.getLastPathSegment();
            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.ALL_COURSE_COLUMNS, courseFilter, null, DBOpenHelper.COURSE_TERM);

            cursor.moveToFirst();
            String courseTitle = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_TITLE));
            String courseStartDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_START));
            String courseEndDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_END));
            String courseStatus = cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_STATUS));
            SimpleDateFormat dtf1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            SimpleDateFormat dtf2= new SimpleDateFormat("EEE MMM dd yyyy");
            try {
                Date tempCourseStart = dtf1.parse(courseStartDate);
                Date tempCourseEnd = dtf1.parse(courseEndDate);
                courseStartDate = dtf2.format(tempCourseStart);
                courseEndDate= dtf2.format(tempCourseEnd);
                courseText.setText(courseTitle);
                startText.setText(courseStartDate);
                endText.setText(courseEndDate);
                statusText.setText(courseStatus);
                mentors = showMentor();
                ArrayAdapter<String> mentorListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
                mentorListAdapter.addAll(mentors);
                mentorList = (ListView) findViewById(R.id.mentorListView);
                mentorList.setAdapter(mentorListAdapter);
            }
            catch(ParseException e){
                e.printStackTrace();
            }

        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, MentorProvider.CONTENT_URI,
                null,  null,null , null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mentorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mentorAdapter.swapCursor(null);
    }
}

