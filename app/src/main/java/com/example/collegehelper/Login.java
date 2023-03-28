package com.example.collegehelper;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    EditText email, pass;
    Button signin;
    ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        progressBar = findViewById(R.id.p1);
        email = findViewById(R.id.e1);
        pass = findViewById(R.id.e2);
        signin = findViewById(R.id.btnsin);
        auth = FirebaseAuth.getInstance();
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Toast.makeText(Login.this, "work", Toast.LENGTH_SHORT).show();
                String emailstr = email.getText().toString();
                String passwordstr = pass.getText().toString();
                if (TextUtils.isEmpty(emailstr) || TextUtils.isEmpty(passwordstr)) {
                    Toast.makeText(Login.this, "All Field Reuqired", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailstr).matches()) {
                    Toast.makeText(Login.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (passwordstr.length() < 6) {
                    Toast.makeText(Login.this, "Password minLength should be 6", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                login(emailstr, passwordstr);
            }
        });
    }

    private void login(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Logged In SuccessFully", Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(Login.this, MainActivity.class);
                    it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(it);
                    finish();
                } else {
                    Toast.makeText(Login.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    public void forgetpasss(View view) {
      if(email.getText().toString().isEmpty()){
          Toast.makeText(Login.this,"Enter The Email Address and then click on this",Toast.LENGTH_SHORT).show();
          return;
      }
      changepass(email.getText().toString());
    }

    private void changepass(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(Login.this,"Reset Password Email Sent",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(Login.this,"Error Occured",Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this,"Error Failed",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void hello(View view) {
        Intent it = new Intent(getApplicationContext(), Registeration.class);
        startActivity(it);
    }
}