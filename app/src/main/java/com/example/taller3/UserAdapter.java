package com.example.taller3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class UserAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<UserPojo> users = new ArrayList<>();
    private ArrayList<Bitmap> images = new ArrayList<>();

    public UserAdapter(Context context, HashMap<String, UserPojo> usersMap, HashMap<String, Bitmap> imagesMap) {
        this.context = context;
        for (String uid : usersMap.keySet()) {
            users.add(usersMap.get(uid));
            images.add(imagesMap.get(uid));
        }
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false);
        }


        ImageView imageUser = convertView.findViewById(R.id.userItemImageView);
        TextView nameUser = convertView.findViewById(R.id.userItemTextView);
        Button trackButton = convertView.findViewById(R.id.userItemButton);

        imageUser.setImageDrawable(new BitmapDrawable(convertView.getResources(), images.get(position)));
        nameUser.setText(users.get(position).getNombre());
        trackButton.setOnClickListener(v -> {
            Log.i("LOL", "trackButton para " + users.get(position).getNombre());
            Intent intent = new Intent(context, UserTrackingAcitivity.class);
            intent.putExtra("uid", users.get(position).getUid());
            context.startActivity(intent);
        });
        return convertView;
    }
}
