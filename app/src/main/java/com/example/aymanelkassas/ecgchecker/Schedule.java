package com.example.aymanelkassas.ecgchecker;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Schedule extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference mDatabase,mDoctor,mPatient;

    ListView lstSchedule;

    ValueEventListener e0,e1,e2;
    String[] pName,pDate,pAvatar,pComment,pTime,id;

    int counter=0;
    int schedule_count=0;

    AbsoluteLayout coverSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Schedule");
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mDatabase= FirebaseDatabase.getInstance().getReference();
        mDoctor= mDatabase.child("doctor");
        mPatient= mDatabase.child("patient");

        mAuth=FirebaseAuth.getInstance();

        coverSchedule=(AbsoluteLayout)findViewById(R.id.coverSchedule);

        lstSchedule= (ListView) findViewById(R.id.lstSchedule);

        e1=mDoctor.child(mAuth.getUid()).child("schedule").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int count_schedule=(int)dataSnapshot.getChildrenCount();

                if(count_schedule!=0)
                {
                    coverSchedule.setVisibility(View.GONE);
                }

                pName=new String[(int)dataSnapshot.getChildrenCount()];//from id
                pDate=new String[(int)dataSnapshot.getChildrenCount()];
                pAvatar=new String[(int)dataSnapshot.getChildrenCount()];//from id
                pComment=new String[(int)dataSnapshot.getChildrenCount()];
                pTime=new String[(int)dataSnapshot.getChildrenCount()];

                id=new String[(int)dataSnapshot.getChildrenCount()];

                for (DataSnapshot c:dataSnapshot.getChildren()) {

                    if (schedule_count < count_schedule) {
                        id[schedule_count] = c.child("pid").getValue(String.class);
                        pName[schedule_count] = c.child("pname").getValue(String.class);
                        pDate[schedule_count] = c.child("date").getValue(String.class);
                        pAvatar[schedule_count] = c.child("pavatar").getValue(String.class);
                        pComment[schedule_count] = c.child("comment").getValue(String.class);
                        pTime[schedule_count] = c.child("time").getValue(String.class);

                        schedule_count++;
                    }
                }

                Schedule.myAdapter m = new Schedule.myAdapter(Schedule.this, android.R.layout.simple_list_item_1, pName);
                lstSchedule.setAdapter(m);
                lstSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Intent in = new Intent(Schedule.this, report.class);
                        Bundle b = new Bundle();
                        b.putString("id", id[(count_schedule-1)-i]);
                        b.putString("message_content", pComment[(count_schedule-1)-i]);
                        b.putString("full_name", pName[(count_schedule-1)-i]);
                        b.putString("avatar", pAvatar[(count_schedule-1)-i]);
                        b.putString("time", pDate[(count_schedule-1)-i]);
                        in.putExtras(b);
                        startActivity(in);

                        Toast.makeText(Schedule.this, pName[i] + " ," + pDate[i], Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    class myAdapter extends ArrayAdapter<String>
    {

        public myAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull String[] objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inf= (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View v=inf.inflate(R.layout.item,parent,false);

            CircleImageView av=v.findViewById(R.id.patientAvatar);

            Glide.with(getBaseContext())
                    .load(pAvatar[(schedule_count-1)-position])
                    .placeholder(R.drawable.avatar)
                    .into(av);

//            Picasso.with(getBaseContext()).load(pAvatar[(schedule_count-1)-position]).into(av);

            TextView mt= v.findViewById(R.id.senderName);
            mt.setText(pName[(schedule_count-1)-position]);

            TextView ms=v.findViewById(R.id.subject);
            ms.setText(pComment[(schedule_count-1)-position]);

            TextView mm=v.findViewById(R.id.job);
            mm.setText(pDate[(schedule_count-1)-position]);

            TextView mtime=v.findViewById(R.id.time);
            mtime.setText(pTime[(schedule_count-1)-position]+" p.m.");

            return v;
        }
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
