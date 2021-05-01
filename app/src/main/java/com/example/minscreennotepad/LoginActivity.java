package com.example.minscreennotepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    SharedViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        viewModel = SharedViewModel.getInstance();
        getSupportActionBar().setTitle("Login");
    }

    public void loginButtonClick(View view) {
        EditText userNameText = (EditText) findViewById(R.id.loginUsername_TextEdit);
        EditText passwordText = (EditText) findViewById(R.id.loginPassword_textEdit);
        String loginStatus = viewModel.loginUser(userNameText.getText().toString(), passwordText.getText().toString());
        if(loginStatus.equals("Login Correcte")){
            goToMainActivity();
        }
    }

    private void onSignUp() {

    }

    private void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}