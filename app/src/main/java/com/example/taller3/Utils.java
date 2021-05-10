package com.example.taller3;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class Utils {
    public static boolean validateEmail(String email) {
        Log.i("LOL", "valida " + email);
        Log.i("LOL", "@: " + !email.contains("@"));
        Log.i("LOL", ".: " + !email.contains("."));
        Log.i("LOL", "len: " + email.length());


        if (!email.contains("@") || !email.contains(".") || email.length() < 5) {
            Log.i("LOL", "Retorna false");
            return false;
        }
        Log.i("LOL", "Retorna true");
        return true;
    }

    public static double calculateDistance(LatLng l1, LatLng l2) {
        double lat1 = l1.latitude, lat2 = l2.latitude;
        double long1 = l1.longitude, long2 = l2.longitude;
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = 6371 * c;
        return Math.round(result * 100.0) / 100.0;
    }
}
