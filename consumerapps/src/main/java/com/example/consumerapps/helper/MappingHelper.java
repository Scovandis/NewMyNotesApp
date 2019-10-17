package com.example.consumerapps.helper;

import android.database.Cursor;

import com.example.consumerapps.entity.Note;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.example.consumerapps.db.DatabaseContract.NoteColumns.DATE;
import static com.example.consumerapps.db.DatabaseContract.NoteColumns.DESCRIPTION;
import static com.example.consumerapps.db.DatabaseContract.NoteColumns.TITLE;


public class MappingHelper {

    public static ArrayList<Note> mapCursorToArrayList(Cursor cursor){
        ArrayList<Note> notes = new ArrayList<>();

        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DATE));
            notes.add(new Note(id, title, description, date));
        }
        return notes;
    }
    public static Note mapCursorToObject(Cursor noteCursor){
        noteCursor.moveToFirst();
        int id = noteCursor.getInt(noteCursor.getColumnIndexOrThrow(_ID));
        String title = noteCursor.getString(noteCursor.getColumnIndexOrThrow(TITLE));
        String description = noteCursor.getString(noteCursor.getColumnIndexOrThrow(DESCRIPTION));
        String date = noteCursor.getString(noteCursor.getColumnIndexOrThrow(DATE));

        return new Note(id, title, description, date);
    }
}
