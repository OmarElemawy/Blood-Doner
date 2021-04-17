package com.example.blooddonar.sing;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.blooddonar.R;
import com.example.blooddonar.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);
        linearLayout=findViewById(R.id.splash_layout);
        mAuth = FirebaseAuth.getInstance();
        Animation animation= AnimationUtils.loadAnimation(this,R.anim.bounce);
        animation.setDuration(1500);
        linearLayout.startAnimation(animation);

        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {

                FirebaseUser currentUser = mAuth.getCurrentUser();
                updateUI(currentUser);
            }
        }.start();
    }
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser==null)
        {
            Intent intent=new Intent(this,AfterSplashActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            Intent intent=new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
