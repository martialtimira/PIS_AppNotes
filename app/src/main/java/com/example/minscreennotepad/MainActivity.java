package com.example.minscreennotepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteListAdapter.OnNoteListener {


    private NoteListAdapter noteListAdapter;
    private SharedViewModel viewModel;
    private boolean userChanged;


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
            case R.id.delete:
                Toast.makeText(this, "delete     selected", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void init() {
        viewModel = SharedViewModel.getInstance();
        if(viewModel.getLoggedInUser() == null) {
            goToLoginActivity();
        }
        userChanged = false;
        noteListAdapter = new NoteListAdapter(viewModel.getNoteList(), this, this);
        RecyclerView noteRecyclerView = findViewById(R.id.noteRView);
        noteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteRecyclerView.setAdapter(noteListAdapter);
    }

    @Override
    protected void onResume() {
        if(viewModel.getLoggedInUser() == null) {
            goToLoginActivity();
        }
        if(userChanged) {
            noteListAdapter.setItems(viewModel.getNoteList());
        }
        super.onResume();
    }

    private void logout() {
        viewModel.setLoggedInUser(null);
        goToLoginActivity();
    }
    public void addNoteButtonClick(View view) {
        viewModel = SharedViewModel.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("¿Que tipo de nota quieres crear?");
        builder.setItems(new CharSequence[]
                        {"Texto", "Audio", "Imagen", "Cancelar"},
                (dialog, which) -> {
                    // The 'which' argument contains the index position
                    // of the selected item
                    switch (which) {
                        case 0:
                            goToTextCreatorActivity();
                            break;
                        case 1:
                            Toast.makeText(view.getContext(), "Audio", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            goToImageCreatorActivity();
                            break;
                        case 3:
                            Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT).show();
                            break;
                    }
                });
        builder.create().show();

    }

    //Basic explicit intent to textCreatorActivity without extra data
    private void goToImageCreatorActivity() {
        Intent intent = new Intent(this, imageCreatorActivity.class);
        startActivity(intent);
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        userChanged = true;
        startActivity(intent);
    }

    private void goToImageViewActivity() {
        Intent intent = new Intent(this, imageViewActivity.class);
        startActivity(intent);
    }

    //Basic explicit intent to textCreatorActivity without extra data
    public void goToTextCreatorActivity(){
        Intent intent = new Intent(this, textCreatorActivity.class);
        startActivity(intent);
    }

    //Basic explicit intent to textCreatorActivity without extra data
    public void goToTextViewActivity(){
        Intent intent = new Intent(this, textViewActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNoteClick(int position) {
        //Note noteSelected = noteList.get(position);
        Note noteSelected = viewModel.getNoteByPosition(position);
        if(noteSelected instanceof NoteImage) {
            viewModel.setNoteToView(position);
            goToImageViewActivity();
        }
        else if(noteSelected instanceof  NoteAudio) {
            Toast.makeText(this, noteSelected.getTitle() + ": " + ((NoteAudio) noteSelected).getFileName(), Toast.LENGTH_LONG).show();
        }
        else if(noteSelected instanceof NoteText){
            viewModel.setNoteToView(position);
            goToTextViewActivity();
        }
    }

}