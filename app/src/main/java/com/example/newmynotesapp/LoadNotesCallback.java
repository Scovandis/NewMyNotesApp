package com.example.newmynotesapp;

import com.example.newmynotesapp.entity.Note;

import java.util.ArrayList;

interface LoadNotesCallback {
    void preExecute();
    void postExecute(ArrayList<Note> notes);

}
