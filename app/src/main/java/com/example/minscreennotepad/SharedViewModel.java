package com.example.minscreennotepad;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minscreennotepad.NoteClasses.Note;
import com.example.minscreennotepad.NoteClasses.NoteAudio;
import com.example.minscreennotepad.NoteClasses.NoteImage;
import com.example.minscreennotepad.NoteClasses.NoteText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends androidx.lifecycle.ViewModel implements DatabaseAdapter.vmInterface {

    private volatile static SharedViewModel uniqueInstance;

    private boolean userLoggedIn;
    private List<Note> noteList;
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
        userLoggedIn = false;
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
    public void addAudioNoteToFireBase(NoteAudio noteAudio) {
        noteAudio.saveAudioNote();
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
        String fileAdress = (da.getUser().getUid() + "/" + title);
        uploadImage(fileAdress, image);
    }

    public void uploadImage(String adress, Uri file) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = storageReference.child(adress);
        fileRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadURL = taskSnapshot.getUploadSessionUri();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
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
        addAudioNoteToFireBase(audioNote);
        String fileAdress = (da.getUser().getUid() + "/" + title);
        uploadAudio(fileAdress, filePath);
    }

    public void uploadAudio(String adress, String filePath) {
        Uri file = Uri.fromFile(new File(filePath));
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = storageReference.child(adress);
        fileRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadURL = taskSnapshot.getUploadSessionUri();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    public LiveData<String> getToast(){
        return mToast;
    }
    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }
    public void setUserLoggedIn(boolean status) {
        userLoggedIn = status;
    }
    public void setDBUser(FirebaseUser user) {
        da.setUser(user);
    }
    public FirebaseUser getDBUser() {
        return da.getUser();
    }
    public void refreshNotes() {
        da.getCollection();
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