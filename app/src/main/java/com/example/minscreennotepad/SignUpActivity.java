package com.example.minscreennotepad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    private SharedViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        viewModel = SharedViewModel.getInstance();

        getSupportActionBar().setTitle("Sign up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void signUpUserButtonClick(View view) {
        EditText userName = (EditText) findViewById(R.id.signupUsername_textEdit);
        EditText password = (EditText) findViewById(R.id.signupPassword_textEdit);
        EditText passwodR = (EditText) findViewById(R.id.signupRepeatPassword_textEdit);

        if(userName.getText().toString().equals("") || passwodR.getText().toString().equals("") ||
                password.getText().toString().equals("")) {
            Toast.makeText(view.getContext(), "Hi ha camps buits", Toast.LENGTH_SHORT).show();
        }
        else {
            if (password.getText().toString().equals(passwodR.getText().toString())) {
                String signupStatus = viewModel.signUpUser(userName.getText().toString(), password.getText().toString());
                if (signupStatus.equals("Usuari registrat")) {
                    this.finish();
                } else {
                    Toast.makeText(view.getContext(), signupStatus, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(view.getContext(), "Contrasenya no coincideix", Toast.LENGTH_SHORT).show();
            }
        }
    }
}