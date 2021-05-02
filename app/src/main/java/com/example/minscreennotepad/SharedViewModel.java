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

    private User loggedInUser;
    public List<Note> noteList;
    private CarteraUsuaris carteraUsuaris;
    private Note noteToView;
    public static final String TAG = "ViewModel";

    /**
     * Constructor de SharedViewModel
     */
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

    /**
     * Getter del usuario logueado
     * @return User logueado
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Setter de loggedInUser
     * @param loggedInUser nuevo usuario logueado
     */
    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    /**
     * Encuentra una nota a partir de su posición en la lista
     * @param pos int de la posición de la nota
     * @return Nota encontrada
     */
    public Note getNoteByPosition(int pos){
        return noteList.get(pos);
    }

    /**
     * Getter de noteList
     * @return Lista de notas
     */
    public List<Note> getNoteList(){
        return noteList;
    }

    /**
     * Setter de noteList
     * @param noteList nueve lista de notas
     */
    public void setNoteList(List<Note> noteList) {
        this.noteList=noteList;
    }

    /**
     * Setter de noteToView a partir de su posición en la lista
     * @param notePosition int de la posición de la nota
     */
    public void setNoteToView(int notePosition) {
        this.noteToView=noteList.get(notePosition);
    }

    /**
     * Getter de noteToView
     * @return Nota de noteToView
     */
    public Note getNoteToView() {
        return this.noteToView;
    }

    /**
     * Intenta iniciar sesión con un usuario a partir de su nombre y contraseña
     * @param userName String con el nombre de usuario
     * @param password String con la Contraseña
     * @return String con el estado del login
     */
    public String loginUser(String userName, String password) {
        String returnStatement = "Inicio de sesión correcto.";
        User user = carteraUsuaris.find(userName);
        if(user != null) {
            if (user.getPassword().equals(password)){
                loggedInUser = user;
                noteList = user.getNoteList();
            }
            else {
                returnStatement = "Usuario/contraseña incorrectos.";
            }
        }
        else {
            returnStatement = "Usuario no existe.";
        }
        return returnStatement;
    }

    /**
     * Intenta registrar un nuevo usuario a partir de un nombre y contraseña
     * @param userName String del nombre del usuario
     * @param passwrod String de la contraseña del usuario
     * @return String del estado del registro
     */
    public String signUpUser(String userName, String passwrod) {
        if(carteraUsuaris.signUpUser(new User(userName, passwrod))) {
            return "Usuario registrado.";
        }
        else{
            return "El nombre de usuario ya existe.";
        }
    }

    /**
     * Elimina la noteToView de la lista de notas
     */
    public void DeleteNoteToView() {
        this.noteList.remove(noteToView);
    }

    /**
     * Añade una nota de texto a la noteList a partir de su título y texto
     * @param title String del título de la nota
     * @param text String del texto de la nota
     */
    public void addTextNote(String title, String text){
        // Creamos nota de texto a partir de los datos pasados como parametros
        NoteText textNote = new NoteText(title, text);
        // Añadimos nota de texto a la lista
        noteList.add(textNote);
    }

    /**
     * Verifica que el título de la nota sea válido
     * @param title String del título de la nota
     * @return true, si es válido, false, si ya hay una nota con ese título el la lista
     */
    public boolean isValidTitle(String title) {
        boolean isValid = true;
        for (Note n: noteList) {
            if(n.getTitle().equals(title)){
                isValid = false;
            }
        }
        return isValid;
    }

    /**
     * Añade una nota de imagen a la lista de notas
     * @param title String del título de la nota
     * @param image Uri de la imagen de la nota
     */
    public void addImageNote(String title, Uri image) {
        //Creamos nota de imagen a partir de los datos pasados como parametros
        NoteImage imageNote = new NoteImage(title, image);
        //Añadimos la nota de imagen a la lista
        noteList.add(imageNote);

    }

    /**
     * Añade una nota de audio a la lista de notas
     * @param title String del título de la nota
     * @param filePath String del path del archivo de audio
     * @param fileLength long de la longitud del archivo de audio
     */
    public void addAudioNote(String title, String filePath, long fileLength){
        // Creamos nota de audio a partir de los datos pasados como parametros
        NoteAudio audioNote = new NoteAudio(title, filePath, fileLength);
        // Añadimos nota de audio a la lista
        noteList.add(audioNote);
    }
}