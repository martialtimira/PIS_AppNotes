package com.example.minscreennotepad.NoteClasses;

public class NoteText extends Note {

    String body;

    public NoteText(String title, String body) {
        super(title);
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
