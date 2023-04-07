package com.example.collegehelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
  LottieAnimationView lottieAnimationView;
  ImageView textView;
  LinearLayout textView2;
    @Override
    protected void onStart() {
        super.onStart();
        lottieAnimationView.playAnimation();
    }
//    app id- ca-app-pub-7925334082418787~1493869248
//    ad id - ca-app-pub-7925334082418787/9521103892
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        lottieAnimationView=findViewById(R.id.a1);
        textView=findViewById(R.id.a2);
        textView2=findViewById(R.id.a3);
        FirebaseApp.initializeApp(SplashScreen.this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                SafetyNetAppCheckProviderFactory.getInstance());
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                lottieAnimationView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                        if(firebaseUser!=null){
                            Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                            SplashScreen.this.startActivity(mainIntent);
                            SplashScreen.this.finish();
                        }
                        else{
                            Intent mainIntent = new Intent(SplashScreen.this, Login.class);
                            SplashScreen.this.startActivity(mainIntent);
                            SplashScreen.this.finish();
                        }
                    }
                }, 1600);
            }
        }, 2000);

    }
}