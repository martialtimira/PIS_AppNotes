package com.example.minscreennotepad;

import com.example.minscreennotepad.NoteClasses.Note;

import java.util.ArrayList;
import java.util.List;

public class Usuari {

    private String userName, password;
    private List<Note> noteList;

    public Usuari(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.noteList = new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Note> getNoteList() {
        return noteList;
    }

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
    }
}
