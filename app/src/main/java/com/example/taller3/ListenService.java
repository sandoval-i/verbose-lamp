package com.example.taller3;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class ListenService extends JobIntentService {
    private final static int JOB_ID = 20;
    private final ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            int disponible = Integer.parseInt(snapshot.child("disponible").getValue().toString());
            if (disponible == 1) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(ListenService.this, LogInActivity.NOTIFICATION_CHANNEL_ID);
                builder.setSmallIcon(R.drawable.common_full_open_on_phone);
                builder.setContentTitle("Nuevos usuarios disponibles");
                builder.setContentText(snapshot.child("nombre").getValue().toString() + " esta disponible!");
                builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

                String uid = snapshot.child("uid").getValue().toString();

                Intent intent = new Intent(ListenService.this, UserTrackingAcitivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("uid", uid);

                PendingIntent pendingIntent = PendingIntent.getActivity(ListenService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                builder.setAutoCancel(true); //Remueve la notificación cuando se toca

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ListenService.this);
                notificationManager.notify(Utils.getNotificationId(uid), builder.build());
            }
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {
        }
    };


    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, ListenService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull @NotNull Intent intent) {
        FirebaseDatabase.getInstance().getReference(PATHSDB.USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    data.getRef().addValueEventListener(eventListener);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }
}
