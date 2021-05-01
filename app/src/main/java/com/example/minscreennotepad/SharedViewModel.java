package com.example.minscreennotepad;

import android.net.Uri;

import com.example.minscreennotepad.NoteClasses.Note;
import com.example.minscreennotepad.NoteClasses.NoteImage;
import com.example.minscreennotepad.NoteClasses.NoteText;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends androidx.lifecycle.ViewModel {

    private volatile static SharedViewModel uniqueInstance;

    //Lista de notas
    private User loggedInUser;
    public List<Note> noteList;
    private CarteraUsuaris carteraUsuaris;
    private Note noteToView;
    public static final String TAG = "ViewModel";

    //Constructor
    private SharedViewModel(){
        noteList = new ArrayList<>();
        carteraUsuaris = new CarteraUsuaris();
    }

    public static SharedViewModel getInstance() {
        if (uniqueInstance == null) {
            synchronized (SharedViewModel.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new SharedViewModel();
                }
            }
        }
        return uniqueInstance;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    // Retorna la lista de notas
    public Note getNoteByPosition(int pos){
        return noteList.get(pos);
    }

    // Retorna la lista de notas
    public List<Note> getNoteList(){
        return noteList;
    }

    // Asignamos la lista de notas a la que nos pasan
    public void setNoteList(List<Note> noteList) {
        this.noteList=noteList;
    }

    public void setNoteToView(int notePosition) {
        this.noteToView=noteList.get(notePosition);
    }

    public Note getNoteToView() {
        return this.noteToView;
    }

    public void DeleteNoteToView() {
        this.noteList.remove(noteToView);
    }

    public String loginUser(String userName, String password) {
        String returnStatement = "Login Correcte";
        User user = carteraUsuaris.find(userName);
        if(user != null) {
            if (user.getPassword().equals(password)){
                loggedInUser = user;
                noteList = user.getNoteList();
            }
            else {
                returnStatement = "Usuari/contrasenya incorrectes";
            }
        }
        else {
            returnStatement = "Usuari no existeix";
        }
        return returnStatement;
    }

    public String signUpUser(String userName, String passwrod) {
        if(carteraUsuaris.signUpUser(new User(userName, passwrod))) {
            return "Usuari registrat";
        }
        else{
            return "Nom d'usuari ja existeix";
        }
    }

    // Añadir nota de texto a la lista
    public void addTextNote(String title, String text){
        // Creamos nota de texto a partir de los datos pasados como parametros
        NoteText textNote = new NoteText(title, text);
        // Añadimos nota de texto a la lista
        noteList.add(textNote);
    }

    public void addImageNote(String title, Uri image) {
        //Creamos nota de imagen a partir de los datos pasados como parametros
        NoteImage imageNote = new NoteImage(title, image);
        //Añadimos la nota de imagen a la lista
        noteList.add(imageNote);

    }

    public boolean isValidTitle(String title) {
        boolean isValid = true;
        for (Note n: noteList) {
            if(n.getTitle().equals(title)){
                isValid = false;
            }
        }
        return isValid;
    }
}