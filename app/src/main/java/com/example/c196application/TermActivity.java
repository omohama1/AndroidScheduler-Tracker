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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class TermActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ADD_TERM_REQUEST_CODE = 2001;
    private static final int VIEWER_REQUEST_CODE = 2002 ;
    private ArrayList<String> terms;
    private DBOpenHelper dbHelper;
    private ListView termList;
    private TermCursorAdapter termAdapter;
    private SQLiteDatabase db;
    private Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        uri = intent.getParcelableExtra(TermProvider.CONTENT_ITEM_TYPE);
        setContentView(R.layout.activity_term);
        termAdapter = new TermCursorAdapter(this, null, 0);
        dbHelper = new DBOpenHelper(this, null, null, DBOpenHelper.DATABASE_VERSION);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTerm(view);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        printDatabase();

        termList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(TermActivity.this, TermViewer.class);
                Uri uri = Uri.parse(TermProvider.CONTENT_URI + "/" + id);
                intent.putExtra(TermProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, VIEWER_REQUEST_CODE);
              //  goToViewer();
            }
        });
        getLoaderManager().initLoader(0, null, this);

    }

    public void goToViewer(){
       Intent goToTermViewer = new Intent(TermActivity.this, TermViewer.class);
       int termID = Integer.parseInt(uri.getLastPathSegment());
       goToTermViewer.putExtra("TERM_ID",termID);
       startActivityForResult(goToTermViewer, VIEWER_REQUEST_CODE);

   }

    public void addTerm(View view) {
        Intent intent = new Intent(this, NewTerm.class);
        startActivityForResult(intent, ADD_TERM_REQUEST_CODE);
    }

    public void printDatabase() {
        termList = (ListView) findViewById(R.id.termsList);
        termList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        termList.setAdapter(termAdapter);
        terms = new ArrayList<>();
        String dbString = dbHelper.termToString();
        terms.add(dbString);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, TermProvider.CONTENT_URI,
                null, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        termAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        termAdapter.swapCursor(null);

    }
}
