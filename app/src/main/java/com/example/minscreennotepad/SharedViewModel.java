package com.example.minscreennotepad;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.minscreennotepad.NoteClasses.Note;
import com.example.minscreennotepad.NoteClasses.NoteAudio;
import com.example.minscreennotepad.NoteClasses.NoteImage;
import com.example.minscreennotepad.NoteClasses.NoteText;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends androidx.lifecycle.ViewModel {

    private volatile static SharedViewModel uniqueInstance;

    //Lista de notas
    private Usuari loggedInUser;
    public List<Note> noteList;
    private List<Usuari> userList;
    private Note noteToView;
    public static final String TAG = "ViewModel";

    //Constructor
    private SharedViewModel(){
        noteList = new ArrayList<>();
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

    // Asignamos la lista de notas a la que nos pasan
    public void setNoteToView(int notePosition) {
        this.noteToView=noteList.get(notePosition);
    }

    // Asignamos la lista de notas a la que nos pasan
    public Note getNoteToView() {
        return this.noteToView;
    }

    // Asignamos la lista de notas a la que nos pasan
    public void DeleteNoteToView() {
        this.noteList.remove(noteToView);
    }

    // Añadir nota de texto a la lista
    public void addTextNote(String title, String text){
        // Creamos nota de texto a partir de los datos pasados como parametros
        NoteText textNote = new NoteText(title, text);
        // Añadimos nota de texto a la lista
        noteList.add(textNote);
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

    public void addImageNote(String title, Uri image) {
        //Creamos nota de imagen a partir de los datos pasados como parametros
        NoteImage imageNote = new NoteImage(title, image);
        //Añadimos la nota de imagen a la lista
        noteList.add(imageNote);

    }
    // Añadir nota de audio a la lista
    public void addAudioNote(String title, String filePath, long fileLenght){
        // Creamos nota de audio a partir de los datos pasados como parametros
        NoteAudio audioNote = new NoteAudio(title, filePath, fileLenght);
        // Añadimos nota de audio a la lista
        noteList.add(audioNote);
    }
}