package com.example.minscreennotepad.NoteClasses;

import android.graphics.Bitmap;
import android.net.Uri;

public class NoteImage extends Note{

    Uri file;

    /**
     * Constructor de NoteImage
     * @param title String con el titulo de la Note
     * @param file  Uri que contiene el archivo de la imagen
     */
    public NoteImage(String title, Uri file) {
        super(title);
        this.file = file;
    }

    /**
     * Getter del Uri de la imagen
     * @return Uri de la imagen
     */
    public Uri getFile() {
        return file;
    }

    /**
     * Setter del Uri de la imagen
     * @param file Uri de la nueva imagen
     */
    public void setFile(Uri file) {
        this.file = file;
    }
}

