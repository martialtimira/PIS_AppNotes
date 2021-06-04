package com.example.minscreennotepad.NoteClasses;

import com.example.minscreennotepad.DatabaseAdapter;

import java.util.Objects;

public class Note {

    String title;
    String id;
    final DatabaseAdapter adapter = DatabaseAdapter.databaseAdapter;

    /**
     * constructor de note
     * @param title Título de la nota
     */
    public Note(String title) {
        this.title = title;
    }

    /**
     * Getter del título
     * @return  String con el título
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter del título
     * @param title String del nuevo título
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Metodo equals para comparar 2 objetos Note
     * @param o Objeto a comparar
     * @return  true si son iguales, false si no lo són
     */
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
