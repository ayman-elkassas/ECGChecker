package com.example.aymanelkassas.ecgchecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Appointment extends AppCompatActivity {

    TextView timePatientName;
    EditText timeComment;
    CalendarView timeCalendar;
    TimePicker timeTime;
    Button timeSend;

    String date="";
    String davatar,dfname,dlname,pavatar="";

    FirebaseAuth mAuth;
    DatabaseReference mDatabase,mDoctor,mPatient,mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Appointment");
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mDoctor= mDatabase.child("doctor");
        mPatient= mDatabase.child("patient");
        mDevice = mDatabase.child("device");

        timePatientName=findViewById(R.id.timePatientName);
        timeComment=findViewById(R.id.timeComment);
        timeCalendar=findViewById(R.id.timeCalendar);
        timeTime=findViewById(R.id.timeTime);
        timeSend=findViewById(R.id.timeSend);

        final Bundle b=getIntent().getExtras();

        timePatientName.setText(b.getString("full_name"));

        final String d_id=mAuth.getUid();
        final String p_id=b.getString("id");

        timeCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                date=i2+"/"+(i1+1)+"/"+i;
            }
        });



        mDoctor.child(d_id).child("avatar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                davatar=dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDoctor.child(d_id).child("fname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dfname=dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDoctor.child(d_id).child("lname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dlname=dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mPatient.child(p_id).child("avatar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pavatar=dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        timeSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final int hour=timeTime.getHour();
                final int minute=timeTime.getMinute();

                final String comment=timeComment.getText().toString();

//                //TODO:DOCTOR SAVING
                final DatabaseReference ref=mDoctor.child(d_id).child("schedule").push();
                ref.child("pid").setValue(p_id);
                ref.child("date").setValue(date);
                ref.child("time").setValue(hour+":"+minute);
                ref.child("comment").setValue(comment);
                ref.child("pname").setValue(b.getString("full_name"));
                ref.child("pavatar").setValue(pavatar);

//                //TODO:Patient Saving
                final DatabaseReference reference=mPatient.child(p_id).child("notify").child(p_id);
                reference.child("did").setValue(d_id);
                reference.child("date").setValue(date);
                reference.child("time").setValue(hour+":"+minute);
                reference.child("comment").setValue(comment);

                reference.child("davatar").setValue(davatar);
                reference.child("dfname").setValue(dfname);
                reference.child("dlname").setValue(dlname);

                mPatient.child(p_id).child("email").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String email=dataSnapshot.getValue(String.class);
                        oneSignalNotify.sendNotification(email,comment,0);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                //TODO:CONTINUE AND SEND DATA

                Toast.makeText(Appointment.this, "Saving Done", Toast.LENGTH_SHORT).show();

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
}
