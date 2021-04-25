package com.example.minscreennotepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.create_text_note_menu, menu);
        return true;
    }

    //Guardar nota de texto
    public void saveTextNote (MenuItem item) {
        EditText noteTitle = (EditText) findViewById(R.id.titleText);
        EditText noteText = (EditText) findViewById(R.id.text);
        viewModel.addTextNote( noteTitle.getText().toString(),  noteText.getText().toString());
        Toast.makeText(this, "Nota guardada", Toast.LENGTH_SHORT).show();
        goToMainActivity();
    }
}
