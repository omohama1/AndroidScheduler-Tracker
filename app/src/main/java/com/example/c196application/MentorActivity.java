package com.example.c196application;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class MentorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ADD_MENTOR_REQUEST_CODE = 4001;
    private static final int VIEWER_REQUEST_CODE = 4002 ;

    private ArrayList<String> mentors;
    private DBOpenHelper dbHelper;
    private ListView mentorList;
    private MentorCursorAdapter mentorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor);
        mentorAdapter = new MentorCursorAdapter(this,null,0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMentor(view);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dbHelper = new DBOpenHelper(this, null, null, DBOpenHelper.DATABASE_VERSION);
        printDatabase();
        mentorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MentorActivity.this, MentorViewer.class);
                Uri uri = Uri.parse(MentorProvider.CONTENT_URI + "/" + id);
                intent.putExtra(MentorProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, VIEWER_REQUEST_CODE);
            }
        });
        getLoaderManager().initLoader(0, null, this);

    }

    public void addMentor(View view) {
        Intent intent = new Intent(this, NewMentor.class);
        startActivityForResult(intent, ADD_MENTOR_REQUEST_CODE);
    }

    public void printDatabase() {
        mentorList = (ListView) findViewById(R.id.mentorList);
        mentorList.setAdapter(mentorAdapter);
        mentors = new ArrayList<>();
        String dbString = dbHelper.mentorToString();
        mentors.add(dbString);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, MentorProvider.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mentorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mentorAdapter.swapCursor(null);
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == ADD_MENTOR_REQUEST_CODE ||requestCode==VIEWER_REQUEST_CODE) && resultCode == RESULT_OK) {
            restartLoader();
        }
    }
}

