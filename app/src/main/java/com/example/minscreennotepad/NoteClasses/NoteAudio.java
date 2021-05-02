package com.example.minscreennotepad.NoteClasses;

public class NoteAudio extends Note {

    long fileLenght; // Duracio en segons
    String filePath; //file path

    public NoteAudio(String title, String filePath, long fileLenght) {
        super(title);
        this.filePath = filePath;
        this.fileLenght = fileLenght;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileLenght(){return fileLenght;}

    public void setFileLenght(long fileLenght ){this.fileLenght = fileLenght;}
}
