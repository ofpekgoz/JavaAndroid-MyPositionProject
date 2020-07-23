package com.omerfpekgoz.mypositionproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.omerfpekgoz.mypositionproject.R;

public class MainActivity extends AppCompatActivity {

    private static int GECİS_SURESİ = 2000;

    FirebaseAuth auth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth=FirebaseAuth.getInstance();
        mUser=auth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                    if (mUser == null) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }else{
                        startActivity(new Intent(MainActivity.this, FollowListActivity.class));
                        finish();
                    }
            }
        }, GECİS_SURESİ);
    }
}
