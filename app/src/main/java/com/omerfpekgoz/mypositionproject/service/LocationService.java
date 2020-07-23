package com.omerfpekgoz.mypositionproject.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.WindowId;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.omerfpekgoz.mypositionproject.activity.FollowListActivity;

import java.util.HashMap;
import java.util.Map;

public class LocationService extends Service {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private String konumSaglayici = "gps";
    private int izinKontrol;

    private FirebaseAuth auth;
    private FirebaseUser mUser;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Konumlar").child(mUser.getUid());

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location konum = locationManager.getLastKnownLocation(konumSaglayici);  //En sonki konumu aldık


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location konum) {
                saveUserLocation(konum.getLatitude(), konum.getLongitude());  //Kullanıcı enlem ve boylam kaydetme

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }


    private void saveUserLocation(double latitude, double longitude) {

        Map<String, String> usersLocation = new HashMap<>();

        usersLocation.put("latitude", String.valueOf(latitude));
        usersLocation.put("longitude", String.valueOf(longitude));
        usersLocation.put("time", String.valueOf(System.currentTimeMillis()));

        databaseReference.push().setValue(usersLocation).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                setAnAlarm();
                stopSelf();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "KONUM ALMADA HATA", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAnAlarm() {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent serviceIntent = new Intent(this, LocationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, FollowListActivity.ALARM_CODE, serviceIntent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 600000, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 600000, pendingIntent);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
            locationManager = null;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = locationManager.getBestProvider(criteria, true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
        } else {

            if (izinKontrol != PackageManager.PERMISSION_GRANTED) {

            } else {
                locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }
}
