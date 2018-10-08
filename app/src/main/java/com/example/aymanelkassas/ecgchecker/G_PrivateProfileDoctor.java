package com.example.aymanelkassas.ecgchecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class G_PrivateProfileDoctor extends AppCompatActivity {

    connectionDetector cd;

    FirebaseAuth mAuth;
    DatabaseReference mRef, mPatients, mDoctors, mDevice;

    TextView name,job,bio,email,location,phone,working_hours;
    CircleImageView avatar;

    Bundle b;

    static Integer count=0;

    static boolean flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g__private_profile_doctor);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("profile");
//        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        mPatients = mRef.child("patient");
        mDoctors = mRef.child("doctor");
        mDevice = mRef.child("device");

        cd=new connectionDetector(this);

        name= (TextView) findViewById(R.id.privateFullnameDoctor);
        job= (TextView) findViewById(R.id.privateJobDoctor);
        bio= (TextView) findViewById(R.id.privateBioDoctor);
        email= (TextView) findViewById(R.id.privateMailDoctor);
        location= (TextView) findViewById(R.id.privateLocationDoctor);
        phone= (TextView) findViewById(R.id.privatePhoneDoctor);
        working_hours= (TextView) findViewById(R.id.privateTimeWorking);

        avatar= (CircleImageView) findViewById(R.id.privateAvatar);

        b= getIntent().getExtras();
        name.setText(b.getString("fname")+" "+b.getString("lname"));
        job.setText(b.getString("job"));
        bio.setText(b.getString("bio"));
        email.setText(b.getString("email"));
        location.setText(b.getString("location"));
        phone.setText(b.getString("phone"));
        working_hours.setText(b.getString("working_hours"));

        Picasso.with(getBaseContext()).load(b.getString("avatar")).into(avatar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if(cd.isConnected()) {

                    final String uid = mAuth.getUid();

                    final String doctor_id = b.getString("id");

                    mDoctors.child(doctor_id).child("my_patients").child(uid).setValue(uid);
                    mPatients.child(uid).child("my_doctors").child(doctor_id).setValue(doctor_id);
                    Snackbar.make(view, "Add successfully to your network", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                }
                else {
                    Snackbar.make(view, "No internet connection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }


            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in =new Intent(G_PrivateProfileDoctor.this,E_patientProfile.class);
        startActivity(in);
    }
}
