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

public class audioViewActivity extends AppCompatActivity {
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
                //goToMainActivity();
                showReturnDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showReturnDialog() {
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
                Toast.makeText(audioViewActivity.this, "Operación Cancelada.", Toast.LENGTH_SHORT).show();
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
        inflater.inflate(R.menu.view_audio_note_menu, menu);
        return true;
    }

    //Inicializamos los elementos que manipularemos
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

    //Guardar cambios nota de texto
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
            noteAudio.setTitle(audioTitle.getText().toString());
            Toast.makeText(this, "Cambios guardados.", Toast.LENGTH_SHORT).show();
            goToMainActivity();
        }
    }

    /*
     *
     * @param item
     * Mensaje de confirmación para eliminar una nota
     */
    public void showDeleteDialog (MenuItem item) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirmación.");
        alert.setTitle("¿Seguro que quieres eliminar esta nota?");

        alert.setPositiveButton("Eliminar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewModel.DeleteNoteToView();
                Toast.makeText(audioViewActivity.this, "Nota eliminada.", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            }
        });

        alert.setNegativeButton("Cancelar.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(audioViewActivity.this, "Operación cancelada.", Toast.LENGTH_SHORT).show();
            }
        });

        alert.create().show();
    }
    //updating mSeekBar
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

    private void updateSeekBar() {
        mHandler.postDelayed(mRunnable, 1000);
    }
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

    // Play start/stop
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
    private void pausePlaying() {
        playBtn.setImageResource(R.drawable.ic_media_play);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.pause();
    }

    private void resumePlaying() {
        playBtn.setImageResource(R.drawable.ic_media_pause);
        mHandler.removeCallbacks(mRunnable);
        mMediaPlayer.start();
        updateSeekBar();
    }

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