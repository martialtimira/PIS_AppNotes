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
import android.widget.Toast;

public class textCreatorActivity extends AppCompatActivity {

    private SharedViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_creator);

        viewModel = SharedViewModel.getInstance();

        getSupportActionBar().setTitle("Crear nota de texto");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
    private void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.create_text_note_menu, menu);
        return true;
    }

    //Guardar nota de texto
    public void saveTextNote (MenuItem item) {
        EditText noteTitle = (EditText) findViewById(R.id.textView_titleText);
        EditText noteText = (EditText) findViewById(R.id.text_view_text);
        if(!viewModel.isValidTitle(noteTitle.getText().toString())) {
            //cambiar a dialog
            Toast.makeText(this, "Titulo ya usado", Toast.LENGTH_SHORT).show();
        }
        else if (noteTitle.getText().toString().isEmpty()) {
            //cambiar a dialog
            Toast.makeText(this, "Titulo no puede estar vacío", Toast.LENGTH_SHORT).show();
        }
        else if(noteText.getText().toString().length() > 10000) {
            //cambiar a dialog
            Toast.makeText(this, "Nota demasiado larga", Toast.LENGTH_SHORT).show();
        }
        else{
            viewModel.addTextNote( noteTitle.getText().toString(),  noteText.getText().toString());
            Toast.makeText(this, "Nota guardada", Toast.LENGTH_SHORT).show();
            goToMainActivity();
        }
    }

    /*
     *
     * @param item
     * Mensaje de confirmación para eliminar una nota
     */
    public void showBackDialog () {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirmación");
        alert.setTitle("¿Quieres guardar esta nota?");

        EditText noteTitle = (EditText) findViewById(R.id.textView_titleText);
        EditText noteText = (EditText) findViewById(R.id.text_view_text);

        alert.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!viewModel.isValidTitle(noteTitle.getText().toString())) {
                    //cambiar a dialog
                    Toast.makeText(textCreatorActivity.this, "Titulo ya usado", Toast.LENGTH_SHORT).show();
                }
                else if (noteTitle.getText().toString().isEmpty()) {
                    //cambiar a dialog
                    Toast.makeText(textCreatorActivity.this, "Titulo no puede estar vacío", Toast.LENGTH_SHORT).show();
                }
                else if(noteText.getText().toString().length() > 10000) {
                    //cambiar a dialog
                    Toast.makeText(textCreatorActivity.this, "Nota demasiado larga", Toast.LENGTH_SHORT).show();
                }
                else{
                    viewModel.addTextNote( noteTitle.getText().toString(),  noteText.getText().toString());
                    Toast.makeText(textCreatorActivity.this, "Nota guardada", Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                }
            }
        });

        alert.setNegativeButton("No guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(textCreatorActivity.this, "Nota eliminada", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            }
        });

        alert.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(textCreatorActivity.this, "Acción cancelada", Toast.LENGTH_SHORT).show();
            }
        });

        alert.create().show();
    }
}
