package com.example.minscreennotepad.NoteClasses;

import android.graphics.Bitmap;
import android.net.Uri;

public class NoteImage extends Note{

    Uri file;

    public NoteImage(String title, Uri file) {
        super(title);
        this.file = file;
    }

    public Uri getFile() {
        return file;
    }

    public void setFile(Uri file) {
        this.file = file;
    }

    public void setBitmap(Bitmap file){

    }
}

