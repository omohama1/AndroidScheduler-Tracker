package com.example.c196application;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by user on 7/11/2017.
 */

public class MentorProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.c196application.mentorprovider";
    private static final String BASE_PATH = "student";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // Constant to identify the requested operation
    private static final int MENTORS = 1;
    private static final int MENTORS_ID = 2;

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "mentor";
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, MENTORS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH +  "/#", MENTORS_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {

        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if (uriMatcher.match(uri) == MENTORS_ID) {
            selection = DBOpenHelper.MENTOR_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(DBOpenHelper.TABLE_MENTORS, DBOpenHelper.ALL_MENTOR_COLUMNS,
                selection, null, null, null,
                DBOpenHelper.MENTOR_ID);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(DBOpenHelper.TABLE_MENTORS,
                null, values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(DBOpenHelper.TABLE_MENTORS, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_MENTORS,
                values, selection, selectionArgs);
    }
}
