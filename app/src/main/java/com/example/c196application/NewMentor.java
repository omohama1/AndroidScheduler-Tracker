package com.example.c196application;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class NewMentor extends AppCompatActivity {
    EditText nameText;
    EditText phoneText;
    EditText emailText;
    DBOpenHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_mentor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = new DBOpenHelper(this, null, null, DBOpenHelper.DATABASE_VERSION);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameText = (EditText) findViewById(R.id.mentorNameText);
        phoneText = (EditText) findViewById(R.id.mentorPhoneText);
        emailText = (EditText) findViewById(R.id.mentorEmailText);
    }

    public String fetchName() {

        String nameStr = nameText.getText().toString();
        if (nameStr.isEmpty() || nameStr == null) {
            Toast.makeText(NewMentor.this, "You must enter your course mentor's name", Toast.LENGTH_SHORT).show();
            nameStr = null;
        } else {
            nameStr = nameText.getText().toString();
        }
        return nameStr;
    }

    public String fetchPhone() {

        String phoneStr = phoneText.getText().toString();
        if (phoneStr.isEmpty() || phoneStr == null) {
            Toast.makeText(NewMentor.this, "You must enter your course mentor's phone number", Toast.LENGTH_SHORT).show();
            phoneStr = null;
        } else {
            phoneStr = phoneText.getText().toString();
        }
        return phoneStr;
    }
    private boolean checkDuplicateMentor(){
        boolean duplicate = false;
        String subMentor = fetchName();
        String dbString = "";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "SELECT * FROM " + dbHelper.TABLE_MENTORS;
        //Cursor points to location in results.
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("mentor_name")) != null) {
                String mentor= c.getString(c.getColumnIndex("mentor_name"));
                if(mentor.equals(subMentor)) {
                    duplicate=true;
                    return duplicate;
                }
                else {
                    c.moveToNext();
                }
            }
        }

        return duplicate;
    }

    public String fetchEmail() {

        String emailStr = emailText.getText().toString();
        if (emailStr.isEmpty() || emailStr == null) {
            Toast.makeText(NewMentor.this, "You must enter your course mentor's e-mail address", Toast.LENGTH_SHORT).show();
            emailStr = null;
        } else {
            emailStr = emailText.getText().toString();
        }
        return emailStr;
    }

    public void createNewMentor(View view) {

        String nameStr = fetchName();
        String phoneStr = fetchPhone();
        String emailStr = fetchEmail();

        Mentor mentor = new Mentor(nameStr, phoneStr, emailStr);
        if(checkDuplicateMentor()){
            Toast.makeText(NewMentor.this,"That mentor already exists",Toast.LENGTH_SHORT).show();
        }
        else {
            if (nameStr != null && phoneStr != null && emailStr != null) {

                String mentorStr = nameStr + " " + phoneStr + " " +
                        emailStr;
                Toast.makeText(NewMentor.this, mentorStr, Toast.LENGTH_SHORT).show();
                dbHelper.addMentor(mentor);
                Toast.makeText(NewMentor.this, "Mentor added", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(NewMentor.this, "Missing data", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
