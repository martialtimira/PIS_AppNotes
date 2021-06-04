package com.example.minscreennotepad;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.minscreennotepad.NoteClasses.Note;
import com.example.minscreennotepad.NoteClasses.NoteImage;
import com.example.minscreennotepad.NoteClasses.NoteText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DatabaseAdapter{

    private volatile static DatabaseAdapter uniqueInstance;
    public static final String TAG = "DatabaseAdapter";
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;

    public static vmInterface listener;
    public static DatabaseAdapter databaseAdapter;

    public DatabaseAdapter(){
        databaseAdapter = this;
        FirebaseFirestore.setLoggingEnabled(true);
    }

    public static DatabaseAdapter getInstance() {
        if (uniqueInstance == null) {
            synchronized (SharedViewModel.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new DatabaseAdapter();
                }
            }
        }
        return uniqueInstance;
    }

    public void setListener(vmInterface listener){
        this.listener = listener;
        initFirebase();
    }

    public interface vmInterface{
        void setCollection(ArrayList<Note> noteList);
        void setToast(String s);
    }

    public void initFirebase(){

        user = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        if (user == null) {
            mAuth.signInAnonymously() // Fer amb user y password
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInAnonymously:success");
                                listener.setToast("Authentication successful.");
                                user = mAuth.getCurrentUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInAnonymously:failure", task.getException());
                                listener.setToast("Authentication failed.");

                            }
                        }
                    });
        }
        else{
            listener.setToast("Authentication with current user.");
            Log.d(TAG, "Authentication with current user.");

        }
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public FirebaseUser getUser() {
        return this.user;
    }

    public void getCollection(){
        Log.d(TAG,"updateNotes");
        DatabaseAdapter.db.collection(user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            ArrayList<Note> retrieved_noteList = new ArrayList<Note>() ;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                if (document.get("noteType").equals("text") ) {
                                    retrieved_noteList.add(new NoteText(document.getString("title"), document.getString("body"), document.getId()));
                                }
                                else if (document.get("noteType").equals("image") ) {
                                    //downlaoad URI here
                                    NoteImage noteImage = new NoteImage(document.getString("title"), null, document.getId());
                                    downloadImage(document.getString("body"), noteImage);
                                    retrieved_noteList.add(noteImage);
                                }
                                else if (document.get("noteType").equals("audio")) {
                                    //retrieved_noteList.add(new NoteAudio(document.getString("title"), document.getString("body")));
                                }
                            }
                            listener.setCollection(retrieved_noteList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void downloadImage(String imageAdress, NoteImage noteImage) {

        StorageReference fileRef = storageReference.child(imageAdress);
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "Download URL: " + uri.toString());
                noteImage.setFile(uri);
            }
        });
    }

    public void deleteImageFromStorage(String imageAdress) {
        StorageReference fileRef = storageReference.child(imageAdress);
        fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: Img deleted");
            }
        });
    }

    public void saveNoteText (String title, String body, String id) {
        // Create a new user with a first and last name
        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        note.put("body", body);
        note.put("noteType", "text");
        // Add a new document with a generated ID
        db.collection(user.getEmail()).document(id).set(note);
    }

    public void saveChangesNoteText (String title, String body, String id) {
        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        note.put("body", body);
        note.put("noteType", "text");
        // Update data of already existing document
        db.collection(user.getEmail()).document(id).update(note);
    }

    public void deleteNoteText (String id) {
        // Delete an existing document
        db.collection(user.getEmail()).document(id).delete();
    }

    public void saveNoteImage (String title, String id) {
        // Create a new user with a first and last name
        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        String imageAdress = (user.getUid() + "/" + id);
        note.put("body", imageAdress);
        note.put("noteType", "image");
        // Add a new document with a generated ID
        db.collection(user.getEmail()).document(id).set(note);
    }

    public void saveChangesNoteImage (String title, String id) {
        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        String imageAdress = (user.getUid() + "/" + id);
        note.put("body", imageAdress);
        note.put("noteType", "image");
        // Update data of already existing document
        db.collection(user.getEmail()).document(id).update(note);
    }

    public void deleteNoteImage (String id) {
        // Delete an existing document
        db.collection(user.getEmail()).document(id).delete();
        String imageAdress = user.getUid() + "/" + id;
        deleteImageFromStorage(imageAdress);
    }
}




