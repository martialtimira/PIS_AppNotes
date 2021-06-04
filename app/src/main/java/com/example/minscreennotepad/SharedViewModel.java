package com.example.minscreennotepad;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minscreennotepad.NoteClasses.Note;
import com.example.minscreennotepad.NoteClasses.NoteAudio;
import com.example.minscreennotepad.NoteClasses.NoteImage;
import com.example.minscreennotepad.NoteClasses.NoteText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends androidx.lifecycle.ViewModel implements DatabaseAdapter.vmInterface {

    private volatile static SharedViewModel uniqueInstance;

    private User loggedInUser;
    private List<Note> noteList;
    private CarteraUsuaris carteraUsuaris;
    private Note noteToView;
    private final MutableLiveData<String> mToast;
    public static final String TAG = "ViewModel";
    private NoteListAdapter noteListAdapter;
    private DatabaseAdapter da;

    /**
     * Constructor de SharedViewModel
     */
    private SharedViewModel(){
        noteList = new ArrayList<Note>();

        carteraUsuaris = new CarteraUsuaris();
        mToast = new MutableLiveData<>();
        da = DatabaseAdapter.getInstance();
        da.setListener(this);
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

    public void setNoteListAdapter(NoteListAdapter noteListAdapter){
        this.noteListAdapter = noteListAdapter;
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
     * Getter de noteList
     * @return Lista de notas
     */
    public List<Note> getNoteListLiveData(){
        return noteList;
    }

    /**
     * Setter de noteList
     * @param noteList nueve lista de notas
     */
    public void setNoteList(ArrayList<Note> noteList) {
        this.noteList = noteList;
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
                da.signIn(userName,password);
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
     * @param password String de la contraseña del usuario
     * @return String del estado del registro
     */
    public String signUpUser(String userName, String password) {

        if(carteraUsuaris.signUpUser(new User(userName, password))) {
            da.createAccount(userName, password);
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

    public void addTextoNoteToFireBase(NoteText noteText){
        noteText.saveNote();
    }

    public void addImageNoteToFireBase(NoteImage noteImage) {
        noteImage.saveImageNote();
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
        addTextoNoteToFireBase(textNote);
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
        addImageNoteToFireBase(imageNote);
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

    public LiveData<String> getToast(){
        return mToast;
    }

    public void setDBUser(FirebaseUser user) {
        da.setUser(user);
    }

    //communicates user inputs and updates the result in the viewModel
    @Override
    public void setCollection(ArrayList<Note> noteList) {
        this.noteList.clear();
        for (Note note : noteList) {
            this.noteList.add(note);
        }
        //this.noteList.setValue(noteList);
        noteListAdapter.notifyDataSetChanged();
    }

    public void setToast(String t) {
        mToast.setValue(t);
    }

}