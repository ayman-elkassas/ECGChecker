package com.example.aymanelkassas.ecgchecker;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

public class B_SplashScreen extends TutorialActivity {

    //TODO:Not Used THIS CLASS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addFragment(new Step.Builder().setTitle("Using IOT Technology")
                .setDrawable(R.drawable.heart1)
                .setContent(getString(R.string.section_format_1))
                .setBackgroundColor(Color.parseColor("#98ca97")) // int background color
                .build());

        addFragment(new Step.Builder().setTitle("Doctor can see")
                .setDrawable(R.drawable.doctor)
                .setContent(getString(R.string.section_format_2))
                .setBackgroundColor(Color.parseColor("#f2b682")) // int background color
                .build());

        addFragment(new Step.Builder().setTitle("Doctor can determine")
                .setDrawable(R.drawable.report)
                .setContent(getString(R.string.section_format_3))
                .setBackgroundColor(Color.parseColor("#94afe0")) // int background color
                .build());

    }

    @Override
    public void finishTutorial() {
        Intent in=new Intent(this,C_LoginChhose.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(in);
    }
}
