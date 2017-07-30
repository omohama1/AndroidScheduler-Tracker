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

public class MentorViewer extends AppCompatActivity {
    private String action;
    private String mentorFilter;
    private TextView mentorText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(MentorProvider.CONTENT_ITEM_TYPE);
        mentorText = (TextView) findViewById(R.id.mentorView);

        if (uri == null) {
            action = Intent.ACTION_INSERT;

        } else {
            action = Intent.ACTION_EDIT;
            mentorFilter = DBOpenHelper.MENTOR_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.ALL_MENTOR_COLUMNS, mentorFilter, null, null);
            cursor.moveToFirst();

            String mentorName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_NAME));
            String phone = cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_PHONE));
            String email = cursor.getString(cursor.getColumnIndex(DBOpenHelper.MENTOR_EMAIL));


            mentorText.setText("Mentor: " + mentorName + "\n" +
                    "Phone: " + phone + "\n" +
                    "E-mail: " + email);



        }
    }
}
