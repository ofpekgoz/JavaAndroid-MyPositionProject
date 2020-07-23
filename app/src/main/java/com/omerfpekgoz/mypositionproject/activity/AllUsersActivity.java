package com.omerfpekgoz.mypositionproject.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omerfpekgoz.mypositionproject.R;
import com.omerfpekgoz.mypositionproject.adapter.AllUsersAdapter;
import com.omerfpekgoz.mypositionproject.model.User;

import java.util.ArrayList;
import java.util.List;

public class AllUsersActivity extends AppCompatActivity {

    private RecyclerView recylerViewAllUsers;
    private Toolbar toolbarAllUsers;

    private AllUsersAdapter allUsersAdapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseAuth auth;
    FirebaseUser mUser;
    private String mUserId;

    List<String> userKeyList;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        recylerViewAllUsers=findViewById(R.id.recylerViewAllUsers);
        toolbarAllUsers=findViewById(R.id.toolbarAllUsers);

        toolbarAllUsers.setTitle("Tüm Kullanıcılar");
        setSupportActionBar(toolbarAllUsers);

        recylerViewAllUsers.setHasFixedSize(true);
        recylerViewAllUsers.setLayoutManager(new LinearLayoutManager(this));


        userKeyList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        mUserId = mUser.getUid();

        getAllUsers();

    }

    public void getAllUsers() {
        databaseReference.child("Kullanicilar").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                databaseReference.child("Kullanicilar").child(dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (!user.getUserName().equals("null") && !dataSnapshot.getKey().equals(mUser.getUid())) {
                            if (userKeyList.indexOf(dataSnapshot.getKey()) == -1) {
                                userKeyList.add(dataSnapshot.getKey());
                            }
                            allUsersAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                allUsersAdapter = new AllUsersAdapter(getApplicationContext(), userKeyList);
                recylerViewAllUsers.setAdapter(allUsersAdapter);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

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
