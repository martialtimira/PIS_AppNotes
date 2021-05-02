package com.example.minscreennotepad;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minscreennotepad.NoteClasses.NoteImage;
import com.example.minscreennotepad.NoteClasses.NoteText;

import java.io.ByteArrayOutputStream;

public class imageViewActivity extends AppCompatActivity {
    
    private SharedViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        
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
                showReturnDialog(android.R.id.home);
                //goToMainActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showReturnDialog(int item) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirmación.");
        alert.setTitle("¿Seguro que quieres salir?");

        alert.setPositiveButton("Salir.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToMainActivity();
            }
        });

        alert.setNegativeButton("Cancelar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(imageViewActivity.this, "Operación cancelada.", Toast.LENGTH_SHORT).show();
            }
        });

        alert.create().show();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.view_image_note_menu, menu);
        return true;
    }

    private void setNote() {
        EditText noteTitle = (EditText) findViewById(R.id.imageTitle);
        ImageView noteContent = (ImageView) findViewById(R.id.imageContent);

        NoteImage note = (NoteImage)viewModel.getNoteToView();

        noteTitle.setText(note.getTitle(), TextView.BufferType.EDITABLE);
        noteContent.setImageURI(note.getFile());
    }

    //Guardar cambios nota de texto
    public void saveChangesImageNote(MenuItem item) {
        EditText noteTitle = (EditText) findViewById(R.id.imageTitle);
        ImageView noteContent = findViewById(R.id.imageContent);

        NoteImage note = (NoteImage)viewModel.getNoteToView();
        if(!viewModel.isValidTitle(noteTitle.getText().toString())) {
            sameTitleDialog();
        }
        else if(noteTitle.getText().toString().isEmpty()){
            nullTitleDialog();
        }
        else {
            note.setTitle(noteTitle.getText().toString());
            goToMainActivity();
        }
    }

    /*
     *
     * @param item
     * Mensaje de confirmación para eliminar una nota
     */
    public void showDeleteDialog (MenuItem item) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirmación.");
        alert.setTitle("¿Seguro que quieres eliminar esta nota?");

        alert.setPositiveButton("Eliminar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewModel.DeleteNoteToView();
                Toast.makeText(imageViewActivity.this, "Nota eliminada.", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            }
        });

        alert.setNegativeButton("Cancelar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(imageViewActivity.this, "Operación cancelada.", Toast.LENGTH_SHORT).show();
            }
        });

        alert.create().show();
    }

    public void sameTitleDialog() {
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

    public void nullTitleDialog() {
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


    /*
     *
     * @param item
     * Mensaje de confirmación para eliminar una nota
     */
    public void showBackDialog () {
        EditText noteTitle = (EditText) findViewById(R.id.imageTitle);
        NoteImage note = (NoteImage)viewModel.getNoteToView();

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirmación");
        alert.setTitle("¿Quieres guardar los cambios?");

        alert.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!viewModel.isValidTitle(noteTitle.getText().toString())) {
                    //cambiar a dialog
                    Toast.makeText(imageViewActivity.this, "Titulo ya usado", Toast.LENGTH_SHORT).show();
                }
                else if (noteTitle.getText().toString().isEmpty()) {
                    //cambiar a dialog
                    Toast.makeText(imageViewActivity.this, "Titulo no puede estar vacío", Toast.LENGTH_SHORT).show();
                }
                else{
                    note.setTitle(noteTitle.getText().toString());
                    Toast.makeText(imageViewActivity.this, "Cambios guardados", Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                }
            }
        });

        alert.setNegativeButton("No guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(imageViewActivity.this, "Cambios no guardados", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            }
        });

        alert.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(imageViewActivity.this, "Acción cancelada", Toast.LENGTH_SHORT).show();
            }
        });

        alert.create().show();


    }


}