package com.example.newmynotesapp;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newmynotesapp.adapter.NoteAdapter;
import com.example.newmynotesapp.db.DatabaseContract;
import com.example.newmynotesapp.db.NoteHelper;
import com.example.newmynotesapp.entity.Note;
import com.example.newmynotesapp.helper.MappingHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoadNotesCallback{

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private FloatingActionButton fabAdd;

    public static final String EXTRA_STATE = "EXTRA_STATE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("notes");
        }
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.rv_notes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new NoteAdapter(this);
        recyclerView.setAdapter(adapter);


        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NoteAddUpdateActivity.class);
                startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_ADD);
            }
        });
        HandlerThread handlerThread = new HandlerThread("DataObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        DataObserver observer = new DataObserver(handler, this);
        getContentResolver().registerContentObserver(DatabaseContract.NoteColumns.CONTENT_URI, true, observer);


        if (savedInstanceState == null) {
            //proses data di ambil
            new LoadNotesAsync(this, this).execute();
        }else {
            ArrayList<Note> list = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if (list != null) {
                adapter.setListNote(list);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            //akan di panggil jika request add
            if (requestCode == NoteAddUpdateActivity.REQUEST_ADD) {
                if (resultCode == NoteAddUpdateActivity.RESULT_ADD) {
                    Note note = data.getParcelableExtra(NoteAddUpdateActivity.EXTRA_NOTE);

                    adapter.addItem(note);
                    recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);

                    showSnackbarMessage("satu item berhasil di tambahkan");
                }
            } else if (requestCode == NoteAddUpdateActivity.REQUEST_UPDATE) {
                if (resultCode == NoteAddUpdateActivity.RESULT_UPDATE) {
                    Note note = data.getParcelableExtra(NoteAddUpdateActivity.EXTRA_NOTE);
                    int position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0);

                    adapter.updateItem(position, note);
                    recyclerView.smoothScrollToPosition(position);

                    showSnackbarMessage("satu item berhasil di ubah");
                } else if (resultCode == NoteAddUpdateActivity.RESULT_DELETE) {
                    int position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0);

                    adapter.removeItem(position);

                    showSnackbarMessage("datu item berhasil di hapus");
                }
            }
        }
    }
    private void showSnackbarMessage(String message){
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private static class LoadNotesAsync extends AsyncTask<Void, Void, ArrayList<Note>>{
        private final WeakReference<Context> weakContext;
        private final WeakReference<LoadNotesCallback> loadNotesCallback;

        private LoadNotesAsync(Context context, LoadNotesCallback callback){
            weakContext = new WeakReference<>(context);
            loadNotesCallback = new WeakReference<>(callback);
        }
        @Override
        protected ArrayList<Note> doInBackground(Void... voids) {
            Context context = weakContext.get();
            Cursor data = context.getContentResolver().query(DatabaseContract.NoteColumns.CONTENT_URI, null, null, null, null);
            return MappingHelper.mapCursorToArrayList(data);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadNotesCallback.get().preExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<Note> notes) {
            super.onPostExecute(notes);
            loadNotesCallback.get().postExecute(notes);
        }
    }

    @Override
    public void preExecute() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void postExecute(ArrayList<Note> notes) {
        progressBar.setVisibility(View.INVISIBLE);
        if (notes.size() > 0){
            adapter.setListNote(notes);
        }
        else {
            adapter.setListNote(new ArrayList<Note>());
            showSnackbarMessage("tidak ada data saat ini");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, adapter.getListNotes());
    }
    public static class DataObserver extends ContentObserver{
        final Context context;
        public DataObserver(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new LoadNotesAsync(context, (LoadNotesCallback) context).execute();
        }
    }
}
