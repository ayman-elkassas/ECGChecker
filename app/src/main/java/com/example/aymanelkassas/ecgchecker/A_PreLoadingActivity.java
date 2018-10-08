package com.example.aymanelkassas.ecgchecker;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class A_PreLoadingActivity extends AppCompatActivity {

    private static int splashScreen=2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_pre_loading);

        //TODO:Delay for loading screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startAct();
                this.finish();
            }

            private void finish()
            {

            }
        },splashScreen);


    }

    @Override
    public void onBackPressed() {
        //TODO:nothing doing
    }

    void startAct()
    {
        Intent in=new Intent(this,Splash_Scren.class);
        //TODO:TO CLEAR ACTIVITY STACK
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
    }


}
