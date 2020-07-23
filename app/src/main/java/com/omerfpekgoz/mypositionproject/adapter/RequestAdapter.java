package com.omerfpekgoz.mypositionproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.cardViewHolder> {

    private Context mContext;
    private List<String> requestKeyList;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseAuth auth;
    FirebaseUser mUser;

    String mUserId;

    public RequestAdapter() {
    }

    public RequestAdapter(Context mContext, List<String> requestKeyList) {
        this.mContext = mContext;
        this.requestKeyList = requestKeyList;

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        mUserId = mUser.getUid();
    }

    @NonNull
    @Override
    public cardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_requests, parent, false);
        return new cardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final cardViewHolder holder, final int position) {

        databaseReference.child("Kullanicilar").child(requestKeyList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if (!user.getUserName().equals("null")) {

                    Picasso.with(mContext).load(user.getImage()).into(holder.profileImageRequest);
                    holder.txtKullaniciAdiRequest.setText(user.getUserName());
                    holder.txtEmailRequest.setText(user.getEmail());

                    holder.btnKabulEtRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirm(mUserId, requestKeyList.get(position));
                        }
                    });

                    holder.btnReddetRequest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss(mUserId, requestKeyList.get(position));
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
        return requestKeyList.size();
    }

    public class cardViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profileImageRequest;
        private TextView txtKullaniciAdiRequest, txtEmailRequest;
        private Button btnKabulEtRequest, btnReddetRequest;

        public cardViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageRequest = itemView.findViewById(R.id.profileImageRequest);
            txtKullaniciAdiRequest = itemView.findViewById(R.id.txtKullaniciAdiRequest);
            txtEmailRequest = itemView.findViewById(R.id.txtEmailRequest);
            btnKabulEtRequest = itemView.findViewById(R.id.btnKabulEtRequest);
            btnReddetRequest = itemView.findViewById(R.id.btnReddetRequest);
        }
    }

    public void confirm(final String mUserId, final String otherUserId) {   //Arkadaşlık isteği onaylama

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date today = Calendar.getInstance().getTime();
        final String date = df.format(today);

        databaseReference.child("Arkadaslar").child(mUserId).child(otherUserId).child("date").setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                databaseReference.child("Arkadaslar").child(otherUserId).child(mUserId).child("date").setValue(date).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(mContext, "İstek Kabul Edildi", Toast.LENGTH_LONG).show();
                        deleteRequestForConfirm(mUserId, otherUserId);
                    }
                });
            }
        });
    }

    public void dismiss(final String mUserId, final String otherUserId) {     //Arkadaşlık isteğini reddetme

        databaseReference.child("Istekler").child(mUserId).child(otherUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                databaseReference.child("Istekler").child(otherUserId).child(mUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(mContext, "İstek Reddildi", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    public void deleteRequestForConfirm(final String mUserId, final String otherUserId) {   //İstek kabul edilirse istek tablosundan isteği silme
        databaseReference.child("Istekler").child(mUserId).child(otherUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                databaseReference.child("Istekler").child(otherUserId).child(mUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });

            }
        });
    }
}
