package com.example.consumerapps;

import com.example.consumerapps.entity.Note;

import java.util.ArrayList;

interface LoadNotesCallback {
    void preExecute();
    void postExecute(ArrayList<Note> notes);

}
