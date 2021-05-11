package com.example.taller3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

public class UserListActivity extends AppCompatActivity {
    private ListView listView;

    private HashMap<String, Bitmap> images = new HashMap<>();
    private HashMap<String, UserPojo> users = new HashMap<>();
    private final int TWO_MEGABYTES = 2000000;
    private final ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            users.clear();
            for (DataSnapshot data : snapshot.getChildren()) {
                UserPojo user = data.getValue(UserPojo.class);
                if (user.getDisponible() == 1) {
                    Log.i("LOL", user.getNombre() + " esta disponible!");
                    users.put(user.getUid(), user);
                }
            }
            getImagesAndUpdateList();
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        listView = findViewById(R.id.userListView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference(PATHSDB.USERS).addValueEventListener(eventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference(PATHSDB.USERS).removeEventListener(eventListener);
    }

    private void getImagesAndUpdateList() {
        images.clear();
        for (String uid : users.keySet()) {
            FirebaseStorage.getInstance().getReference(PATHSDB.IMAGES).child(uid).getBytes(TWO_MEGABYTES)
                    .addOnSuccessListener(bytes -> {
                        images.put(uid, BitmapFactory.decodeStream(new ByteArrayInputStream(bytes)));
                        if (images.size() == users.size()) {
                            listView.setAdapter(new UserAdapter(this, users, images));
                        }
                    });
        }
    }
}