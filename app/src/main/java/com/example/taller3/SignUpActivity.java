package com.example.taller3;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class SignUpActivity extends AppCompatActivity {
    private EditText nombreEditText;
    private EditText apellidoEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText identificacionEditText;
    private Button addImageGalleryButton;
    private Button addImageCamaraButton;
    private Button addLocationButton;
    private Button signupButton;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    private final int GALLERY_CODE = 1;
    private final int CAMERA_CODE = 2;
    private final int IMAGE_PICKER_REQUEST = 3;
    private final int CAMERA_REQUEST = 4;
    private final int LOCATION_REQUEST = 5;

    private Bitmap selectedImage = null;
    private LatLng location = null;

    private int writeCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        nombreEditText = findViewById(R.id.nombreEditText);
        apellidoEditText = findViewById(R.id.apellidoEditText);
        identificacionEditText = findViewById(R.id.identificacionEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        addImageGalleryButton = findViewById(R.id.anadirImagenGaleriaButton);
        addImageCamaraButton = findViewById(R.id.anadirImagenCamaraButton);
        addLocationButton = findViewById(R.id.localizacionButton);
        signupButton = findViewById(R.id.registrarseButton);

        signupButton.setOnClickListener(v -> registrar());
        addImageGalleryButton.setOnClickListener(v -> {
            if (!PermissionHelper.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                PermissionHelper.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                        GALLERY_CODE);
                return;
            }
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_PICKER_REQUEST);
        });
        addImageCamaraButton.setOnClickListener(v -> {
            if (!PermissionHelper.hasPermission(this, Manifest.permission.CAMERA)) {
                PermissionHelper.requestPermission(this, Manifest.permission.CAMERA,
                        CAMERA_CODE);
                return;
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });
        addLocationButton.setOnClickListener(v -> startActivityForResult(
                new Intent(this, CurrentLocationActivity.class), LOCATION_REQUEST));

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("LOL", "onActivityResult, " + requestCode + ", " + resultCode);
        if (resultCode != RESULT_OK) return;
        try {
            if (requestCode == IMAGE_PICKER_REQUEST) {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                imageStream.close();
            } else if (requestCode == CAMERA_REQUEST) {
                selectedImage = (Bitmap) data.getExtras().get("data");
            } else if (requestCode == LOCATION_REQUEST) {
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                location = new LatLng(latitude, longitude);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void successWrite() {
        writeCount++;
        if (writeCount == 2) {
            finish();
        }
    }

    private void registrar() {
        String nombre = nombreEditText.getText().toString();
        String apellido = apellidoEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String numeroIdentificacion = identificacionEditText.getText().toString();
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty() || !Utils.validateEmail(email)) {
            Log.i("LOL", "nombre: " + nombre.isEmpty());
            Log.i("LOL", "apellido: " + apellido.isEmpty());
            Log.i("LOL", "email: " + email.isEmpty());
            Log.i("LOL", "password: " + password.isEmpty());
            Log.i("LOL", "validacion: " + !Utils.validateEmail(email));
            Log.i("LOL", "Crear usuario: failure");
            Toast.makeText(this, "Campos invalidos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedImage == null) {
            Toast.makeText(this, "Falta agregar foto", Toast.LENGTH_SHORT).show();
            return;
        }
        if (location == null) {
            Toast.makeText(this, "Falta agregar localizacion", Toast.LENGTH_SHORT).show();
            return;
        }
        UserPojo user = new UserPojo();
        user.setNombre(nombre);
        user.setApellido(apellido);
        user.setEmail(email);
        user.setNumeroIdentificacion(numeroIdentificacion);
        user.setLatitud(location.latitude);
        user.setLongitud(location.longitude);
        user.setDisponible(0);
        Log.i("LOL", "Crear usuario:success");
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = auth.getCurrentUser().getUid();
                user.setUid(uid);

                Log.i("LOL", "Registra al usuario con UID: " + uid);
                Log.i("LOL", user.toString());
                database.getReference(PATHSDB.USERS).child(uid).setValue(user).addOnSuccessListener(unused -> successWrite());

                // Sube la imagen.
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                storage.getReference(PATHSDB.IMAGES).child(uid).putBytes(byteArrayOutputStream.toByteArray()).addOnSuccessListener(unused -> successWrite());

                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Registro fallido. Intente de nuevo", Toast.LENGTH_SHORT).show();
            }
        });
    }
}