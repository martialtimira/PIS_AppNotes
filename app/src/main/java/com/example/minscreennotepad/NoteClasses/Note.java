package com.example.minscreennotepad.NoteClasses;

import java.util.Objects;

public class Note {

    String title;

    public Note(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return title.equals(note.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
