package com.omerfpekgoz.mypositionproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.omerfpekgoz.mypositionproject.R;
import com.omerfpekgoz.mypositionproject.model.User;
import com.omerfpekgoz.mypositionproject.utils.RandomName;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private ImageView imgAddPhoto;
    private EditText txtKullaniciAdiProfil, txtEmailProfil, txtParolaProfil;
    private Button btnGuncelleProfil;

    String imageUrl;

    FirebaseAuth auth;
    FirebaseUser mUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profileImage);
        imgAddPhoto = findViewById(R.id.imgAddPhoto);
        txtKullaniciAdiProfil = findViewById(R.id.txtKullaniciAdiProfil);
        txtEmailProfil = findViewById(R.id.txtEmailProfil);
        txtParolaProfil = findViewById(R.id.txtParolaProfil);
        btnGuncelleProfil = findViewById(R.id.btnGuncelleProfil);

        auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Kullanicilar").child(mUser.getUid());

        getUserInfo();

        imgAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhotosGallery();
            }
        });
        btnGuncelleProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });
    }

    public void getUserInfo() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                txtKullaniciAdiProfil.setText(user.getUserName());
                txtEmailProfil.setText(user.getEmail());
                txtParolaProfil.setText(user.getPassword());
                imageUrl = user.getImage();

                if (!user.getImage().equals("null")) {
                    Picasso.with(getApplicationContext()).load(imageUrl).into(profileImage);
                }else{

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateUserInfo() {

        String userName = txtKullaniciAdiProfil.getText().toString();
        String email = txtEmailProfil.getText().toString();
        String password = txtParolaProfil.getText().toString();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Kullanicilar").child(auth.getUid());

        Map<String, String> users = new HashMap<>();

        users.put("userName", userName);
        users.put("email", email);
        users.put("password", password);

        if (imageUrl.equals("null")) {
            users.put("image", "null");
        } else {
            users.put("image", imageUrl);
        }

        databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "Bilgiler Güncellendi", Toast.LENGTH_LONG).show();
                getUserInfo();
            }
        });

        //mUser.updatePassword(password);
        //mUser.updateEmail(email);

    }

    public void openPhotosGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 5);

    }

    public void onActivityResult(int requestCode, int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && resultCode == Activity.RESULT_OK) {
            final Uri filePath = data.getData();
            final StorageReference ref = storageReference.child("KullaniciResimleri").child(RandomName.getSlatString() + ".jpg");
            ref.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Resim Güncellendi", Toast.LENGTH_LONG).show();

                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageUrl = uri.toString();
                                updateUserInfo();
                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), "Resim Güncelenemedi", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
