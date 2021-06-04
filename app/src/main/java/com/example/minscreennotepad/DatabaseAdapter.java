package com.example.minscreennotepad;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.minscreennotepad.NoteClasses.Note;
import com.example.minscreennotepad.NoteClasses.NoteAudio;
import com.example.minscreennotepad.NoteClasses.NoteImage;
import com.example.minscreennotepad.NoteClasses.NoteText;
import com.google.android.gms.common.util.ScopeUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Continuation;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;


public class DatabaseAdapter{

    private volatile static DatabaseAdapter uniqueInstance;
    public static final String TAG = "DatabaseAdapter";
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    public String publicReturnStatement;

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
    public void createAccount(String email, String password){
        //String realEmail = email + "@gmail.com";
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    user = mAuth.getCurrentUser();
                }
                else{
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    listener.setToast("Authentication failed.");
                }
            }
        });
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            user = mAuth.getCurrentUser();
                            getCollection();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            listener.setToast("Authentication failed.");
                            user = null;
                            }
                        }
        });
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
                                    retrieved_noteList.add(new NoteImage(document.getString("title"), Uri.parse((String) document.get("body")), document.getId()));
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

    public void saveNoteImage (String title, String body, String id) {
        // Create a new user with a first and last name
        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        note.put("body", body);
        note.put("noteType", "image");
        // Add a new document with a generated ID
        db.collection(user.getEmail()).document(id).set(note);
    }

    public void saveChangesNoteImage (String title, String body, String id) {
        Map<String, Object> note = new HashMap<>();
        note.put("title", title);
        note.put("body", body);
        note.put("noteType", "image");
        // Update data of already existing document
        db.collection(user.getEmail()).document(id).update(note);
    }

    public void deleteNoteImage (String id) {
        // Delete an existing document
        db.collection(user.getEmail()).document(id).delete();
    }
}




