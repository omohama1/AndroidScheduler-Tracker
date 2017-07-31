package com.example.c196application;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class NotesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EDITOR_REQUEST_CODE = 6001;
    private static final int ADD_NOTE_REQUEST_CODE = 6002;
    private static final int BACK_TO_COURSE_CODE =6003 ;
    private ListView noteList;
    private NotesCursorAdapter noteAdapter;
    private ArrayList<String> notes;
    private DBOpenHelper dbHelper;
    private SQLiteDatabase db;
    private String courseID;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        uri = intent.getParcelableExtra(CourseProvider.CONTENT_ITEM_TYPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            courseID = extras.getString("COURSE_ID");
            System.out.println("This is the course ID: "+courseID);
        }
        dbHelper = new DBOpenHelper(this,null,null,DBOpenHelper.DATABASE_VERSION);
        db= dbHelper.getWritableDatabase();
        noteAdapter = new NotesCursorAdapter(this,null,0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        noteList = (ListView)findViewById(R.id.noteList);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNote(view);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        printDatabase();
        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(NotesActivity.this, NoteViewer.class);
                Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + id);
                intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
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
            Intent intent = new Intent(NotesActivity.this, CourseViewer.class);
            uri = Uri.parse(CourseProvider.CONTENT_URI + "/" + courseID);
           intent.putExtra(CourseProvider.CONTENT_ITEM_TYPE, uri);
            startActivityForResult(intent,BACK_TO_COURSE_CODE);
        }
    }
    public void addNote(View view) {
        Intent intent = new Intent(this, NoteViewer.class);
        intent.putExtra("COURSE_ID",courseID);
        startActivityForResult(intent, ADD_NOTE_REQUEST_CODE);
    }

    public void printDatabase() {

        noteList.setAdapter(noteAdapter);
        notes = new ArrayList<>();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this, NotesProvider.CONTENT_URI,
                null, dbHelper.NOTE_COURSE+"="+courseID, null, null);

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        noteAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        noteAdapter.swapCursor(null);

    }
}
