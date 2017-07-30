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

public class AssessmentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.c196application.assessmentprovider";
    private static final String BASE_PATH = "student";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // Constant to identify the requested operation
    private static final int ASSESSMENTS = 1;
    private static final int ASSESSMENTS_ID = 2;

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "assessment";
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, ASSESSMENTS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH +  "/#", ASSESSMENTS_ID);
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

        if (uriMatcher.match(uri) == ASSESSMENTS_ID) {
            selection = DBOpenHelper.ASSESSMENT_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(DBOpenHelper.TABLE_ASSESSMENTS, DBOpenHelper.ALL_ASSESSMENT_COLUMNS,
                selection, null, null, null,
                DBOpenHelper.ASSESSMENT_ID);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(DBOpenHelper.TABLE_ASSESSMENTS,
                null, values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(DBOpenHelper.TABLE_ASSESSMENTS, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_ASSESSMENTS,
                values, selection, selectionArgs);
    }
}
