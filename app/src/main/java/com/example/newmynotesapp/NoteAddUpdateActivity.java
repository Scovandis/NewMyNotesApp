package com.example.newmynotesapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newmynotesapp.db.NoteHelper;
import com.example.newmynotesapp.entity.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.newmynotesapp.db.DatabaseContract.NoteColumns.DATE;
import static com.example.newmynotesapp.db.DatabaseContract.NoteColumns.DESCRIPTION;
import static com.example.newmynotesapp.db.DatabaseContract.NoteColumns.TITLE;


public class NoteAddUpdateActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText edtTitle, edtDescription;
    private Button submit;

    private boolean isEdit = false;
    private Note note;
    private int position;
    private NoteHelper noteHelper;

    public static final String EXTRA_NOTE = "extra_note";
    public static final String EXTRA_POSITION = "extra_position";
    public static final int REQUEST_ADD = 100;
    public static final int RESULT_ADD = 101;
    public static final int REQUEST_UPDATE = 200;
    public static final int RESULT_UPDATE = 201;
    public static final int RESULT_DELETE = 301;
    private final int ALERT_DIALOG_CLOSE = 10;
    private final int ALERT_DIALOG_DELETE = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add_update);

        edtTitle = findViewById(R.id.edt_title);
        edtDescription = findViewById(R.id.description);
        submit = findViewById(R.id.btn_submit);

        noteHelper = NoteHelper.getInstance(getApplicationContext());

        note = getIntent().getParcelableExtra(EXTRA_NOTE);
        if (note != null) {
            position = getIntent().getIntExtra(EXTRA_POSITION, 0);
            isEdit = true;
        }
        else {
            note = new Note();
        }

        String actionBar;
        String btnTitle;

        if (isEdit){
            actionBar = "ubah";
            btnTitle = "update";
            if (note != null) {
                edtTitle.setText(note.getTitle());
                edtDescription.setText(note.getDescription());
            }
        }else {
            actionBar = "tambah";
            btnTitle = "simpan";
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(actionBar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        submit.setText(btnTitle);

        submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_submit){
            String title = edtTitle.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();


            if (TextUtils.isEmpty(title)){
                edtTitle.setError("Field can not be blank");
                return;
            }

            note.setTitle(title);
            note.setDescription(description);

            Intent mIntent = new Intent();
            mIntent.putExtra(EXTRA_NOTE, note);
            mIntent.putExtra(EXTRA_POSITION, position);

            ContentValues value = new ContentValues();
            value.put(TITLE, title);
            value.put(DESCRIPTION, description);
            value.put(DATE, getCurrentDate());

            if (isEdit) {
                long result = noteHelper.update(String.valueOf(note.getId()), value); //cuma butuh 1 parameter karena nambah data cukup isikan datanya tidak perlu pake id keuali id nya bukan auto increment
                if (result > 0) {
                    setResult(RESULT_UPDATE, mIntent);
                    finish();
                } else {
                    Toast.makeText(NoteAddUpdateActivity.this, "Gagal mengupdate data", Toast.LENGTH_SHORT).show();
                }
            }else {
                note.setDate(getCurrentDate());
                value.put(DATE, getCurrentDate());
                long result = noteHelper.insert(value);
                System.out.println(result);
                System.out.println(value);
                if (result > 0) {
                    note.setId((int) result);
                    setResult(RESULT_ADD, mIntent);
                    finish();
                } else {
                    Toast.makeText(NoteAddUpdateActivity.this, "Gagal menambah data", Toast.LENGTH_SHORT).show();
                }


            }
        }
    }


    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit) {
            getMenuInflater().inflate(R.menu.menu_from, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete :
                showAlertDialog(ALERT_DIALOG_DELETE);
                break;
            case R.id.home :
                showAlertDialog(ALERT_DIALOG_CLOSE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE);
    }
    private void showAlertDialog(int type){
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage;

        if (isDialogClose){
            dialogTitle = "batal";
            dialogMessage = "äpakah anda yakin ingin membatalkan perubahan pada from?";
        }
        else {
            dialogMessage = "apakah anda yakin ingin menghapus item ini?";
            dialogTitle = "hapus note";
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isDialogClose) {
                            fileList();
                        }else {
                            long result = noteHelper .deleteById(String.valueOf(note.getId()));
                            if (result > 0) {
                                Intent mIntent = new Intent();
                                mIntent.putExtra(EXTRA_POSITION, position);
                                setResult(RESULT_DELETE, mIntent);
                                finish();
                            }
                            else {
                                Toast.makeText(NoteAddUpdateActivity.this, "gagal menghapus data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
