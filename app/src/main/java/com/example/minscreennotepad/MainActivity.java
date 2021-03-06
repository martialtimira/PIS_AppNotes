package com.example.minscreennotepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.minscreennotepad.NoteClasses.Note;
import com.example.minscreennotepad.NoteClasses.NoteAudio;
import com.example.minscreennotepad.NoteClasses.NoteImage;
import com.example.minscreennotepad.NoteClasses.NoteText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NoteListAdapter.OnNoteListener {

    private final int maxNumText = 100;
    private final int maxNumImage = 100;
    private final int maxNumAudio = 100;

    private Context parentContext;
    private AppCompatActivity mActivity;
    private NoteListAdapter noteListAdapter;
    private SharedViewModel viewModel;
    private RecyclerView noteRecyclerView;
    private SharedPreferences sharedpreferences;
    private static final String mypreference = "mypref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Inicializa los componentes de la MainActivity
     */
    public void init() {
        viewModel = SharedViewModel.getInstance();

        sharedpreferences = getApplicationContext().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        if(!viewModel.isUserLoggedIn()) {
            if (sharedpreferences.contains("email") && sharedpreferences.contains("password")){
                loadSharedPreferences();
                String email = viewModel.getDBUser().getEmail();
                int atIndex = email.indexOf("@");
                email = email.substring(0, atIndex);
                getSupportActionBar().setTitle("Notas de " + email);
                viewModel.refreshNotes();
            }
            else{
                this.finish();
                goToLoginActivity();
                String email = viewModel.getDBUser().getEmail();
                int atIndex = email.indexOf("@");
                email = email.substring(0, atIndex);
                getSupportActionBar().setTitle("Notas de " + email);
                viewModel.refreshNotes();
            }
        }

        String email = viewModel.getDBUser().getEmail();
        int atIndex = email.indexOf("@");
        email = email.substring(0, atIndex);
        getSupportActionBar().setTitle("Notas de " + email);
        viewModel.refreshNotes();

        parentContext = this.getBaseContext();
        mActivity = this;
        noteListAdapter = new NoteListAdapter(viewModel.getNoteList(), this, this);
        noteRecyclerView = findViewById(R.id.noteRView);
        noteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteRecyclerView.setAdapter(noteListAdapter);
        viewModel.setNoteListAdapter(noteListAdapter);
        viewModel.setParentContext(parentContext);
    }

    /**
     * Metodo que sirve para verificar que siempre hay un usuario logueado.
     */
    @Override
    protected void onResume() {
        sharedpreferences = getApplicationContext().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        if(!viewModel.isUserLoggedIn()) {
            if (sharedpreferences.contains("email") && sharedpreferences.contains("password")){
                loadSharedPreferences();
                String email = viewModel.getDBUser().getEmail();
                int atIndex = email.indexOf("@");
                email = email.substring(0, atIndex);
                getSupportActionBar().setTitle("Notas de " + email);
                viewModel.refreshNotes();
            }
            else{
                this.finish();
                goToLoginActivity();
            }
        }
        else {
            String email = viewModel.getDBUser().getEmail();
            int atIndex = email.indexOf("@");
            email = email.substring(0, atIndex);
            getSupportActionBar().setTitle("Notas de " + email);
            viewModel.refreshNotes();
        }

        super.onResume();
    }

    /**
     * Cierra sesi??n del usuario actual
     */
    private void logout() {
        viewModel.setDBUser(null);
        viewModel.setUserLoggedIn(false);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();

        goToLoginActivity();
    }

    public void loadSharedPreferences(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword((String)sharedpreferences.getAll().get("email"), (String)sharedpreferences.getAll().get("password"))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            viewModel.setDBUser(mAuth.getCurrentUser());
                            viewModel.setUserLoggedIn(true);
                        }
                        else {
                            goToLoginActivity();
                        }
                    }
                });
    }

    /**
     * Al clickar el bot??n de "+", muestra un di??logo al usuario para elegir el tipo de nota a crear
     * y lo redirecciona a la actividad pertinente.
     */
    public void addNoteButtonClick(View view) {
        viewModel = SharedViewModel.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("??Qu?? tipo de nota quieres crear?");
        builder.setItems(new CharSequence[]
                        {"Texto.", "Audio.", "Imagen."},
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            if(viewModel.getNumText()<maxNumText){
                                goToTextCreatorActivity();
                            }
                            else{
                                maxNumTextDialog();
                            }
                            break;
                        case 1:
                            if(viewModel.getNumAudio()<maxNumAudio){
                                goToAudioCreatorActivity();
                            }
                            else{
                                maxNumAudiotDialog();
                            }
                            break;
                        case 2:
                            if(viewModel.getNumImage()<maxNumImage){
                                goToImageCreatorActivity();
                            }
                            else{
                                maxNumImagetDialog();
                            }
                            break;
                        case 3:
                            Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT).show();
                            break;
                    }
                });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Acci??n cancelada", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();

    }

    /**
     * Navega a ImageCreatorActivity
     */
    private void goToImageCreatorActivity() {
        Intent intent = new Intent(this, ImageCreatorActivity.class);
        startActivity(intent);
    }

    /**
     * Navega a LoginActivity
     */
    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Navega a ImageViewActivity
     */
    private void goToImageViewActivity() {
        Intent intent = new Intent(this, ImageViewActivity.class);
        startActivity(intent);
    }

    /**
     * Navega a TextCreatorActivity
     */
    public void goToTextCreatorActivity(){
        Intent intent = new Intent(this, TextCreatorActivity.class);
        startActivity(intent);
    }

    /**
     * Navega a TextViewActivity
     */
    public void goToTextViewActivity(){
        Intent intent = new Intent(this, TextViewActivity.class);
        startActivity(intent);
    }

    /**
     * Navega a AudioCreatorActivity
     */
    public void goToAudioCreatorActivity(){
        Intent intent = new Intent(this, AudioCreatorActivity.class);
        startActivity(intent);
    }

    /**
     * Navega a AudioViewActivity
     */
    public void goToAudioViewActivity(){
        Intent intent = new Intent(this, AudioViewActivity.class);
        startActivity(intent);
    }

    /**
     * Al clickar en la card de una nota, navega a la actividad correspondiente para visualizarla
     */
    @Override
    public void onNoteClick(int position) {
        Note noteSelected = viewModel.getNoteByPosition(position);
        if(noteSelected instanceof NoteImage) {
            viewModel.setNoteToView(position);
            goToImageViewActivity();
        }
        else if(noteSelected instanceof  NoteAudio) {
            viewModel.setNoteToView(position);
            goToAudioViewActivity();
        }
        else if(noteSelected instanceof NoteText){
            viewModel.setNoteToView(position);
            goToTextViewActivity();
        }
    }

    private void maxNumTextDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error.");
        alert.setTitle("No puedes a??adir m??s notas de texto");
        alert.setPositiveButton("Aceptar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.create().show();
    }

    private void maxNumImagetDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error.");
        alert.setTitle("No puedes a??adir m??s notas de imagen");
        alert.setPositiveButton("Aceptar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.create().show();
    }

    private void maxNumAudiotDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error.");
        alert.setTitle("No puedes a??adir m??s notas de audio");
        alert.setPositiveButton("Aceptar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.create().show();
    }

}