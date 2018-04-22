package com.example.dinesh.btranslate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.dinesh.btranslate.Blind.BlindCamera;
import com.example.dinesh.btranslate.People.MainActivity;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //SharedPrefernces to save type of User
        sharedPreferences=getSharedPreferences("usertype", Context.MODE_PRIVATE);
        final String type=sharedPreferences.getString("type","");


//<------------------------------------------------------------->
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                if(type.equals("1")){
                    Intent intent = new Intent(SplashActivity.this, BlindCamera.class);
                    startActivity(intent);
                    finish();;
                }else if(type.equals("2")){

                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent i = new Intent(SplashActivity.this, Decision_Activity.class);
                    Toast.makeText(SplashActivity.this, "Decision Activity", Toast.LENGTH_SHORT).show();
                    startActivity(i);
                    finish();// This method will be executed once the timer is over
                }

                // close this activity

            }
        }, SPLASH_TIME_OUT);
    }


}
