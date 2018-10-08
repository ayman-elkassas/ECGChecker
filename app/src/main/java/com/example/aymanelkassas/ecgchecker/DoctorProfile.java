package com.example.aymanelkassas.ecgchecker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.x;
import static android.R.attr.y;
import static java.security.AccessController.getContext;

public class DoctorProfile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int count=0;
    static Intent in=null;
    static boolean isRun=true;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase,mDoctor,mPatient;

    CircleImageView avatar;
    TextView FullnameDoctorProfile,mailDoctorProfile;
    ValueEventListener v;

    ProgressDialog cashed;

    ListView lst;
    LinearLayout content;

    ValueEventListener e1,e2,e3;
    static String[] senderName,subject,patientAvatar,job,time,ids;

    int counter=0;
    int draft_count=0;

    AbsoluteLayout cover;

    static public String LoggedIn_User_Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        mDatabase= FirebaseDatabase.getInstance().getReference();
        mDoctor= mDatabase.child("doctor");
        mPatient= mDatabase.child("patient");

        mAuth=FirebaseAuth.getInstance();

        cashed=new ProgressDialog(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LoggedIn_User_Email=mAuth.getCurrentUser().getEmail();
        OneSignal.sendTag("User_ID",LoggedIn_User_Email);


        cover=findViewById(R.id.cover);

//        mAuth.signOut();
        //TODO:ASSIGN HEADER PROFILE VALUES
        e3=mDoctor.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //TODO:SHOULD BE initialized HERE IN THIS SCOPE
                avatar=(CircleImageView)findViewById(R.id.DoctorAvatar);
                FullnameDoctorProfile=(TextView)findViewById(R.id.FullnameDoctorProfile);
                mailDoctorProfile=(TextView)findViewById(R.id.mailDoctorProfile);

                String path="";
                String fname="";
                String lname="";
                String email="";

                for (DataSnapshot ch:dataSnapshot.getChildren())
                {
                    if(ch.getKey().equals("avatar"))
                    {
                        path=ch.getValue(String.class);
                    }
                    else if(ch.getKey().equals("fname"))
                    {
                        fname=ch.getValue(String.class);
                    }
                    else if(ch.getKey().equals("lname"))
                    {
                        lname=ch.getValue(String.class);
                    }
                    else if(ch.getKey().equals("email"))
                    {
                        email=ch.getValue(String.class);
                    }
                }
                if(path!=null && fname!=null && lname!=null && email!=null )
                {
                    try
                    {
//                        Picasso.with(getBaseContext()).load(path).into(avatar);
                        Glide.with(getBaseContext())
                                .load(path)
                                .into(avatar);
                        FullnameDoctorProfile.setText(fname+" "+lname);
                        mailDoctorProfile.setText(email);


                    }
                    catch (Exception ex)
                    {

                    }

                }
                mDoctor.child(mAuth.getUid()).removeEventListener(e3);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        lst= (ListView) findViewById(R.id.lst);

        e1=mDoctor.child(mAuth.getUid()).child("drafts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int count_drafts=(int)dataSnapshot.getChildrenCount();

                if(count_drafts!=0)
                {
                    cover.setVisibility(View.GONE);
                }

                senderName=new String[(int)dataSnapshot.getChildrenCount()];
                subject=new String[(int)dataSnapshot.getChildrenCount()];
                patientAvatar=new String[(int)dataSnapshot.getChildrenCount()];
                job=new String[(int)dataSnapshot.getChildrenCount()];
                time=new String[(int)dataSnapshot.getChildrenCount()];

                ids=new String[(int)dataSnapshot.getChildrenCount()];

                for (DataSnapshot c:dataSnapshot.getChildren()) {
                    if(draft_count<count_drafts) {
                        subject[draft_count] = c.child("message_content").getValue(String.class);
                        ids[draft_count] = c.child("from").getValue(String.class);
                        time[draft_count] = c.child("time").getValue(String.class);
                    }

                    if(counter<count_drafts) {
                        if(ids.length>draft_count) {
                            e2 = mPatient.child(ids[draft_count]).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (counter < ids.length) {
                                        senderName[counter] = dataSnapshot.child("fname").getValue(String.class) + " " + dataSnapshot.child("lname").getValue(String.class);
                                        patientAvatar[counter] = dataSnapshot.child("avatar").getValue(String.class);
                                        job[counter] = dataSnapshot.child("job").getValue(String.class);
                                        counter++;
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    draft_count++;

                    Thread th =new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true)
                            {
                                if(senderName[count_drafts-1]!=null && patientAvatar[count_drafts-1]!=null &&job[count_drafts-1]!=null)
                                {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //Custom adapter
                                            myAdapter m=new myAdapter(DoctorProfile.this,android.R.layout.simple_list_item_1,senderName);
                                            lst.setAdapter(m);
                                            lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                                    Intent in =new Intent(DoctorProfile.this,report.class);
                                                    Bundle b=new Bundle();
                                                    b.putString("message_content",subject[(count_drafts-1)-i]);
                                                    b.putString("id",ids[(count_drafts-1)-i]);
                                                    b.putString("full_name",senderName[(count_drafts-1)-i]);
                                                    b.putString("avatar",patientAvatar[(count_drafts-1)-i]);
                                                    b.putString("time",time[(count_drafts-1)-i]);
                                                    in.putExtras(b);
                                                    startActivity(in);
                                                }
                                            });
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                    });

                    th.start();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Thread th=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    in =new Intent(DoctorProfile.this,myNotification.class);
                    startService(in);

                }catch (Exception e)
                {
                }
            }
        });

        th.start();


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
                    .load(patientAvatar[(draft_count-1)-position])
                    .placeholder(R.drawable.avatar)
                    .into(av);
//            Picasso.with(getBaseContext()).load(patientAvatar[position]).into(av);

            TextView mt= v.findViewById(R.id.senderName);
            mt.setText(senderName[(draft_count-1)-position]);

            TextView ms=v.findViewById(R.id.subject);
            ms.setText(subject[(draft_count-1)-position]);

            TextView mm=v.findViewById(R.id.job);
            mm.setText(job[(draft_count-1)-position]);

            TextView mtime=v.findViewById(R.id.time);
            mtime.setText(time[(draft_count-1)-position]);

            return v;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.doctor_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent in =new Intent(getBaseContext(),configurations.class);
            startActivity(in);
        }
        else if(id==R.id.logoutDoctor)
        {
//            stopService(in);

//            mDoctor.child(mAuth.getUid()).child("drafts").removeEventListener(e1);
            mAuth.signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.inbox) {
            Intent in =new Intent(getBaseContext(),DoctorProfile.class);
            startActivity(in);
        }
        else if(id==R.id.schedule)
        {
            Intent in =new Intent(this,Schedule.class);
            startActivity(in);
        }
        else if(id==R.id.recent_report)
        {
            Intent in =new Intent(getBaseContext(),DoctorProfile.class);
            startActivity(in);
        }
        else if(id==R.id.Myprofile)
        {

//            v=mDoctor.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    Bundle b=new Bundle();
//                    b.putString("fname",dataSnapshot.child("fname").getValue(String.class));
//                    b.putString("job",dataSnapshot.child("job").getValue(String.class));
//                    b.putString("bio",dataSnapshot.child("bio").getValue(String.class));
//                    b.putString("email",dataSnapshot.child("email").getValue(String.class));
//                    b.putString("location",dataSnapshot.child("location").getValue(String.class));
//                    b.putString("phone",dataSnapshot.child("phone").getValue(String.class));
//                    b.putString("working_hours",dataSnapshot.child("working_hours").getValue(String.class));
//                    mDoctor.child(mAuth.getUid()).removeEventListener(v);
//                    Intent in =new Intent(getBaseContext(),G_PrivateProfileDoctor.class);
//                    in.putExtras(b);
//                    startActivity(in);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
            Intent in =new Intent(getBaseContext(),DoctorProfile.class);
            startActivity(in);


        }
        else if(id==R.id.about)
        {
            Intent in =new Intent(getBaseContext(),AboutTeam.class);
            startActivity(in);
        }
        else if(id==R.id.setting)
        {
            Intent in =new Intent(getBaseContext(),configurations.class);
            startActivity(in);
        }
        else if(id==R.id.logoutDrawer)
        {
//            stopService(in);
            mAuth.signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
