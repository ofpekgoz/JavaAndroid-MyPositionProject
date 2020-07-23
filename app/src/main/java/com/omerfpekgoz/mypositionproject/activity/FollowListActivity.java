package com.omerfpekgoz.mypositionproject.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.omerfpekgoz.mypositionproject.R;
import com.omerfpekgoz.mypositionproject.adapter.FriendsAdapter;
import com.omerfpekgoz.mypositionproject.model.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FollowListActivity extends AppCompatActivity {

    private Toolbar toolbarMain;
    private RecyclerView recylerViewFollowList;
    private FloatingActionButton fab;

    private int izinKontrol;
    private String mUserId;
    public static final int ALARM_CODE = 1;

    private List<String> userKeyList;
    private FriendsAdapter friendsAdapter;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDatabaseReference, followDatabaseReference, locationDatabaseReference;

    FirebaseAuth auth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_list);

        toolbarMain = findViewById(R.id.toolbarMain);
        recylerViewFollowList = findViewById(R.id.recylerViewFollowList);

        recylerViewFollowList.setHasFixedSize(true);
        recylerViewFollowList.setLayoutManager(new LinearLayoutManager(this));


        auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();


        friendsAdapter = new FriendsAdapter();
        userKeyList = new ArrayList<>();


        firebaseDatabase = FirebaseDatabase.getInstance();

        followDatabaseReference = firebaseDatabase.getReference().child("Arkadaslar");


        toolbarMain.setTitle("My Position");
        setSupportActionBar(toolbarMain);


        //İzin verilip verilmediğini kontrol ediyoruz
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            izinKontrol = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            if (izinKontrol != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(FollowListActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                        , 100);


            } else {

            }
        }


        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FollowListActivity.this, AllUsersActivity.class));
            }
        });

        mUserId = mUser.getUid();
        getAllFriends();

    }

    //Kullanıcıya sorulduktan sonra verilne cevabı buardan alırız
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            //Verilen izin sayısı 0 dan büyük ise ve 0. indeks yani lokasyon iznine izin verildiyse
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "İZİN VERİLDİ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "İZİN VERİLMEDİ", Toast.LENGTH_SHORT).show();
            }

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menuLogout) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(FollowListActivity.this);


            alertBuilder.setTitle("Çıkış Yapılsın Mı?"); //başlık kısmı


            alertBuilder.setPositiveButton("ÇIKIŞ YAP", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    auth.signOut();
                    Toast.makeText(FollowListActivity.this, "Çıkış Yapıldı", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(FollowListActivity.this, LoginActivity.class));
                    finish();

                }
            });

            alertBuilder.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertBuilder.create().show();

        }
        if (item.getItemId() == R.id.menuSettings) {
            startActivity(new Intent(FollowListActivity.this, SettingsActivity.class));

        }
        if (item.getItemId() == R.id.menuProfil) {
            startActivity(new Intent(FollowListActivity.this, ProfileActivity.class));

        }
        if (item.getItemId() == R.id.menuRequest) {
            startActivity(new Intent(FollowListActivity.this, RequestsActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        if (mUser == null) {
            startActivity(new Intent(FollowListActivity.this, LoginActivity.class));
            finish();
        } else {
            mUserId = mUser.getUid();
        }
        super.onStart();
    }

    public void getAllFriends() {

        followDatabaseReference.child(mUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String checkValue = dataSnapshot.child("date").getValue().toString();

                if (!checkValue.equals("") && (userKeyList.indexOf(dataSnapshot.getKey()) == -1)) {
                    userKeyList.add(dataSnapshot.getKey());

                }

                friendsAdapter = new FriendsAdapter(FollowListActivity.this, userKeyList);
                recylerViewFollowList.setAdapter(friendsAdapter);
                friendsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                userKeyList.remove(dataSnapshot.getKey());
                friendsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
