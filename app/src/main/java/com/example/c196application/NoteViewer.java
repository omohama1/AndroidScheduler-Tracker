package com.example.c196application;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class NoteViewer extends AppCompatActivity {

    private static final String APP_TAG = "C196Application";
    String action;
    EditText editor;
    String noteFilter;
    String oldText;
    Uri takenPhotoUri;
    ImageView imageView;
    Bitmap image;
    byte[] imageByte;
    Uri uri;
    String photoFileName;
    String courseID;
    private ShareActionProvider mSAP;
    File photo;
    public final static int CAPTURE_PHOTO_REQUEST_CODE = 7001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            courseID = extras.getString("COURSE_ID");
            System.out.println("This is the course ID: " + courseID);
        }
        editor = (EditText) findViewById(R.id.editText);

        Intent intent = getIntent();
        imageView = (ImageView) findViewById(R.id.noteImage);
        uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.ALL_NOTE_COLUMNS, noteFilter, null, null);
            cursor.moveToFirst();
            byte[] imageByte = cursor.getBlob(cursor.getColumnIndex(DBOpenHelper.NOTE_IMAGE));
            if (imageByte != null) {
                image = getImage(imageByte);
                imageView.setImageBitmap(image);
            } else {
                Toast.makeText(NoteViewer.this, uri.getLastPathSegment(), Toast.LENGTH_LONG).show();
                String noteStr = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
                System.out.println(noteStr);

            }
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            editor.setText(oldText);
            editor.requestFocus();

            takenPhotoUri = getTakenPhotoUri();
            photo = new File(Environment.getExternalStorageDirectory() + "/" + photoFileName);
            Picasso.with(imageView.getContext()).load(photo).into(imageView);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:
                deleteNote();
                break;
        }

        return true;
    }

    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI,
                noteFilter, null);
        Toast.makeText(this, getString(R.string.note_deleted),
                Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void finishEditing() {
        String newText = editor.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote(newText);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0) {
                    deleteNote();
                } else if (oldText.equals(newText)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(newText);
                }

        }
        finish();
    }

    private void updateNote(String noteText) {
        ContentValues values = new ContentValues();

        if (image != null) {
            imageByte = getImageBytes(image);
        } else {
        }

        values.put(DBOpenHelper.NOTE_IMAGE, imageByte);
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, getString(R.string.note_updated), Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();

        if (image != null) {
            imageByte = getImageBytes(image);
        } else {
        }
        values.put(DBOpenHelper.NOTE_COURSE, courseID);
        values.put(DBOpenHelper.NOTE_IMAGE, imageByte);
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        Toast.makeText(this, "Note created", Toast.LENGTH_SHORT).show();
        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }
/*
    private void insertImage(byte[] image) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_IMAGE_FILE, image);
        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        setResult(RESULT_OK);

//        Toast.makeText(NoteViewer.this,uri.getLastPathSegment(),Toast.LENGTH_LONG).show();
    }*/



    public void onLaunchCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(uri!=null) {
            photoFileName = courseID + uri.getLastPathSegment() + "_photo.jpg";
            photo = new File(Environment.getExternalStorageDirectory() + "/" + photoFileName);
            takenPhotoUri = getTakenPhotoUri();
            //Attempting to use Picasso to update picture
            Picasso.with(imageView.getContext()).invalidate(photo);
            Picasso.with(imageView.getContext()).invalidate(takenPhotoUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, takenPhotoUri);
            startActivityForResult(intent, CAPTURE_PHOTO_REQUEST_CODE);
        }
        else {
            Toast.makeText(this,"Note must be created and saved before a picture can be added.",Toast.LENGTH_LONG).show();

        }







    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = takenPhotoUri;
            getContentResolver().notifyChange(selectedImage, null);

            imageView = (ImageView) findViewById(R.id.noteImage);
            ContentResolver cr = getContentResolver();

            try {
                image = MediaStore.Images.Media.getBitmap(cr, selectedImage);
                imageView.setImageBitmap(image);
                InputStream input = cr.openInputStream(selectedImage);
                imageByte = getImageBytes(image);
                //   byte[] imageInput = getBytes(input);

                // insertImage(imageInput);
                Toast.makeText(NoteViewer.this, "Image successfully taken", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Convert bitmap to bytes
    //Partially from http://www.coderzheaven.com/2012/12/23/store-image-android-sqlite-retrieve-it/
    private static byte[] getImageBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    //Convert byte array to bitmap
    private static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public Uri getTakenPhotoUri() {
        if (uri != null) {
            photoFileName = courseID + uri.getLastPathSegment() + "_photo.jpg";
            photo = new File(Environment.getExternalStorageDirectory() + "/" + photoFileName);
            takenPhotoUri = Uri.fromFile(photo);
            return takenPhotoUri;

        } else {
            Toast.makeText(this, "Note must be created and saved before a picture can be added.", Toast.LENGTH_LONG).show();
        }
        takenPhotoUri=null;
        return takenPhotoUri;
    }

    /*
    Sends message containing note image and note text.
    onClick method that takes view as a parameter
    //https://guides.codepath.com/android/Sharing-Content-with-Intents
    */
    public void emailNote(View view) {
        if(uri.getLastPathSegment()!=null) {
            takenPhotoUri = getTakenPhotoUri();
            System.out.println(takenPhotoUri.toString());
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_STREAM, takenPhotoUri);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Course note");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Message body: " + editor.getText());
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            emailIntent.setType("*/*");
            startActivity(Intent.createChooser(emailIntent, "Share your note..."));
        }
        else{
            Toast.makeText(NoteViewer.this,"You must make sure your note is saved before sending",Toast.LENGTH_SHORT);

        }
    }

}

