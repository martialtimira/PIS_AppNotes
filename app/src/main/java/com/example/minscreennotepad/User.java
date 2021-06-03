package com.example.minscreennotepad;

import com.example.minscreennotepad.NoteClasses.Note;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String userName, password;
    private ArrayList<Note> noteList;

    /**
     * Constructor de User
     * @param userName  String con el nombre del usuario
     * @param password  String con la contraseña asignada al usuario
     */
    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.noteList = new ArrayList<>();
    }

    /**
     * Getter del nombre del usuario
     * @return String con el nombre del usuario
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Setter del nombre del usuario
     * @param userName String con el nuevo nomrbe del usuario
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Getter de la contraseña del usuario
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter de la contraseña del usuario
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter de la lista de notas del usuario
     * @return Lista de notas
     */
    public ArrayList<Note> getNoteList() {
        return noteList;
    }

    /**
     * Setter de la lista de notas del usuario
     * @param noteList Nueva lista de notas
     */
    public void setNoteList(ArrayList<Note> noteList) {
        this.noteList = noteList;
    }
}
