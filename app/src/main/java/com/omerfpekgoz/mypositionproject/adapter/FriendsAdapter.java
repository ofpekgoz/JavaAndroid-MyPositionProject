package com.omerfpekgoz.mypositionproject.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omerfpekgoz.mypositionproject.R;
import com.omerfpekgoz.mypositionproject.activity.FollowListActivity;
import com.omerfpekgoz.mypositionproject.activity.LoginActivity;
import com.omerfpekgoz.mypositionproject.activity.MapsActivity;
import com.omerfpekgoz.mypositionproject.model.Location;
import com.omerfpekgoz.mypositionproject.model.User;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.cardViewHolder> {

    private Context mContext;
    private List<String> friendsKeyList;
    private List<String> userLocationKeyList;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDatabaseReference, friendsDatabaseReference, locationDatabaseReference;

    FirebaseAuth auth;
    FirebaseUser mUser;

    String mUserId;
    String otherUserId;

    Double latitude;
    Double longitude;
    User otherUser;

    Location location;

    public FriendsAdapter() {
    }

    public FriendsAdapter(Context mContext, List<String> friendsKeyList) {
        this.mContext = mContext;
        this.friendsKeyList = friendsKeyList;

        firebaseDatabase = FirebaseDatabase.getInstance();

        usersDatabaseReference = firebaseDatabase.getReference().child("Kullanicilar");
        friendsDatabaseReference = firebaseDatabase.getReference().child("Arkadaslar");
        locationDatabaseReference = firebaseDatabase.getReference().child("Konumlar");

        auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        mUserId = mUser.getUid();


        friendsKeyList = new ArrayList<>();

    }

    @NonNull
    @Override
    public cardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_follow_friends, parent, false);

        return new cardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final cardViewHolder holder, final int position) {

        usersDatabaseReference.child(friendsKeyList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                otherUser = dataSnapshot.getValue(User.class);


                if (!otherUser.getUserName().equals("null")) {

                    Picasso.with(mContext).load(otherUser.getImage()).into(holder.profileImageFollowFriends);
                    holder.txtKullaniciAdiFollowFriends.setText(otherUser.getUserName());


                    holder.btnKaldırFollowFriends.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteUserFollowList(position);
                        }
                    });

                    holder.cardViewFollowFrieds.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (holder.txtAdresFollowFriends.getText().equals("Konum Bilgisi Yok")) {
                                Snackbar.make(holder.cardViewFollowFrieds, "Bu Kişinin Konum Bilgisi Yok", 3000).show();

                            } else {

                                otherUserId = dataSnapshot.getKey();
                                locationDatabaseReference.child(otherUserId).orderByValue().limitToLast(1).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        Location otherUserLocation = dataSnapshot.getValue(Location.class);

                                        Double latitude1 = Double.parseDouble(otherUserLocation.getLatitude().toString().trim());
                                        Double longitude1 = Double.parseDouble(otherUserLocation.getLongitude().toString().trim());

                                        Intent intent = new Intent(mContext, MapsActivity.class);
                                        intent.putExtra("latitude", latitude1);
                                        intent.putExtra("longitude", longitude1);
                                        intent.putExtra("userName", holder.txtKullaniciAdiFollowFriends.getText());
                                        intent.putExtra("image", holder.profileImageFollowFriends.toString());
                                        mContext.startActivity(intent);
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
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        locationDatabaseReference.child(friendsKeyList.get(position)).orderByValue().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {


                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    location = dataSnapshot1.getValue(Location.class);
                    setLocation(location);

                }

            }


            public void setLocation(final Location location) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date date = new Date(Long.parseLong(location.getTime()));
                holder.txtZamanFollowFriends.setText(simpleDateFormat.format(date));

                String address = "";

                Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocation(Double.parseDouble(location.getLatitude()), Double.parseDouble(location.getLongitude()), 1);
                    for (Address adr : addressList) {
                        address += adr.getAddressLine(0);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                latitude = Double.parseDouble(location.getLatitude());
                longitude = Double.parseDouble(location.getLongitude());

                holder.txtAdresFollowFriends.setText(String.valueOf(String.format("%10.3f", latitude))
                        + " - " + String.valueOf(String.format("%10.3f", longitude)) + "\n\n" +
                        address);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return friendsKeyList.size();
    }


    public class cardViewHolder extends RecyclerView.ViewHolder {

        private CardView cardViewFollowFrieds;
        private CircleImageView profileImageFollowFriends;
        private TextView txtKullaniciAdiFollowFriends, txtAdresFollowFriends, txtZamanFollowFriends;
        private Button btnKaldırFollowFriends;

        public cardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewFollowFrieds = itemView.findViewById(R.id.cardViewFollowFrieds);
            profileImageFollowFriends = itemView.findViewById(R.id.profileImageFollowFriends);
            txtKullaniciAdiFollowFriends = itemView.findViewById(R.id.txtKullaniciAdiFollowFriends);
            txtAdresFollowFriends = itemView.findViewById(R.id.txtAdresFollowFriends);
            txtZamanFollowFriends = itemView.findViewById(R.id.txtZamanFollowFriends);
            btnKaldırFollowFriends = itemView.findViewById(R.id.btnKaldırFollowFriends);

        }
    }

    public void deleteUserFollowList(final int position) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);


        alertBuilder.setTitle("Kişi Listeden Kaldırılsın Mı?"); //başlık kısmı


        alertBuilder.setPositiveButton("KALDIR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeFriends(mUserId, friendsKeyList.get(position));
                Toast.makeText(mContext, "Kişi Silindi", Toast.LENGTH_LONG).show();

            }
        });

        alertBuilder.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertBuilder.create().show();


    }

    public void removeFriends(final String mUserId, final String otherUserId) {   //Arkadaş Listesinden Kişiyi Çıkarma
        friendsDatabaseReference.child(mUserId).child(otherUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                friendsDatabaseReference.child(otherUserId).child(mUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext, "Kişi Arkadaş Listesinden Silindi", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }
}
