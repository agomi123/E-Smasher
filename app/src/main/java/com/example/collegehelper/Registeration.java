package com.example.collegehelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Registeration extends AppCompatActivity {
//name,email,year,branch.joined society , college,password
    EditText name,email,year,branch,mobile,colleg,password;
    CheckBox checkBox;
    Button signup,verifymobile;
    Intent it;
    ProgressBar progressBar;
    FirebaseAuth auth;
    PinView pinView;
    TextView number;
    DatabaseReference reference;
    LinearLayout l1;
   static String id,codes;
    RelativeLayout r1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.darker_gray));
        progressBar=findViewById(R.id.a5);
        name=findViewById(R.id.n1);
        email=findViewById(R.id.n2);
        year=findViewById(R.id.n3);
        branch=findViewById(R.id.n4);
        mobile=findViewById(R.id.n5);
        colleg=findViewById(R.id.n6);
        password=findViewById(R.id.n7);
        checkBox=findViewById(R.id.checkBox);
        signup=findViewById(R.id.btnsignup);
        l1=findViewById(R.id.l1);
        r1=findViewById(R.id.r1);
        verifymobile=findViewById(R.id.v4);
        pinView=findViewById(R.id.v5);
        number=findViewById(R.id.v6);
        auth=FirebaseAuth.getInstance();
        verifymobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    Toast.makeText(VerificationActivity.this,pinView.getText().toString(),Toast.LENGTH_SHORT).show();
                verifyCode(pinView.getText().toString(), id);
            }
        });
signup.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Toast.makeText(Registeration.this, "Wait Registering✋🏻", Toast.LENGTH_SHORT).show();
        String namestr = name.getText().toString();
        String yearstr = year.getText().toString();
        String branchstr = branch.getText().toString();
        String collegestr = colleg.getText().toString();
        String mobilestr = mobile.getText().toString();
        String emailstr = email.getText().toString();
        String passwordstr = password.getText().toString();
        if (TextUtils.isEmpty(namestr) || TextUtils.isEmpty(yearstr) || TextUtils.isEmpty(emailstr) || TextUtils.isEmpty(mobilestr) || TextUtils.isEmpty(passwordstr)
                || TextUtils.isEmpty(branchstr) || TextUtils.isEmpty(collegestr)
        ) {
            Toast.makeText(Registeration.this, "All Field Reuqired", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailstr).matches()) {
            Toast.makeText(Registeration.this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passwordstr.length() < 6) {
            Toast.makeText(Registeration.this, "Password minLength should be 6", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mobilestr.length() != 10) {
            Toast.makeText(Registeration.this, "Mobile No should be of 10 digit", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!checkBox.isChecked()) {
            Toast.makeText(Registeration.this, "Agree to T&C?", Toast.LENGTH_SHORT).show();
            return;
        }
        register(namestr, emailstr, passwordstr,mobilestr,yearstr,branchstr,collegestr);
    }
});



    }
    private void register(String name, String email, String password, String mobile, String year,String branch,String college) {
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String userid = firebaseUser.getUid();
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", userid);
                    hashMap.put("name", name);
                    hashMap.put("email", email);
                    hashMap.put("mobile", mobile);
                    hashMap.put("password", password);
                    hashMap.put("year", year);
                    hashMap.put("branch",branch);
                    hashMap.put("college",college);
                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Registeration.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                sendVerificationCode(mobile);
                                number.setText(mobile);
                                l1.setVisibility(View.GONE);
                                r1.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } else {
                    Toast.makeText(Registeration.this, "Some error occured", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Registeration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        progressBar.setVisibility(View.GONE);

    }
    private void verifyCode(String code, String id) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            verifymobile.setText("OTp Verified");
                            Intent it=new Intent(Registeration.this,Login.class);
                            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            Toast.makeText(Registeration.this, "Please Login Again", Toast.LENGTH_SHORT).show();
                            startActivity(it);
                            finish();

                        } else {
                            Toast.makeText(Registeration.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public void tandc(View view) {
        Uri uri = Uri.parse("https://www.freeprivacypolicy.com/live/e749e3fc-efb1-4a34-9552-71424970f4b3"); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }
    private void sendVerificationCode(String number) {
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber("+91"+number)            // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            id=s;
            Toast.makeText(Registeration.this, "Otp Send to your mobile no", Toast.LENGTH_LONG).show();
        }
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                codes=code;
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            // displaying error message with firebase exception.
            Toast.makeText(Registeration.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };


    public void hello(View view) {
        Intent it=new Intent(getApplicationContext(),Login.class);
        startActivity(it);
    }
}