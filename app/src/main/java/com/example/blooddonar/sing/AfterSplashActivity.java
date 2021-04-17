package com.example.blooddonar.sing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.blooddonar.R;
import com.example.blooddonar.home.HomeActivity;

public class AfterSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_splash);
    }

    public void sing_in_after(View view) {
        Intent intent=new Intent(this,SignInActivity.class);
        startActivity(intent);
        finish();
    }

    public void skip_after(View view) {
        Intent intent=new Intent(this, HomeActivity.class);
        intent.putExtra("singWith","skip");
        startActivity(intent);
        finish();
    }
}
