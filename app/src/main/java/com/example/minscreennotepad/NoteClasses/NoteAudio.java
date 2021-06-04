package com.example.minscreennotepad.NoteClasses;

import android.util.Log;

import com.example.minscreennotepad.DatabaseAdapter;

public class NoteAudio extends Note {

    long fileLenght; // Duracio en segons
    String filePath; //file path


    /**
     * Constructor de NoteAudio
     * @param title     String del título
     * @param filePath  String del "path" del archivo
     * @param fileLenght    long de la longitud del archivo
     */
    public NoteAudio(String title, String filePath, long fileLenght) {
        super(title);
        this.filePath = filePath;
        this.fileLenght = fileLenght;
        id = title;
    }

    public NoteAudio(String title, String filePath, long fileLenght, String id) {
        super(title);
        this.filePath = filePath;
        this.fileLenght = fileLenght;
        this.id= id;
    }


    /**
     * Getter del "path" del archivo de audio
     * @return String con el "path"
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * setter del "path" del archivo de audio
     * @param filePath String con el "path"
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Getter de la longitud del archivo de audio
     * @return long con la longitud del archivo de audio
     */
    public long getFileLenght(){return fileLenght;}

    /**
     * setter de la longitud del archivo de audio
     * @param fileLenght long con la longitud del archivo de audio
     */
    public void setFileLenght(long fileLenght ){this.fileLenght = fileLenght;}

    /**
     * Getter del id de la nota
     * @return String con el id de la nota
     */
    public String getId() {
        return id;
    }

    public void saveAudioNote() {
        Log.d("saveCard", "saveCard-> saveDocument");
        adapter.saveNoteAudio(this.title, this.fileLenght, this.id);
    }
}

