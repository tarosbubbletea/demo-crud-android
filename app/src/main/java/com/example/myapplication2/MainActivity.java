package com.example.myapplication2;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        Button b = findViewById(R.id.btnLoginMenu);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .setCustomAnimations(
                                R.anim.slide_in_right,  // enter
                                R.anim.fade_out,  // exit
                                R.anim.fade_in,   // popEnter
                                R.anim.slide_out_right  // popExit
                        )
                        .add(R.id.FrameContainer, LoginFragment.class, null)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    public void CallMenuActivity(View view) {
        startActivity(new Intent(this, MenuActivity.class));
        finish();
    }

}