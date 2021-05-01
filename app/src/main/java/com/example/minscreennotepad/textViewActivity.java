package com.example.minscreennotepad;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minscreennotepad.NoteClasses.NoteText;

public class textViewActivity extends AppCompatActivity {

    private SharedViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);

        viewModel = SharedViewModel.getInstance();

        getSupportActionBar().setTitle("Ver nota de texto");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setNote();
    }

    //Botón/flecha para regresar a la pantalla principal de la aplicación
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goToMainActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Basic explicit intent to MainActivity without extra data
    public void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.view_text_note_menu, menu);
        return true;
    }

    //Ponemos el título y texto de la nota
    public void setNote(){
        EditText noteTitle = (EditText) findViewById(R.id.textView_titleText);
        EditText noteText = (EditText) findViewById(R.id.text_view_text);

        NoteText note = (NoteText)viewModel.getNoteToView();

        noteTitle.setText(note.getTitle(), TextView.BufferType.EDITABLE);
        noteText.setText(note.getBody(), TextView.BufferType.EDITABLE);
    }

    //Guardar cambios nota de texto
    public void saveChangesTextNote(MenuItem item) {
        EditText noteTitle = (EditText) findViewById(R.id.textView_titleText);
        EditText noteText = (EditText) findViewById(R.id.text_view_text);

        NoteText note = (NoteText)viewModel.getNoteToView();

        note.setTitle(noteTitle.getText().toString());
        note.setBody(noteText.getText().toString());

        Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();

    }

    /*
     *
     * @param item
     * Mensaje de confirmación para eliminar una nota
     */
    public void showDeleteDialog (MenuItem item) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirmación");
        alert.setTitle("¿Seguro que quieres eliminar esta nota?");

        alert.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewModel.DeleteNoteToView();
                Toast.makeText(textViewActivity.this, "Nota eliminada", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(textViewActivity.this, "Operación Cancelada", Toast.LENGTH_SHORT).show();
            }
        });

        alert.create().show();
    }

}
