package com.example.minscreennotepad;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minscreennotepad.NoteClasses.NoteAudio;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AudioViewActivity extends AppCompatActivity {
    private static final String LOG_TAG = "AudioPlayTest";
    private SharedViewModel viewModel;
    private MediaPlayer mMediaPlayer = null;
    private Handler mHandler = new Handler();
    private boolean isPlaying = false;
    private String filePath;
    private ImageButton playBtn;
    private SeekBar mSeekBar = null;
    long minutes = 0;
    long seconds = 0;
    private TextView actualTimeTextView = null;
    private TextView fileLengthTextView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_view);
        viewModel = SharedViewModel.getInstance();

        getSupportActionBar().setTitle("Ver nota de audio.");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setNote();
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar mSeekBar, int progress, boolean fromUser) {
                if (mMediaPlayer != null && fromUser) {
                    mMediaPlayer.seekTo(progress);
                    mHandler.removeCallbacks(mRunnable);

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mMediaPlayer.getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mMediaPlayer.getCurrentPosition())
                            - TimeUnit.MINUTES.toSeconds(minutes);
                    actualTimeTextView.setText(String.format("%02d:%02d", minutes, seconds));

                    updateSeekBar();

                } else if (mMediaPlayer == null && fromUser) {
                    prepareMediaPlayerFromPoint(progress);
                    updateSeekBar();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(mMediaPlayer != null) {
                    // remove message Handler from updating progress bar
                    mHandler.removeCallbacks(mRunnable);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mMediaPlayer != null) {
                    mHandler.removeCallbacks(mRunnable);
                    mMediaPlayer.seekTo(seekBar.getProgress());

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mMediaPlayer.getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mMediaPlayer.getCurrentPosition())
                            - TimeUnit.MINUTES.toSeconds(minutes);
                    actualTimeTextView.setText(String.format("%02d:%02d", minutes,seconds));
                    updateSeekBar();
                }
            }
        });

        playBtn = (ImageButton) findViewById(R.id.play_button);
            playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(isPlaying);
                isPlaying = !isPlaying;
            }
            });

        fileLengthTextView.setText(String.format("%02d:%02d", minutes,seconds));
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showBackDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Muestra un diálogo al usuario preguntando si quiere guardar o descartar los cambios en la nota,
     * o cancelar la acción actual.
     */
    private void showBackDialog() {
        EditText noteTitle = (EditText) findViewById(R.id.audio_title);
        NoteAudio note = (NoteAudio) viewModel.getNoteToView();

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
                        Toast.makeText(AudioViewActivity.this, "Cambios guardados", Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    }
                }
            });

            alert.setNegativeButton("No guardar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(AudioViewActivity.this, "Cambios no guardados", Toast.LENGTH_SHORT).show();
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

    /**
     * Envia al usuario a la MainActivity
     */
    public void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.view_audio_note_menu, menu);
        return true;
    }

    /**
     * Inicializa los elementos del layout y los rellena con los parámetros de la nota a mostrar
     */
    public void setNote(){
        EditText audioTitle = (EditText) findViewById(R.id.audio_title);

        NoteAudio noteAudio = (NoteAudio)viewModel.getNoteToView();

        audioTitle.setText(noteAudio.getTitle(), TextView.BufferType.EDITABLE);
        filePath = noteAudio.getFilePath();
        long audioDuration = noteAudio.getFileLenght();
        minutes = TimeUnit.MILLISECONDS.toMinutes(audioDuration);
        seconds = TimeUnit.MILLISECONDS.toSeconds(audioDuration)
                - TimeUnit.MINUTES.toSeconds(minutes);
        actualTimeTextView = (TextView) findViewById(R.id.actual_time);
        fileLengthTextView = (TextView) findViewById(R.id.file_lenght);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);


    }

    /**
     * Guarda cambios en la nota de audio
     */
    public void saveChangesAudioNote(MenuItem item) {
        EditText audioTitle = (EditText) findViewById(R.id.audio_title);
        NoteAudio noteAudio = (NoteAudio)viewModel.getNoteToView();
        if(!viewModel.isValidTitle(audioTitle.getText().toString())){
            sameTitleDialog();
        }
        else if(audioTitle.getText().toString().isEmpty()){
            nullTitleDialog();
        }
        else{
            DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance();
            databaseAdapter.saveChangesNoteAudio(audioTitle.getText().toString(), noteAudio.getFilePath(), noteAudio.getFileLenght(), noteAudio.getId());

            noteAudio.setTitle(audioTitle.getText().toString());
            Toast.makeText(this, "Cambios guardados.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Muestra un diálogo preguntando al usuario si está seguro de querer borrar la nota
     */
    public void showDeleteDialog (MenuItem item) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirmación.");
        alert.setTitle("¿Seguro que quieres eliminar esta nota?");

        NoteAudio noteAudio = (NoteAudio) viewModel.getNoteToView();

        alert.setPositiveButton("Eliminar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseAdapter databaseAdapter = DatabaseAdapter.getInstance();
                //Eliminamos la nota en Firebase
                databaseAdapter.deleteNoteAudio(noteAudio.getId());
                //Eliminamos la nota a nivel Local
                viewModel.DeleteNoteToView();
                Toast.makeText(AudioViewActivity.this, "Nota eliminada.", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            }
        });

        alert.setNegativeButton("Cancelar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(AudioViewActivity.this, "Operación cancelada.", Toast.LENGTH_SHORT).show();
            }
        });

        alert.create().show();
    }

    /**
     * updating mSeekbar
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(mMediaPlayer != null){

                int mCurrentPosition = mMediaPlayer.getCurrentPosition();
                mSeekBar.setProgress(mCurrentPosition);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition)
                        - TimeUnit.MINUTES.toSeconds(minutes);
                actualTimeTextView.setText(String.format("%02d:%02d", minutes, seconds));

                updateSeekBar();
            }
        }
    };

    /**
     * Actualiza la SeekBar
     */
    private void updateSeekBar() {
        mHandler.postDelayed(mRunnable, 1000);
    }

    /**
     * Pausa la reproducción de audio
     */
    @Override
    public void onPause() {
        super.onPause();

        if (mMediaPlayer != null) {
            stopPlaying();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mMediaPlayer != null) {
            stopPlaying();
        }
    }

    /**
     * Acciones realizadas al inicar la reproducción del audio
     * @param isPlaying Boolean que verifica si se está reproduciendo
     */
    private void onPlay(boolean isPlaying){
        if (!isPlaying) {
            //currently MediaPlayer is not playing audio
            if(mMediaPlayer == null) {
                startPlaying(); //start from beginning
            } else {
                resumePlaying(); //resume the currently paused MediaPlayer
            }

        } else {
            //pause the MediaPlayer
            pausePlaying();
        }
    }

    /**
     * Inicia la reproducción del audio
     */
    private void startPlaying() {
        playBtn.setImageResource(R.drawable.ic_media_pause);
        mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mSeekBar.setMax(mMediaPlayer.getDuration());

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        } catch (IOException e) {
            Log.e(LOG_TAG, "Preparacion fallida.");
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
            }
        });

        updateSeekBar();

    }

    /**
     * Prepara el reproductior para iniciar desde un punto en concreto del audio
     * @param progress int indicando el punto donde se quiere empezar a reproducir
     */
    private void prepareMediaPlayerFromPoint(int progress) {
        //set mediaPlayer to start from middle of the audio file

        mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mSeekBar.setMax(mMediaPlayer.getDuration());
            mMediaPlayer.seekTo(progress);

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlaying();
                }
            });

        } catch (IOException e) {
            Log.e(LOG_TAG, "Preparacion fallida");
        }


    }

    /**
     * Pausa la reproducción del audio
     */
    private void pausePlaying() {
        playBtn.setImageResource(R.drawable.ic_media_play);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.pause();
    }

    /**
     * Reanuda la reproducción del audio
     */
    private void resumePlaying() {
        playBtn.setImageResource(R.drawable.ic_media_pause);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.start();
        updateSeekBar();
    }

    /**
     * Para la reproducción del audio
     */
    private void stopPlaying() {
        playBtn.setImageResource(R.drawable.ic_media_play);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;

        mSeekBar.setProgress(mSeekBar.getMax());
        isPlaying = !isPlaying;

        actualTimeTextView.setText(fileLengthTextView.getText());
        mSeekBar.setProgress(mSeekBar.getMax());

    }

    /**
     * Muestra un diálogo al usuario indicandole que el título que ha elegido para la nota ya
     * está siendo usado por otra nota
     */
    public void sameTitleDialog() {
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
     * Muestra un diálogo al usuario indicandole que el parámetro de título está vacío
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


}