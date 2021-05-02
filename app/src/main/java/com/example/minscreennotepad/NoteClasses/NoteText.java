package com.example.minscreennotepad.NoteClasses;

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
    }

    /**
     * Getter del texto de la nota
     * @return String con el texto de la nota
     */
    public String getBody() {
        return body;
    }

    /**
     * Setter del texto de la nota
     * @param body String con el nuevo texto de la nota
     */
    public void setBody(String body) {
        this.body = body;
    }
}
