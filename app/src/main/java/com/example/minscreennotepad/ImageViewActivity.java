package com.example.minscreennotepad;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minscreennotepad.NoteClasses.NoteImage;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.io.FileOutputStream;

public class ImageViewActivity extends AppCompatActivity {
    
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
                this.showBackDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Navega a la MainActivity
     */
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

    /**
     * Rellena los elementos del layout a partir de los parámetros de la nota a visualizar
     */
    private void setNote() {
        EditText noteTitle = (EditText) findViewById(R.id.imageTitle);
        ImageView noteContent = (ImageView) findViewById(R.id.imageContent);

        NoteImage note = (NoteImage)viewModel.getNoteToView();

        noteTitle.setText(note.getTitle(), TextView.BufferType.EDITABLE);
        noteContent.setImageURI(note.getFile());
    }

    /**
     * Guarda los cambios realizados a la nota
     */
    public void saveChangesImageNote(MenuItem item) {
        EditText noteTitle = (EditText) findViewById(R.id.imageTitle);
        NoteImage note = (NoteImage)viewModel.getNoteToView();

        if(!viewModel.isValidTitle(noteTitle.getText().toString()) &&
                !noteTitle.getText().toString().equals(note.getTitle())) {
            sameTitleDialog();
        }
        else if(noteTitle.getText().toString().isEmpty()){
            nullTitleDialog();
        }
        else {
            note.setTitle(noteTitle.getText().toString());
        }
    }
    /**
     * Compartir imagen
     */
    public void shareImage(MenuItem item) {
        NoteImage note = (NoteImage)viewModel.getNoteToView();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, note.getFile());
        try {
            startActivity(Intent.createChooser(shareIntent, "Compartir vía"));
        }catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Muestra un diálogo al usuario preguntandole si quiere eliminar la nota.
     */
    public void showDeleteDialog (MenuItem item) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirmación.");
        alert.setTitle("¿Seguro que quieres eliminar esta nota?");

        alert.setPositiveButton("Eliminar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewModel.DeleteNoteToView();
                Toast.makeText(ImageViewActivity.this, "Nota eliminada.", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            }
        });

        alert.setNegativeButton("Cancelar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ImageViewActivity.this, "Operación cancelada.", Toast.LENGTH_SHORT).show();
            }
        });

        alert.create().show();
    }

    /**
     * Muestra un diálogo avisando al usuario de que el título de la nota ya está en uso por otra
     */
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

    /**
     * Muestra un diálogo avisando al usuario de que el parámetro del título está vacío
     */
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

    /**
     * Muestra un diálogo al usuario preguntando si quiere guardar o descartar los cambios en la nota,
     * o cancelar la acción actual.
     */
    private void showBackDialog() {
        EditText noteTitle = (EditText) findViewById(R.id.imageTitle);
        NoteImage note = (NoteImage)viewModel.getNoteToView();

        if(!noteTitle.getText().toString().equals(note.getTitle())) {
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
                    else{
                        note.setTitle(noteTitle.getText().toString());
                        Toast.makeText(ImageViewActivity.this, "Cambios guardados", Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    }
                }
            });

            alert.setNegativeButton("No guardar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(ImageViewActivity.this, "Cambios no guardados", Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                }
            });

            alert.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            alert.create().show();
        }
        //Si no hay cambios, volvemos directamente a MainActivity
        else{
            goToMainActivity();
        }
    }


}