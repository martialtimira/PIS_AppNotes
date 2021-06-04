package com.example.minscreennotepad.NoteClasses;

import android.util.Log;

import com.example.minscreennotepad.DatabaseAdapter;

public class NoteText extends Note {

    String body;


    /**
     * Constructor de NoteText
     * @param title String con el titulo de la Note
     * @param body  String con el texto de NoteText
     */
    public NoteText(String title, String body) {
        super(title);
        this.body = body;
        id = title;
    }

    /**
     * Constructor de NoteText
     * @param title String con el titulo de la Note
     * @param body  String con el texto de NoteText
     * @param id  String con el id de NoteText
     */
    public NoteText(String title, String body, String id) {
        super(title);
        this.body = body;
        this.id = id;
    }

    /**
     * Getter del texto de la nota
     * @return String con el texto de la nota
     */
    public String getBody() {
        return body;
    }

    /**
     * Getter del id de la nota
     * @return String con el id de la nota
     */
    public String getId() {
        return id;
    }

    /**
     * Setter del texto de la nota
     * @param body String con el nuevo texto de la nota
     */
    public void setBody(String body) {
        this.body = body;
    }

    public void saveNote() {
        Log.d("saveCard", "saveCard-> saveDocument");
        adapter.saveNoteText(this.title, this.body, this.id);
    }

}
