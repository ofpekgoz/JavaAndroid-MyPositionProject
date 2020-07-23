package com.omerfpekgoz.mypositionproject.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.omerfpekgoz.mypositionproject.R;
import com.omerfpekgoz.mypositionproject.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.cardViewHolder>{

    private Context mContext;
    private List<String> userKeyList;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, referenceIstek;

    FirebaseAuth auth;
    FirebaseUser mUser;

    private String otherUserId;
    private String mUserId;
    private String checkValue = "";

    private FragmentManager fragmentManager;


    public AllUsersAdapter() {
    }

    public AllUsersAdapter(Context mContext, List<String> userKeyList) {
        this.mContext = mContext;
        this.userKeyList = userKeyList;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        referenceIstek = firebaseDatabase.getReference().child("Istekler");
        auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();

        mUserId = mUser.getUid();

    }

    @NonNull
    @Override
    public cardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_allusers, parent, false);
        return new cardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final cardViewHolder holder, int position) {


        databaseReference.child("Kullanicilar").child(userKeyList.get(position).toString()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {


                User user = dataSnapshot.getValue(User.class);
                if (!user.getUserName().equals("null")) {
                    Picasso.with(mContext).load(user.getImage()).into(holder.profileImageAllUsers);
                    holder.txtKullaniciAdiAllUsers.setText(user.getUserName());
                    holder.txtEmailAllUsers.setText(user.getEmail());


                    otherUserId = dataSnapshot.getKey();
                    checkRequest(otherUserId, mUserId, holder.btnArkadasEkleAllUsers);


                    holder.btnArkadasEkleAllUsers.setOnClickListener(new View.OnClickListener() {


                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View v) {


                            otherUserId = dataSnapshot.getKey();
                            checkRequestClick(mUserId, otherUserId, holder.btnArkadasEkleAllUsers);


                            if (!checkValue.equals("")) {
                                cancelRequest(otherUserId, mUserId);
                                holder.btnArkadasEkleAllUsers.setText("ARKADAŞ EKLE");
                                holder.btnArkadasEkleAllUsers.setBackgroundColor(mContext.getColor(R.color.btnarkadasekle));


                            } else {
                                addToFriend(otherUserId, mUserId);
                                holder.btnArkadasEkleAllUsers.setText("İSTEK GÖNDERİLDİ");
                                holder.btnArkadasEkleAllUsers.setBackgroundColor(mContext.getColor(R.color.colorPrimary));

                            }

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return userKeyList.size();
    }


    public class cardViewHolder extends RecyclerView.ViewHolder {
        private CardView cardViewAllUsers;
        private CircleImageView profileImageAllUsers;
        private TextView txtKullaniciAdiAllUsers, txtEmailAllUsers;
        private Button btnArkadasEkleAllUsers;

        public cardViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewAllUsers = itemView.findViewById(R.id.cardViewAllUsers);
            profileImageAllUsers = itemView.findViewById(R.id.profileImageAllUsers);
            txtKullaniciAdiAllUsers = itemView.findViewById(R.id.txtKullaniciAdiAllUsers);
            txtEmailAllUsers = itemView.findViewById(R.id.txtEmailAllUsers);
            btnArkadasEkleAllUsers = itemView.findViewById(R.id.btnArkadasEkleAllUsers);

        }
    }

    public void addToFriend(final String otherUserId, final String mUserId) {
        referenceIstek.child(mUserId).child(otherUserId).child("value").setValue("Istek Gönderildi").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    referenceIstek.child(otherUserId).child(mUserId).child("value").setValue("Istek Alındı").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                checkValue = "Istek Alındı";
                                Toast.makeText(mContext, "İstek Gönderildi", Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(mContext, "İstek Gönderilemedi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(mContext, "İstek Gönderilemedi-Sorun Var", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

    public void checkRequest(final String otherUserId, final String mUserId, final Button button) {   //İstek Kontrol İlk Başta


        referenceIstek.child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mUserId)) {
                    checkValue = dataSnapshot.child(mUserId).child("value").getValue().toString();
                    button.setText("İSTEK GÖNDERİLDİ");
                    button.setBackgroundColor(mContext.getColor(R.color.colorPrimary));


                } else {

                    button.setText("ARKADAŞ EKLE");
                    button.setBackgroundColor(mContext.getColor(R.color.btnarkadasekle));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        isFollowingMUser(mUserId, otherUserId, button);
        isFollowingOtherUser(mUserId, otherUserId, button);


    }

    public void checkRequestClick(final String otherUserId, final String mUserId, final Button button) {   //Arkadaş ekle butona basıldığında kontrol
        referenceIstek.child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mUserId)) {
                    checkValue = dataSnapshot.child(mUserId).child("value").getValue().toString();


                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void cancelRequest(final String otherUserId, final String mUserId) {

        referenceIstek.child(otherUserId).child(mUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                referenceIstek.child(mUserId).child(otherUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        checkValue = "";
                        Toast.makeText(mContext, "İstek İptal Edildi", Toast.LENGTH_SHORT).show();
                        new AllUsersAdapter().notifyDataSetChanged();

                    }
                });
            }
        });

    }

    public void isFollowingMUser(String mUserId, final String otherUserId, final Button button) {   //mUser için Takip Ediliyor mu Kontrol
        databaseReference.child("Arkadaslar").child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(otherUserId)) {

                    button.setText("TAKİP EDİLİYOR");
                    button.setBackgroundColor(mContext.getColor(R.color.btntakipediliyor));
                    button.setEnabled(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void isFollowingOtherUser(final String mUserId, final String otherUserId, final Button button) {  //OtherUser için takip ediliyor mu Kontrol
        databaseReference.child("Arkadaslar").child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mUserId)) {

                    button.setText("TAKİP EDİLİYOR");
                    button.setBackgroundColor(mContext.getColor(R.color.btntakipediliyor));
                    button.setEnabled(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}
