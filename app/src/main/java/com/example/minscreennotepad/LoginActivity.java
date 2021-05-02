package com.example.minscreennotepad;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    SharedViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        viewModel = SharedViewModel.getInstance();
        getSupportActionBar().setTitle("Inicio de sesión.");
    }

    /**
     * Al hacer click en el botón de login, intenta iniciar sesión a partir de los parámetros
     * proporcionados
     */
    public void loginButtonClick(View view) {
        EditText userNameText = (EditText) findViewById(R.id.loginUsername_TextEdit);
        EditText passwordText = (EditText) findViewById(R.id.loginPassword_textEdit);
        String loginStatus = viewModel.loginUser(userNameText.getText().toString(), passwordText.getText().toString());
        if(loginStatus.equals("Inicio de sesión correcto.")){
            goToMainActivity();
        }
        else {
            loginErrorDialog(loginStatus);
        }
    }

    /**
     * Al hacer click en el botón de signup, navega hacia la actividad para registrar un usuario
     * @param view
     */
    public void signUpActivityButtonClick(View view) {
        goToSignupActivity();
    }

    /**
     * Navega a SignupActivity
     */
    private void goToSignupActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    /**
     * Navega a MainActivity
     */
    private void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    /**
     * Muestra un diálogo al usuario con información acerca del estado del login
     * @param loginStatus   String con el estado del login
     */
    private void loginErrorDialog(String loginStatus) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error.");
        alert.setTitle(loginStatus);

        alert.setPositiveButton("Aceptar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }
}