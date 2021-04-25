package com.example.minscreennotepad.NoteClasses;

public class NoteAudio extends Note {

    String fileName;

    public NoteAudio(String title, String fileName) {
        super(title);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
