package com.example.aymanelkassas.ecgchecker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class F_SendDataToDoctor extends AppCompatActivity {

    EditText to,content;
    Button send;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase,mDoctor,mPatient,mDevice;
    static Integer count =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f__send_data_to_doctor);

        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mDoctor= mDatabase.child("doctor");
        mPatient= mDatabase.child("patient");
        mDevice = mDatabase.child("device");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Send");
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        to= (EditText) findViewById(R.id.to);
        content= (EditText) findViewById(R.id.content);
        send= (Button) findViewById(R.id.sendNotificate);

        Bundle b=getIntent().getExtras();
        final String id=b.getString("did");
        String dname=b.getString("dname");

        to.setText(dname);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Date date=new Date();
                SimpleDateFormat formatTime=new SimpleDateFormat("hh:mm aa");

                final String strTime=formatTime.format(date);

                mDevice.child(mAuth.getCurrentUser().getUid()).setValue(id);
                final String message=content.getText().toString();

//                mDoctor.child(id).child("notify").child(mAuth.getCurrentUser().getUid()).child("from").setValue(mAuth.getCurrentUser().getUid());
//                mDoctor.child(id).child("notify").child(mAuth.getCurrentUser().getUid()).child("message_content").setValue(message);
//                mDoctor.child(id).child("notify").child(mAuth.getCurrentUser().getUid()).child("time").setValue(strTime);
//                //device should be append here readings
//
                DatabaseReference ref=mDoctor.child(id).child("drafts").push();
                ref.child("from").setValue(mAuth.getCurrentUser().getUid());
                ref.child("message_content").setValue(message);
                ref.child("time").setValue(strTime);
                //device should be append here readings

                mDoctor.child(id).child("email").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String email=dataSnapshot.getValue(String.class);
                        oneSignalNotify.sendNotification(email,message,1);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

//                //store again in patient in sent table
                mPatient.child(mAuth.getCurrentUser().getUid()).child("sent").child(mAuth.getCurrentUser().getUid()).child("to").setValue(id);
                mPatient.child(mAuth.getCurrentUser().getUid()).child("sent").child(mAuth.getCurrentUser().getUid()).child("message_content").setValue(message);
                mPatient.child(mAuth.getCurrentUser().getUid()).child("sent").child(mAuth.getCurrentUser().getUid()).child("time").setValue(strTime);
                //device should be append here readings

//                Intent in =new Intent(F_SendDataToDoctor.this,E_patientProfile.class);
//                startActivity(in);

                Toast.makeText(F_SendDataToDoctor.this, "Send Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //for options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
        }

        switch (item.getItemId())
        {
            case R.id.quickly:
                Toast.makeText(this, "Determine time quickly", Toast.LENGTH_SHORT).show();
                Intent in=new Intent(this,Appointment.class);
                startActivity(in);
                break;
            case R.id.rosheta:
                Toast.makeText(this, "Write Rosheta", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(this,Rosheta.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
