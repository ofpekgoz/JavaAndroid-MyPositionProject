package com.omerfpekgoz.mypositionproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.omerfpekgoz.mypositionproject.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText txtKullaniciAdiRegister, txtEmailRegister, txtSifreRegister;
    private Button btnYeniHesapRegister, btnHesabinizleGirisRegister;

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtKullaniciAdiRegister = findViewById(R.id.txtKullaniciAdiRegister);
        txtEmailRegister = findViewById(R.id.txtEmailRegister);
        txtSifreRegister = findViewById(R.id.txtSifreRegister);
        btnYeniHesapRegister = findViewById(R.id.btnYeniHesapRegister);
        btnHesabinizleGirisRegister = findViewById(R.id.btnHesabinizleGirisRegister);

        auth = FirebaseAuth.getInstance();

        btnHesabinizleGirisRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnYeniHesapRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
    }

    public void createNewAccount() {

        final String userName = txtKullaniciAdiRegister.getText().toString();
        final String email = txtEmailRegister.getText().toString();
        final String password = txtSifreRegister.getText().toString();

        if (TextUtils.isEmpty(userName)) {
            Snackbar.make(btnYeniHesapRegister, "Kullanıcı Adı Giriniz", 3000).show();
            return;

        }
        if (TextUtils.isEmpty(email)) {
            Snackbar.make(btnYeniHesapRegister, "Email Adresi Giriniz", 3000).show();
            return;

        }
        if (!txtEmailRegister.getText().toString().contains("@")) {
            Snackbar.make(btnYeniHesapRegister, "Lütfen Geçerli Bir Email Adresi Giriniz", 3000).show();
            return;

        }
        if (TextUtils.isEmpty(password)) {
            Snackbar.make(btnYeniHesapRegister, "Şifre Giriniz", 3000).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    insertUserInfo(userName,email,password);
                    Toast.makeText(RegisterActivity.this, "Hesabınız Başarılı Şekilde Oluşturuldu", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                }else{
                    if (task.getException().getMessage().toString().equals("The given password is invalid.")) {
                        Snackbar.make(btnYeniHesapRegister, "Şifre 6 Karakterden Az Olamaz", 3000).show();
                    }

                    Log.e("mesaj",task.getException().getMessage().toString());
                    Toast.makeText(RegisterActivity.this, "Hesap Oluşturulamadı", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void insertUserInfo(String userName, String email, String password) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Kullanicilar").child(auth.getUid());

        Map<String, String> users = new HashMap<>();
        users.put("userName", userName);
        users.put("email", email);
        users.put("password", password);
        users.put("image", "null");

        databaseReference.setValue(users);
    }
}
