package com.example.android.dalishboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

//SplashActivity displays Splash screen on launch while MainActivity loads
//It does not delay load time to display screen

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
