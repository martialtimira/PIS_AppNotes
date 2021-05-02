package com.example.minscreennotepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;



import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AudioCreatorActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String filePath = null;
    //private EditText audioTitle;
    private long fileLenght;

    private MediaRecorder mRecorder = null;
    private ImageButton recordButton = null;
    private boolean mStartRecording = true;
    private Chronometer mChronometer = null;
    private SharedViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_creator);
        viewModel = SharedViewModel.getInstance();

        getSupportActionBar().setTitle("Crear nota de audio.");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        //audioTitle = findViewById(R.id.audio_title);
        recordButton = findViewById(R.id.record_btn);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
            }
        });



    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showRetornDialog();
                //goToMainActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showRetornDialog() {
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
                Toast.makeText(AudioCreatorActivity.this, "Operación Cancelada.", Toast.LENGTH_SHORT).show();
            }
        });

        alert.create().show();
    }

    //Basic explicit intent to MainActivity without extra data
    public void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.create_audio_note_menu, menu);
        return true;
    }
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    //Guardar nota de audio
    public void saveAudioNote (MenuItem item) {
        EditText audioT = (EditText) findViewById(R.id.audio_title);
        if(!viewModel.isValidTitle(audioT.getText().toString())) {
            Toast.makeText(this, "Titulo ya usado.", Toast.LENGTH_SHORT).show();
            sameTitleDialog();
        }
        else if(audioT.getText().toString().isEmpty()) {
            nullTitleDialog();
        }
        else if(fileLenght == 0) {
            nullContentDialog();
        }
        else{
            viewModel.addAudioNote( audioT.getText().toString(),  filePath, fileLenght);
            Toast.makeText(this, "Nota guardada.", Toast.LENGTH_SHORT).show();
            goToMainActivity();
        }

    }



    private void onRecord(boolean start){
        if (start) {
            // grabar
            DateFormat df = new SimpleDateFormat("yyMMddHHmmss", Locale.GERMANY);
            String date = df.format(Calendar.getInstance().getTime());
            filePath =  getExternalCacheDir().getAbsolutePath()+ File.separator +date+".3gp";
            //Log.d("startRecording", audioTitle.getText().toString());
            recordButton.setImageResource(R.drawable.pause_icon);

            //inicia el cronometro
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            //comenzamos a grabar
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(filePath);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mRecorder.prepare();
                mRecorder.start();

            } catch (IOException e) {
                Log.e(LOG_TAG, "preparación fallida");
            }
            //keep screen on while recording
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        } else {
            //Finaliza la grabacion
            recordButton.setImageResource(R.drawable.microphone);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());

            try {
                mRecorder.stop();
                fileLenght = (SystemClock.elapsedRealtime() - mChronometer.getBase());
                mRecorder.release();
            } catch (Exception e){
                Log.e(LOG_TAG, "excepción.", e);
            }
            mRecorder = null;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
    public void sameTitleDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error");
        alert.setTitle("El título ya está en uso.");

        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
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

    public void nullContentDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error.");
        alert.setTitle("Audio vacío, graba algo.");

        alert.setPositiveButton("Aceptar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }


}