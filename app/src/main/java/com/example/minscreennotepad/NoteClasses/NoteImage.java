package com.example.minscreennotepad.NoteClasses;

public class NoteImage extends Note{

    String fileName;

    public NoteImage(String title, String fileName) {
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

