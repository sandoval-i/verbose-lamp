package com.example.taller3;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {
    private static boolean SERVICE_RUNNING = false;
    private final String TAG = "taller3";
    public final static String NOTIFICATION_CHANNEL_ID = "taller3";

    private FirebaseAuth auth;
    private Button loginButton;
    private Button signupButton;
    private EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.registroButton);
        email = findViewById(R.id.emailET);
        password = findViewById(R.id.passwordET);
        loginButton.setOnClickListener(v -> signIn(email.getText().toString(), password.getText().toString()));
        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });

        if (!SERVICE_RUNNING) {
            Intent intent = new Intent(LogInActivity.this, ListenService.class);
            ListenService.enqueueWork(this, intent);
        }
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI(auth.getCurrentUser());
    }

    private void signIn(String email, String password) {
        if (email.isEmpty() || password.isEmpty() || !Utils.validateEmail(email)) {
            Toast.makeText(this, "Campos invalidos", Toast.LENGTH_LONG).show();
            return;
        }
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.i(TAG, "signInWithEmail:success");
                updateUI(auth.getCurrentUser());
            } else {
                Log.i(TAG, "signInWithEmail:failure", task.getException());
                updateUI(null);
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        password.setText("");
        if (user == null) return;
        email.setText("");
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }
}