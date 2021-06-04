package com.example.minscreennotepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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
        if(userNameText.getText().toString().equals("") || passwordText.getText().toString().equals("")) {
            loginErrorDialog("Hay campos vacíos");
        }
        else {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(userNameText.getText().toString(), passwordText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                viewModel.setDBUser(mAuth.getCurrentUser());
                                viewModel.setUserLoggedIn(true);
                                goToMainActivity();
                            }
                            else {
                                loginErrorDialog(task.getException().getMessage());
                            }
                        }
                    });
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