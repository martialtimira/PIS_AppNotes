package com.example.minscreennotepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    public List<Note> noteList;
    private NoteListAdapter noteListAdapter;

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
                Toast.makeText(this, "Logout selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.delete:
                Toast.makeText(this, "delete     selected", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void init() {
        noteList = new ArrayList<>();
        noteList.add(new NoteText("NotaTexto1", "Hola me llamo Martí"));
        noteList.add(new NoteAudio("NotaAudio1", "audio1.mp3"));
        noteList.add(new NoteImage("NotaImagen1", "imagen1.png"));
        noteListAdapter = new NoteListAdapter(noteList, this, this);
        RecyclerView noteRecyclerView = findViewById(R.id.noteRView);
        noteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteRecyclerView.setAdapter(noteListAdapter);

    }

    public void addNoteButtonClick(View view) {
        noteList.add(new NoteText("NotaTexto13", "Hola me lslamo Martí"));
        noteListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNoteClick(int position) {
        Note noteSelected = noteList.get(position);
        System.out.println(noteSelected.getTitle());
        if(noteSelected instanceof NoteImage) {
            Toast.makeText(this, noteSelected.getTitle() + ": " + ((NoteImage) noteSelected).getFileName(), Toast.LENGTH_LONG).show();
        }
        else if(noteSelected instanceof  NoteAudio) {
            Toast.makeText(this, noteSelected.getTitle() + ": " + ((NoteAudio) noteSelected).getFileName(), Toast.LENGTH_LONG).show();
        }
        else if(noteSelected instanceof NoteText){
            Toast.makeText(this, noteSelected.getTitle() + ": " + ((NoteText) noteSelected).getBody(), Toast.LENGTH_LONG).show();
        }
    }
}