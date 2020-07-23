package com.omerfpekgoz.mypositionproject.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.omerfpekgoz.mypositionproject.R;
import com.omerfpekgoz.mypositionproject.adapter.AllUsersAdapter;
import com.omerfpekgoz.mypositionproject.adapter.RequestAdapter;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity {

    private Toolbar toolbarRequest;
    private RecyclerView recylerViewRequest;

    private RequestAdapter requestAdapter;
    private AllUsersAdapter allUsersAdapter;

    private FirebaseAuth auth;
    private FirebaseUser mUser;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private String mUserId;
    private List<String> requestKeyList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        toolbarRequest=findViewById(R.id.toolbarRequest);
        toolbarRequest.setTitle("İstekler");
        setSupportActionBar(toolbarRequest);

        recylerViewRequest =findViewById(R.id.recylerViewRequest);

        recylerViewRequest.setHasFixedSize(true);
        recylerViewRequest.setLayoutManager(new LinearLayoutManager(this));

        requestAdapter = new RequestAdapter();
        allUsersAdapter = new AllUsersAdapter();
        requestAdapter.notifyDataSetChanged();

        auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        mUserId = mUser.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Istekler");

        requestKeyList = new ArrayList<>();

        getAllRequests();
    }
    public void getAllRequests() {


        databaseReference.child(mUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String checkValue = dataSnapshot.child("value").getValue().toString();

                if (checkValue.equals("Istek Alındı")) {
                    requestKeyList.add(dataSnapshot.getKey());

                    requestAdapter.notifyDataSetChanged();

                }


                requestAdapter = new RequestAdapter(RequestsActivity.this, requestKeyList);
                recylerViewRequest.setAdapter(requestAdapter);

            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                requestKeyList.remove(dataSnapshot.getKey());
                requestAdapter.notifyDataSetChanged();

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
