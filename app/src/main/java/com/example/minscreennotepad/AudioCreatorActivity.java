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
import java.util.concurrent.TimeUnit;

public class AudioCreatorActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String filePath = null;
    private long fileLenght;
    private int maxDuration = 0;

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

    /**
     * Metodo de selección de item del menú de la barra de herramientas
     * @param item Item del menú que ha sido seleccionado
     * @return true, si se ha ejecutado correctamente
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showRetornDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Muestra un dialogo al usuario pidiendo confirmación de que se quiere volver a la actividad
     * anterior
     */
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

    /**
     * Devuelve al usuario a la MainActivity
     */
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

    /**
     * Guarda verifica que una nota de audio cumpla los requisitos y la guarda en la lista de notas
     * @param item Item del menú seleccionado
     */
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

    /**
     * Empieza a grabar Audio
     * @param start Boolean que indica si empieza
     */
    private void onRecord(boolean start){
        if (start) {
            // grabar
            DateFormat df = new SimpleDateFormat("yyMMddHHmmss", Locale.GERMANY);
            String date = df.format(Calendar.getInstance().getTime());
            filePath =  getExternalCacheDir().getAbsolutePath()+ File.separator +date+".3gp";
            //Log.d("startRecording", audioTitle.getText().toString());
            recordButton.setImageResource(R.drawable.stop_button);

            //inicia el cronometro
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            //comenzamos a grabar
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            maxDuration = 5*60*60; //5 minutos máxima duración
            mRecorder.setMaxDuration(maxDuration);
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

            while(start){
                fileLenght = (SystemClock.elapsedRealtime() - mChronometer.getBase());
                if(fileLenght == maxDuration){
                    onStopRecording();
                    limitExtensionDialog();
                    start = false;
                }
            }


        } else {
            onStopRecording();
        }
    }

    private void onStopRecording(){
        //Finaliza la grabacion
        recordButton.setImageResource(R.drawable.microphone);
        mChronometer.stop();

        try {
            mRecorder.stop();
            fileLenght = (SystemClock.elapsedRealtime() - mChronometer.getBase());
            Log.d("audio length", "Value" + Float.toString(fileLenght));
            mRecorder.release();
        } catch (Exception e){
            Log.e(LOG_TAG, "excepción.", e);
        }
        mRecorder = null;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    /**
     * Muestra una ventana de dialogo indicando que el título de la nota ya está en siendo usado por
     * otra.
     */
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
    /**
     * Muestra una ventana de dialogo indicando que el título de la nota ya está en siendo usado por
     * otra.
     */
    public void limitExtensionDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error");
        alert.setTitle("Duración máxima permitida: 5 minutos");

        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }

    /**
     * Muestra una ventana de dialogo indicando que el parámetro del título de la nota está vacío
     */
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

    /**
     * Muestra una ventana de dialogo indicando que el parámetro del archivo de audio
     * de la nota está vacío
     */
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