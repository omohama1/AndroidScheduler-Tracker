package com.example.c196application;

/**
 * Created by Omar Mohamad on 7/6/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DBOpenHelper extends SQLiteOpenHelper {

    //Constants for db name and version
    private static final String DATABASE_NAME = "student.db";
    protected static final int DATABASE_VERSION = 18;

    //Constants for identifying table for Terms and columns
    public static final String TABLE_TERMS = "terms";
    public static final String TERM_ID = "_id";
    public static final String TERM_TITLE = "term_title";
    public static final String TERM_START = "term_start";
    public static final String TERM_END = "term_end";

    //Constants for identifying table for Courses and columns
    public static final String TABLE_COURSES = "courses";
    public static final String COURSE_ID = "_id";
    public static final String COURSE_TITLE = "course_title";
    public static final String COURSE_START = "course_start";
    public static final String COURSE_END = "course_end";
    public static final String COURSE_NOTES = "course_notes";
    public static final String COURSE_STATUS = "course_status";
    public static final String COURSE_TERM = "course_term";
    public static final String COURSE_MENTOR = "course_mentor";
    public static final String COURSE_ALERT = "course_alert";

    //Constants for identifying table for Assessments and columns
    public static final String TABLE_ASSESSMENTS = "assessments";
    public static final String ASSESSMENT_ID = "_id";
    public static final String ASSESSMENT_TITLE = "assessment_title";
    public static final String ASSESSMENT_DATE = "assessment_date";
    public static final String ASSESSMENT_TYPE = "assessment_type";
    public static final String ASSESSMENT_COURSE = "assessment_course";
    public static final String ASSESSMENT_ALERT = "assessment_alert";


    //Constants for identifying table for Mentors and columns
    public static final String TABLE_MENTORS = "mentors";
    public static final String MENTOR_ID = "_id";
    public static final String MENTOR_NAME = "mentor_name";
    public static final String MENTOR_PHONE = "mentor_phone";
    public static final String MENTOR_EMAIL = "mentor_email";

    //Constants for course mentor table
    public static final String TABLE_CMS = "course_mentors";
    public static final String CM_ID = "_id";
    public static final String MENTOR="mentor_id";
    public static final String COURSE = "course_id";

    //Constants for identifying table for Notes
    public static final String TABLE_NOTES = "notes";
    public static final String NOTE_ID = "_id";
    public static final String NOTE_TEXT = "note_text";
    public static final String NOTE_COURSE = "note_course";
    public static final String NOTE_IMAGE_FILE = "file_name";
    public static final String NOTE_IMAGE = "note_image";


    public static final String[] ALL_TERM_COLUMNS =
            {TERM_ID, TERM_TITLE, TERM_START, TERM_END};

    public static final String[] ALL_COURSE_COLUMNS =
            {COURSE_ID, COURSE_TITLE, COURSE_START, COURSE_END,COURSE_MENTOR,
                    COURSE_TERM,COURSE_STATUS,COURSE_NOTES, COURSE_ALERT};


    public static final String[] ALL_MENTOR_COLUMNS=
            {MENTOR_ID,MENTOR_NAME,MENTOR_EMAIL,MENTOR_PHONE};

    public static final String[] ALL_ASSESSMENT_COLUMNS=
            {ASSESSMENT_ID,ASSESSMENT_TITLE,ASSESSMENT_COURSE,
                    ASSESSMENT_TYPE,ASSESSMENT_DATE,ASSESSMENT_ALERT};

    public static final String[] ALL_NOTE_COLUMNS={
            NOTE_ID,
            NOTE_TEXT,
            NOTE_COURSE,
            NOTE_IMAGE_FILE,
            NOTE_IMAGE
    };



    //SQL to create Terms table
    private static final String TERM_TABLE_CREATE =
            "CREATE TABLE " + TABLE_TERMS + " (" +
                    TERM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TERM_TITLE + " TEXT, " +
                    TERM_START + " TEXT default CURRENT_TIMESTAMP," +
                    TERM_END + " TEXT default CURRENT_TIMESTAMP);" +
                    ");";

    //SQL to create Course table
    private static final String COURSE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_COURSES + " (" +
                    COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COURSE_TITLE + " TEXT, " +
                    COURSE_START + " TEXT default CURRENT_TIMESTAMP," +
                    COURSE_END + " TEXT default CURRENT_TIMESTAMP," +
                    COURSE_NOTES + " TEXT, " +
                    COURSE_STATUS + " TEXT, " +
                    COURSE_TERM + " INTEGER, " +
                    COURSE_ALERT + " INTEGER, "+
                    COURSE_MENTOR + " INTEGER, " +
                    "FOREIGN KEY (" + COURSE_TERM + ") REFERENCES "
                    + TABLE_TERMS + "(" + TERM_ID + "), " +

                    "FOREIGN KEY (" + COURSE_MENTOR + ") REFERENCES "
                    + TABLE_MENTORS + "(" + MENTOR_ID + "));";

    //SQL to create Assessment table
    private static final String ASSESSMENT_TABLE_CREATE =
            "CREATE TABLE " + TABLE_ASSESSMENTS + " (" +
                    ASSESSMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ASSESSMENT_TITLE + " TEXT, " +
                    ASSESSMENT_DATE + " TEXT default CURRENT_TIMESTAMP," +
                    ASSESSMENT_TYPE + " TEXT, " +
                    ASSESSMENT_ALERT + " INTEGER, "+
                    ASSESSMENT_COURSE + " INTEGER, FOREIGN KEY (" + ASSESSMENT_COURSE + ") REFERENCES " +
                    TABLE_COURSES + "(" + COURSE_ID + "));";

    //SQL to create Mentor table
    private static final String MENTOR_TABLE_CREATE =
            "CREATE TABLE " + TABLE_MENTORS + " (" +
                    MENTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MENTOR_NAME + " TEXT, " +
                    MENTOR_PHONE + " TEXT, " +
                    MENTOR_EMAIL + " TEXT);";

    //SQL to create Notes table
    private static final String NOTE_TABLE_CREATE=
            "CREATE TABLE " + TABLE_NOTES+ " (" +
                    NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NOTE_TEXT + " TEXT, " +
                    NOTE_IMAGE_FILE + " TEXT, "+
                    NOTE_IMAGE + " BLOB," +
                    NOTE_COURSE + " INTEGER, FOREIGN KEY (" + NOTE_COURSE + ") REFERENCES " +
                    TABLE_COURSES + "(" + COURSE_ID + "));";


    private static final String COURSE_MENTOR_TABLE_CREATE=
            "CREATE TABLE " + TABLE_CMS+ " ("+
                    CM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    MENTOR + " INTEGER, " +
                    COURSE + " INTEGER, " +
                    " FOREIGN KEY (" + MENTOR + ") REFERENCES " +
                    TABLE_MENTORS + "(" + MENTOR_ID + "),"+
                    " FOREIGN KEY (" + COURSE + ") REFERENCES " +
                    TABLE_COURSES + "(" + COURSE_ID + "));";


    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(TERM_TABLE_CREATE);
        db.execSQL(COURSE_TABLE_CREATE);
        db.execSQL(ASSESSMENT_TABLE_CREATE);
        db.execSQL(MENTOR_TABLE_CREATE);
        db.execSQL(NOTE_TABLE_CREATE);
        db.execSQL(COURSE_MENTOR_TABLE_CREATE);
      //   db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERMS);
        db.execSQL(("DROP TABLE IF EXISTS " + TABLE_COURSES));
        db.execSQL(("DROP TABLE IF EXISTS " + TABLE_MENTORS));
        db.execSQL(("DROP TABLE IF EXISTS " + TABLE_ASSESSMENTS));
        db.execSQL(("DROP TABLE IF EXISTS " + TABLE_NOTES));
        db.execSQL(("DROP TABLE IF EXISTS " + TABLE_CMS));
        onCreate(db);
      //  db.close();

    }

    //ADD TERM METHOD
    public void addTerm(Term term) {
        ContentValues values = new ContentValues();
        values.put(TERM_TITLE, term.getTermTitle());
        values.put(TERM_START, term.getStartDate().toString());
        values.put(TERM_END, term.getEndDate().toString());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_TERMS, null, values);
        db.close();

    }

    //ADD COURSE METHOD
    public void addCourse(Course course) {
        ContentValues values = new ContentValues();
        values.put(COURSE_TITLE, course.getCourseTitle());
        values.put(COURSE_START, course.getStartDate().toString());
        values.put(COURSE_END, course.getEndDate().toString());
        values.put(COURSE_TERM, course.getTermID());
        values.put(COURSE_STATUS,course.getStatus());
        values.put(COURSE_MENTOR,course.getMentor());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_COURSES, null, values);
        db.close();
    }

    Course getCourse(int id){
        SimpleDateFormat dtf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_COURSES, new String[] {
                COURSE_ID, COURSE_TITLE,COURSE_TERM,COURSE_START,
                COURSE_END,COURSE_STATUS,COURSE_ALERT}, COURSE_ID + "=?",
                new String[] {String.valueOf(id)},null,null,null,null);
        if(cursor!=null) {
            cursor.moveToFirst();

            try {
                String title = cursor.getString(1);
                int term = cursor.getInt(2);
                int courseID = cursor.getInt(0);
                Date start = dtf.parse(cursor.getString(3));
                Date end = dtf.parse(cursor.getString(4));
                String status = cursor.getString(5);
                int alert = cursor.getInt(6);
                Course course = new Course(title,status,start,end,term,alert);
                course.setCourseID(courseID);
                return course;
            }
            catch(ParseException e){
                e.printStackTrace();
            }
        }
        return null;
    }
    public Cursor getCourseAlarmCursor(){
        String selectQuery = "SELECT * FROM " + TABLE_COURSES + " WHERE " + COURSE_ALERT + "= 1" ;//+" ORDER BY " + COURSE_ID;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery,null);
    }
    public Cursor getAssessAlarmCursor(){
        String selectQuery = "SELECT * FROM " + TABLE_ASSESSMENTS + " WHERE " + ASSESSMENT_ALERT + "= 1";
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery,null);
    }

    public Cursor getTermsCursor(){
        String selectQuery = "SELECT * FROM " + TABLE_TERMS;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery,null);
    }public Cursor getMentorsCursor(){
        String selectQuery = "SELECT * FROM " + TABLE_MENTORS;
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery,null);
    }


    public void updateCourse(Course course){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COURSE_TITLE, course.getCourseTitle());
        values.put(COURSE_START,course.getStartDate().toString());
        values.put(COURSE_END, course.getEndDate().toString());
        values.put(COURSE_STATUS,course.getStatus());
        values.put(COURSE_MENTOR,course.getMentor());
        values.put(COURSE_ALERT,course.getAlert());

         db.update(TABLE_COURSES,values, COURSE_ID+" =?",new String[]
                {String.valueOf(course.getCourseID())});
        db.close();
    }

    //ADD COURSE MENTOR METHOD
    public void addCourseMentor(int courseID, int mentorID){
        ContentValues values = new ContentValues();
        values.put(MENTOR,mentorID);
        values.put(COURSE,courseID);
        String[] redundant =  {String.valueOf(mentorID),String.valueOf(courseID)};
        SQLiteDatabase db = getWritableDatabase();
        String query1 ="SELECT " + MENTOR + "," + COURSE + " FROM " + TABLE_CMS + " WHERE " + MENTOR  + " = "
                +mentorID + " AND " + COURSE + " = "+ courseID;
        Cursor cursor = db.rawQuery(query1,null);
        cursor.moveToFirst();
        if(cursor.isAfterLast()) {
            db.insert(TABLE_CMS,null,values);
        }
        else
            {
               db.delete(TABLE_CMS,MENTOR +" !=? AND " + COURSE +"=?",redundant);

            }
        //db.insert(TABLE_CMS,null,values);
        db.close();
    }

    //ADD MENTOR METHOD
    public void addMentor(Mentor mentor){
        ContentValues values = new ContentValues();
        values.put(MENTOR_NAME, mentor.getMentorName());
        values.put(MENTOR_EMAIL, mentor.getEmailAdddress());
        values.put(MENTOR_PHONE, mentor.getPhoneNumber());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_MENTORS, null, values);
        db.close();
    }

    //ADD ASSESSMENT METHOD
    public void addAssessment(Assessment assessment){
        ContentValues values = new ContentValues();
        values.put(ASSESSMENT_TITLE, assessment.getAssessmentTitle());
        values.put(ASSESSMENT_DATE, assessment.getAssessmentDate().toString());
        values.put(ASSESSMENT_TYPE, assessment.getAssessmentType());
        values.put(ASSESSMENT_COURSE, assessment.getAssessmentCourseID());
        values.put(ASSESSMENT_ALERT,assessment.getAlert());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_ASSESSMENTS, null, values);
        db.close();
    }




    //print out term as string
    public String termToString() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TERMS;// + " ORDER BY " + TERM_START;

        //Cursor points to location in results.
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("term_title")) != null) {
                dbString += c.getString(c.getColumnIndex("term_title")) + " "
                        + c.getString(c.getColumnIndex("term_start")) + " " +
                        c.getString(c.getColumnIndex("term_end"));
                dbString += "\n";
            }
            c.moveToNext();

        }
        db.close();
        return dbString;
    }

    //toString method for Mentor
    public String mentorToString() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MENTORS;

        //Cursor points to location in results.
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("mentor_name")) != null) {
                dbString += c.getString(c.getColumnIndex("mentor_name"));
                dbString += "\n";
            }
            c.moveToNext();

        }
        db.close();
        return dbString;
    }

    //toString method for Course
    public String courseToString() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_COURSES;

        //Cursor points to location in results.
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("course_title")) != null) {
                dbString += c.getString(c.getColumnIndex("course_title"));
                dbString += "\n";
            }
            c.moveToNext();

        }
        db.close();
        return dbString;
    }

    public void insertImage (byte[] imageBytes){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOTE_IMAGE, imageBytes);
        db.insert(TABLE_NOTES,null,values);

    }


    //toString methdod for Assessment
    public String assessmentToString() {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_ASSESSMENTS;

        //Cursor points to location in results.
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("assessment_title")) != null) {
                dbString += c.getString(c.getColumnIndex("assessment_title"));
                dbString += "\n";
            }
            c.moveToNext();

        }
        db.close();
        return dbString;
    }

    public void updateAssessment(Assessment assessment) {
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ASSESSMENT_TITLE, assessment.getAssessmentTitle());
        values.put(ASSESSMENT_TYPE, assessment.getAssessmentType());
        values.put(ASSESSMENT_DATE,assessment.getAssessmentDate().toString());
        values.put(ASSESSMENT_COURSE,assessment.getAssessmentCourseID());
        values.put(ASSESSMENT_ALERT,assessment.getAlert());

        db.update(TABLE_ASSESSMENTS,values, ASSESSMENT_ID+" =?",new String[]
                {String.valueOf(assessment.getAssessmentID())});
        db.close();
    }
}
