package com.example.minscreennotepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private SharedViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        viewModel = SharedViewModel.getInstance();

        getSupportActionBar().setTitle("Registrarse.");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    //Botón/flecha para regresar a la pantalla de Login
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goToLoginctivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Navega a LoginActivity
     */
    private void goToLoginctivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    /**
     * Muestra un diálogo al usuario avisándole de que las contraseñas introducidas no concuerdan
     */
    private void passwordsDontMatchDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error.");
        alert.setTitle("Las contraseñas no coinciden");

        alert.setPositiveButton("Aceptar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }

    /**
     * Muestra un diálogo al usuario avisándole de que hay campos sin completar
     */
    private void emptyFieldsDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error.");
        alert.setTitle("Hay campos vacíos");

        alert.setPositiveButton("Aceptar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }

    /**
     * Se ejecuta al hacer click en el botón de "SignUp" y intenta registrar un nuevo usuario
     */
    public void signUpUserButtonClick(View view) {
        EditText userName = (EditText) findViewById(R.id.signupUsername_textEdit);
        EditText password = (EditText) findViewById(R.id.signupPassword_textEdit);
        EditText passwodR = (EditText) findViewById(R.id.signupRepeatPassword_textEdit);

        if (userName.getText().toString().equals("") || passwodR.getText().toString().equals("") ||
                password.getText().toString().equals("")) {
            emptyFieldsDialog();
        }
        else {
            if (password.getText().toString().equals(passwodR.getText().toString())) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(userName.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            viewModel.setDBUser(mAuth.getCurrentUser());
                            finish();
                        } else {
                            signUpErrorDialog(task.getException().getMessage());
                        }
                    }
                });
            }
            else {
                passwordsDontMatchDialog();
            }
        }
    }
    /**
     * Muestra un diálogo al usuario informándole del error que ha habido durante el registro
     * @param signUpStatus String del estado del registro(error)
     */
    private void signUpErrorDialog(String signUpStatus) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error.");
        alert.setTitle(signUpStatus);

        alert.setPositiveButton("Aceptar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }
}