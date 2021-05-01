package com.example.minscreennotepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class imageCreatorActivity extends AppCompatActivity {

    private ImageView selectedImage;
    private ImageButton camera_button;
    private ImageButton gallery_button;
    private Uri imageUri;

    private SharedViewModel viewModel;


    private static final int GALLERY_REQUEST_CODE = 1000;
    private static final int GALLERY_PERMISSION_CODE = 1001;

    private static final int CAMERA_PERMISSION_CODE = 2000;
    private static final int CAMERA_REQUEST_CODE = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_creator);

        viewModel = SharedViewModel.getInstance();

        getSupportActionBar().setTitle("Crear nota de imagen");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        selectedImage = findViewById(R.id.imageContent);
        /*
         * Métodos para acceder a la cámara.
         */
        //Asignamos variables
        camera_button = findViewById(R.id.cameraButton);
        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }
        });

        /*
         * Métodos para acceder a la galería.
         */
        gallery_button = findViewById(R.id.galleryButton);
        gallery_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Pedimos permisos
                askGalleryPermissions();
            }
        });

    }

    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(imageCreatorActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(imageCreatorActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else { // Permiso dado
            openCamera();
        }
    }

    private void askGalleryPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                //permiso denegado, lo pedimos.
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                //mostrar pop-up para el permiso
                requestPermissions(permissions, GALLERY_PERMISSION_CODE);
            }
            else { // Permiso dado
                pickImageFromGallery();
            }
        }
        else {
            pickImageFromGallery();
        }
    }

    //Manejar los permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GALLERY_PERMISSION_CODE: {
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                }
                else {
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
            }
            case CAMERA_PERMISSION_CODE: {
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //dispatchTakePictureIntent();
                    openCamera();
                }
                else {
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_REQUEST_CODE);
    }

    private void pickImageFromGallery() {
        //Intent para seleccionar la imagen
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_REQUEST_CODE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showReturnDialog(android.R.id.home);
                //goToMainActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showReturnDialog(int item) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirmación");
        alert.setTitle("¿Seguro que quieres salir?");

        alert.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToMainActivity();
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(imageCreatorActivity.this, "Operación Cancelada", Toast.LENGTH_SHORT).show();
            }
        });

        alert.create().show();
    }

    public void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            imageUri = getImageUriFromBitmap(getApplicationContext(), bitmap);
            selectedImage.setImageURI(imageUri);

        }
        else if (resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE) {

            imageUri = data.getData();

            selectedImage.setImageURI(imageUri);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.create_image_note_menu, menu);
        return true;
    }

    public void saveImageNote(MenuItem item) {
        EditText noteTitle = (EditText) findViewById(R.id.imageTitle);
        if(!viewModel.isValidTitle(noteTitle.getText().toString())) {
            sameTitleDialog();
        }
        else if (noteTitle.getText().toString().isEmpty()) {
            nullTitleDialog();
        }
        else if (imageUri==null) {
            nullContentDialog();
        }
        else {
            viewModel.addImageNote(noteTitle.getText().toString(), imageUri);
            Toast.makeText(this, "Nota guardada", Toast.LENGTH_SHORT).show();
            goToMainActivity();
        }
    }

    public void sameTitleDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error");
        alert.setTitle("Título ya en uso");

        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }

    public void nullTitleDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error");
        alert.setTitle("El título está vacío.");

        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }

    public void nullContentDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Error");
        alert.setTitle("Selecciona una imagen.");

        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }


    private Uri getImageUriFromBitmap(Context context, Bitmap bitmap){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path.toString());
    }
}