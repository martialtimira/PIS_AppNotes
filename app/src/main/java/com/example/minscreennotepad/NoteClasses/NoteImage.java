package com.example.minscreennotepad.NoteClasses;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.example.minscreennotepad.DatabaseAdapter;

public class NoteImage extends Note{

    Uri file;
    private final DatabaseAdapter adapter = DatabaseAdapter.databaseAdapter;

    /**
     * Constructor de NoteImage
     * @param title String con el titulo de la Note
     * @param file  Uri que contiene el archivo de la imagen
     */
    public NoteImage(String title, Uri file) {
        super(title);
        this.file = file;
        id = title;
    }

    /**
     * Constructor de NoteImage
     * @param title String con el titulo de la Note
     * @param file  Uri que contiene el archivo de la imagen
     */
    public NoteImage(String title, Uri file, String id) {
        super(title);
        this.file = file;
        this.id = id;
    }

    /**
     * Getter del Uri de la imagen
     * @return Uri de la imagen
     */
    public Uri getFile() {
        return file;
    }

    /**
     * Getter del id de la nota
     * @return String con el id de la nota
     */
    public String getId() {
        return id;
    }

    /**
     * Setter del Uri de la imagen
     * @param file Uri de la nueva imagen
     */
    public void setFile(Uri file) {
        this.file = file;
    }

    public void saveImageNote() {
        Log.d("saveCard", "saveCard-> saveDocument");
        adapter.saveNoteImage(this.title, this.id);
    }
}

