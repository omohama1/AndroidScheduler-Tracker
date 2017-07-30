package com.example.c196application;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by user on 7/11/2017.
 */

public class CourseCursorAdapter extends CursorAdapter {
    public CourseCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.course_item, parent, false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String courseText = cursor.getString(
                cursor.getColumnIndex(DBOpenHelper.COURSE_TITLE));

        TextView tv = (TextView) view.findViewById(R.id.tvNote);
        tv.setText(courseText);
    }
}
