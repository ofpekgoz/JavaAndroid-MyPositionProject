package com.omerfpekgoz.mypositionproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.omerfpekgoz.mypositionproject.R;

public class LoginActivity extends AppCompatActivity {

    private EditText txtEmailLogin, txtSifreLogin;
    private Button btnGirisYapLogin, btnYeniHesapLogin;

    FirebaseAuth auth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmailLogin = findViewById(R.id.txtEmailLogin);
        txtSifreLogin = findViewById(R.id.txtSifreLogin);
        btnGirisYapLogin = findViewById(R.id.btnGirisYapLogin);
        btnYeniHesapLogin = findViewById(R.id.btnYeniHesapLogin);

        auth=FirebaseAuth.getInstance();
        mUser=auth.getCurrentUser();

        btnYeniHesapLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        btnGirisYapLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    public void loginUser() {

        String email=txtEmailLogin.getText().toString().trim();
        String password=txtSifreLogin.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Snackbar.make(btnGirisYapLogin, "Email Giriniz", 3000).show();
            return;

        }
        if (!txtEmailLogin.getText().toString().contains("@")) {
            Snackbar.make(btnGirisYapLogin, "Lütfen Geçerli Bir Email Adresi Giriniz", 3000).show();
            return;

        }
        if (TextUtils.isEmpty(password)) {
            Snackbar.make(btnGirisYapLogin, "Şifre Giriniz", 3000).show();
            return;
        }

        btnGirisYapLogin.setEnabled(false);

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    Toast.makeText(LoginActivity.this, "Giriş Başarılı", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, FollowListActivity.class));
                    finish();
                }else{

                    Toast.makeText(LoginActivity.this, "Giriş Başarısız", Toast.LENGTH_LONG).show();
                    btnGirisYapLogin.setEnabled(true);
                }

            }
        });

    }
}
