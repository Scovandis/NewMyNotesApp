package com.example.newmynotesapp.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Switch;

import androidx.annotation.NonNull;

import com.example.newmynotesapp.db.NoteHelper;

import static com.example.newmynotesapp.db.DatabaseContract.AUTHORITY;
import static com.example.newmynotesapp.db.DatabaseContract.NoteColumns.CONTENT_URI;
import static com.example.newmynotesapp.db.DatabaseContract.TABLE_NAME;

public class MyContentProvider extends ContentProvider {
    private static final int NOTE = 1;
    private static final int NOTE_ID = 2;
    private NoteHelper noteHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /*
   Uri matcher untuk mempermudah identifier dengan menggunakan integer
   misal
   uri com.dicoding.picodiploma.mynotesapp dicocokan dengan integer 1
   uri com.dicoding.picodiploma.mynotesapp/# dicocokan dengan integer 2
    */
    static {


        //content://com.helloWorld.myNote//note
        sUriMatcher.addURI(AUTHORITY, TABLE_NAME, NOTE);

        // content://com.dicoding.picodiploma.mynotesapp/note/id
        sUriMatcher.addURI(AUTHORITY,
                TABLE_NAME + "/#",
                NOTE_ID);
    }

    public MyContentProvider() {
    }

    @Override
    public boolean onCreate() {
        noteHelper = NoteHelper.getInstance(getContext());
        noteHelper.open();
        return true;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)){
            case NOTE :
                cursor = noteHelper.queryAll();
                break;
            case NOTE_ID :
                cursor = noteHelper.queryById(uri.getLastPathSegment());
                break;
            default:
                cursor = null;
                break;
        }
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int delete;
        switch (sUriMatcher.match(uri)){
            case NOTE_ID:
                delete = noteHelper.deleteById(uri.getLastPathSegment());
                break;
            default:
                delete = 0;
                break;
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return delete;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long added1;
        switch (sUriMatcher.match(uri)){
            case NOTE :
                added1 = noteHelper.insert(values);//ask
            default:
                added1 = 0;
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return Uri.parse(CONTENT_URI + "/" +added1);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int updated;
        switch (sUriMatcher.match(uri)){
            case NOTE_ID :
                updated = noteHelper.update(uri.getLastPathSegment(), values);
                break;
            default:
                updated = 0;
                break;
        }
        getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        return updated;
    }
}
