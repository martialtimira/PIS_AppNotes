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

public class TextViewActivity extends AppCompatActivity {

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
                showBackDialog();
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

        if(!viewModel.isValidTitle(noteTitle.getText().toString()) &&
                !noteTitle.getText().toString().equals(note.getTitle())) {
            sameTitleDialog();
        }
        else if(noteTitle.getText().toString().isEmpty()){
            nullTitleDialog();
        }
        else {
            DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance();
            databaseAdapter.saveChangesNoteText(noteTitle.getText().toString(),noteText.getText().toString(), note.getId());

            note.setTitle(noteTitle.getText().toString());
            note.setBody(noteText.getText().toString());
            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
        }
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
        NoteText note = (NoteText)viewModel.getNoteToView();

        alert.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance();
                //Eliminamos la nota en Firebase
                databaseAdapter.deleteNoteText(note.getId());
                //Eliminamos la nota a nivel local
                viewModel.DeleteNoteToView();
                Toast.makeText(TextViewActivity.this, "Nota eliminada", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(TextViewActivity.this, "Operación Cancelada", Toast.LENGTH_SHORT).show();
            }
        });

        alert.create().show();
    }

    /*
     *
     * @param item
     * Mensaje de confirmación para eliminar una nota
     */
    public void showBackDialog() {
        EditText noteTitle = (EditText) findViewById(R.id.textView_titleText);
        EditText noteText = (EditText) findViewById(R.id.text_view_text);
        NoteText note = (NoteText)viewModel.getNoteToView();

        //Si hay algun cambio en la nota, damos la opción de guardarlo
        if(!noteTitle.getText().toString().equals(note.getTitle()) || !noteText.getText().toString().equals(note.getBody())) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Confirmación");
            alert.setTitle("¿Quieres guardar los cambios?");

            alert.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!viewModel.isValidTitle(noteTitle.getText().toString()) &&
                            !noteTitle.getText().toString().equals(note.getTitle())) {
                        //cambiar a dialog
                        sameTitleDialog();
                    }
                    else if (noteTitle.getText().toString().isEmpty()) {
                        //cambiar a dialog
                        nullTitleDialog();
                    }
                    else if(noteText.getText().toString().length() > 10000) {
                        //cambiar a dialog
                        tooLongDialog();
                    }
                    else{
                        note.setTitle(noteTitle.getText().toString());
                        note.setBody(noteText.getText().toString());
                        Toast.makeText(TextViewActivity.this, "Cambios guardados", Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    }
                }
            });

            alert.setNegativeButton("No guardar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(TextViewActivity.this, "Cambios no guardados", Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                }
            });

            alert.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(TextViewActivity.this, "Acción cancelada", Toast.LENGTH_SHORT).show();
                }
            });

            alert.create().show();
        }
        //Si no hay cambios, volvemos directamente a MainActivity
        else{
            goToMainActivity();
        }
    }

    private void sameTitleDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error.");
        alert.setTitle("El título ya está en uso.");

        alert.setPositiveButton("Aceptar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }

    private void tooLongDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error.");
        alert.setTitle("La nota supera los 10.000 carácteres");

        alert.setPositiveButton("Aceptar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }

    private void nullTitleDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error.");
        alert.setTitle("El título está vacío.");

        alert.setPositiveButton("Aceptar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }

}
