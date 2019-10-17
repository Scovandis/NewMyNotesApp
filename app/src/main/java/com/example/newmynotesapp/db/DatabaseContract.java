package com.example.newmynotesapp.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {
    public static final String AUTHORITY = "com.helloWorld.myNotes";
    private static final String SCHEME = "content";

    public static String TABLE_NAME = "note";

    public static final class NoteColumns implements BaseColumns{
        public static String TITLE = "title";
        public static String DESCRIPTION = "description";
        public static String DATE = "date";


        //untuk membuat URL
        public static final Uri CONTENT_URI = new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

    }
}
