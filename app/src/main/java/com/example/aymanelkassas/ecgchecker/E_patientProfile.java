package com.example.aymanelkassas.ecgchecker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;
import android.app.FragmentTransaction;

public class E_patientProfile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    connectionDetector cd;

    LinearLayout CON;

    CircleButton connect;
    private static int loading = 3000;
    private ProgressDialog pd;

    static CircleImageView avatar;
    TextView FullnamePatientProfile,mailPatientProfile;

    MaterialSearchView materialSearchView;

    FirebaseAuth mAuth;
    DatabaseReference mRef, mPatients, mDoctors, mDevice;

    static ArrayList<DoctorsInfo> ArrDoctors = new ArrayList<DoctorsInfo>();
    static int count =0;

    private ValueEventListener e1,e2;

    public boolean checkPatient=true;

    static String LoggedIn_User_Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_patient_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");

        setTitle("Home");
        homeFragment homeFrag=new homeFragment();
        android.support.v4.app.FragmentTransaction tran1=getSupportFragmentManager().beginTransaction();
        tran1.replace(R.id.frag,homeFrag);
        tran1.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        pd = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        mPatients = mRef.child("patient");
        mDoctors = mRef.child("doctor");
        mDevice = mRef.child("device");

        LoggedIn_User_Email=mAuth.getCurrentUser().getEmail();
        OneSignal.sendTag("User_ID",LoggedIn_User_Email);

        cd = new connectionDetector(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        mAuth.signOut();

        if(cd.isConnected() && checkPatient) {
            //TODO:ASSIGN HEADER PROFILE VALUES
            checkPatient=true;
            e1 = mPatients.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //TODO:SHOULD BE initialized HERE IN THIS SCOPE
                    avatar = (CircleImageView) findViewById(R.id.PatientAvatar);
                    FullnamePatientProfile = (TextView) findViewById(R.id.FullnamePatientProfile);
                    mailPatientProfile = (TextView) findViewById(R.id.mailPatientProfile);

                    String path = null;
                    String fname = null;
                    String lname = null;
                    String email = null;

                    for (DataSnapshot ch : dataSnapshot.getChildren()) {
                        if (ch.getKey().equals("avatar")) {
                            path = ch.getValue(String.class);
                        } else if (ch.getKey().equals("fname")) {
                            fname = ch.getValue(String.class);
                        } else if (ch.getKey().equals("lname")) {
                            lname = ch.getValue(String.class);
                        } else if (ch.getKey().equals("email")) {
                            email = ch.getValue(String.class);
                        }
                    }
                    if (path != null && fname != null && lname != null && email != null) {
                        try
                        {
                            Picasso.with(getBaseContext()).load(path).into(avatar);
                            FullnamePatientProfile.setText(fname + " " + lname);
                            mailPatientProfile.setText(email);

                        }
                        catch (Exception ex)
                        {

                        }

                    }

                    mPatients.child(mAuth.getUid()).removeEventListener(e1);

                }
//
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            CON = (LinearLayout) findViewById(R.id.CON);

//            materialSearchView = (MaterialSearchView) findViewById(R.id.mysearch);
//            //TODO:GET ALL DOCTORS AND PENDING TO ARRAYLIST
//
//            e2=mRef.child("doctor").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    final ArrayList<DoctorsInfo> doctors = new ArrayList<>();
//                    //TODO:any JSON OBJECT contain some another children CALLED dataSnapshot
//                    for (DataSnapshot ch : dataSnapshot.getChildren()) {
//
//                        DoctorsInfo DI = new DoctorsInfo();
//                        DI.setId((ch.child("id")).getValue(String.class));
//                        DI.setFname(ch.child("fname").getValue(String.class));
//                        DI.setLname(ch.child("lname").getValue(String.class));
//                        DI.setMail(ch.child("email").getValue(String.class));
//                        DI.setAvatar(ch.child("avatar").getValue(String.class));
//                        DI.setBio(ch.child("bio").getValue(String.class));
//                        DI.setJob(ch.child("job").getValue(String.class));
//                        DI.setLocation(ch.child("location").getValue(String.class));
//                        DI.setPhone(ch.child("phone").getValue(String.class));
//                        DI.setWorking_hours(ch.child("working_hours").getValue(String.class));
//
//                        doctors.add(DI);
//                    }
//
//                    //TODO:get name of doctors
//                    final String[] nameOfDoctors = new String[doctors.size()];
//
//                    for (int i = 0; i < doctors.size(); i++) {
//                        nameOfDoctors[i] = doctors.get(i).getFname() + " " + doctors.get(i).getLname();
//                    }
//                    srearchengine(nameOfDoctors, doctors);
////                    mRef.child("doctor").removeEventListener(this);
////                    mRef.removeEventListener(this);
//                    mRef.child("doctor").removeEventListener(e2);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
        }
    }

    //TODO:Search engine
//    public void srearchengine(final String [] nameOfDoctors,final ArrayList<DoctorsInfo> Doctor_info) {
//
//        materialSearchView.clearFocus();
//        materialSearchView.setSuggestions(nameOfDoctors);
//        materialSearchView.setHint("Search doctors");
//        materialSearchView.setHintTextColor(R.color.colorPrimary);
//
//        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
//            @Override
//            public void onSearchViewShown() {
////                CON.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onSearchViewClosed() {
////                CON.setVisibility(View.VISIBLE);
//            }
//        });
//
//        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
////                CON.setVisibility(View.INVISIBLE);
//
//                for(int i=0;i<Doctor_info.size();i++)
//                {
//                    if(((Doctor_info.get(i).getFname())+" "+(Doctor_info.get(i).getLname())).equals(query))
//                    {
//                        Bundle b=new Bundle();
//                        b.putString("id",Doctor_info.get(i).getId());
//                        b.putString("fname",Doctor_info.get(i).getFname());
//                        b.putString("lname",Doctor_info.get(i).getLname());
//                        b.putString("job",Doctor_info.get(i).getJob());
//                        b.putString("bio",Doctor_info.get(i).getBio());
//                        b.putString("email",Doctor_info.get(i).getMail());
//                        b.putString("location",Doctor_info.get(i).getLocation());
//                        b.putString("phone",Doctor_info.get(i).getPhone());
//                        b.putString("working_hours",Doctor_info.get(i).getWorking_hours());
//                        b.putString("avatar",Doctor_info.get(i).getAvatar());
//
//                        Intent in = new Intent(E_patientProfile.this, G_PrivateProfileDoctor.class);
//                        in.putExtras(b);
//                        startActivity(in);
//                    }
//                }
//
//                return false;
//            }
//
//            //TODO:For Real time filtering
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });

//    }

    void startAct()
    {
        pd.dismiss();
        Intent in=new Intent(this,NetworkDoctors.class);
        startActivity(in);
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
        getMenuInflater().inflate(R.menu.e_patient_profile, menu);

//        MenuItem item = menu.findItem(R.id.search_button);
//        materialSearchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.logout2)
        {
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

        if (id == R.id.connect1) {
            // Handle the camera action
            setTitle("Home");
            homeFragment homeFrag=new homeFragment();
            android.support.v4.app.FragmentTransaction tran1=getSupportFragmentManager().beginTransaction();
            tran1.replace(R.id.frag,homeFrag);
            tran1.commit();

        } else if (id == R.id.doctors) {
            setTitle("Doctors");
            settingFragment settingFragment=new settingFragment();
            android.support.v4.app.FragmentTransaction tran4=getSupportFragmentManager().beginTransaction();
            tran4.replace(R.id.frag,settingFragment);
            tran4.commit();
//            searchDoctors();
        } else if (id == R.id.nearHosp) {

            setTitle("Search");
            searchFragment searchFrag=new searchFragment();
            android.support.v4.app.FragmentTransaction tran2=getSupportFragmentManager().beginTransaction();
            tran2.replace(R.id.frag,searchFrag);
            tran2.commit();

        } else if (id == R.id.logout1) {
            mAuth.signOut();
        }else if (id == R.id.about1) {
            Intent in =new Intent(getBaseContext(),AboutTeam.class);
            startActivity(in);
        }else if (id == R.id.setting1) {
            Intent in =new Intent(getBaseContext(),configurations.class);
            startActivity(in);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setTitle("Home");
                    homeFragment homeFrag=new homeFragment();
                    android.support.v4.app.FragmentTransaction tran1=getSupportFragmentManager().beginTransaction();
                    tran1.replace(R.id.frag,homeFrag);
                    tran1.commit();
                    return true;
                case R.id.navigation_search:
                    setTitle("Search");
                    searchFragment searchFrag=new searchFragment();
                    android.support.v4.app.FragmentTransaction tran2=getSupportFragmentManager().beginTransaction();
                    tran2.replace(R.id.frag,searchFrag);
                    tran2.commit();

//                    Intent in=new Intent(E_patientProfile.this,HospitalsNearby.class);
//                    startActivity(in);
                    return true;
                case R.id.navigation_notifications:
                    setTitle("Notifications");
                    notificationFragment notifyFragment=new notificationFragment();
                    android.support.v4.app.FragmentTransaction tran3=getSupportFragmentManager().beginTransaction();
                    tran3.replace(R.id.frag,notifyFragment);
                    tran3.commit();
                    return true;
                case R.id.allDoctors:
                    setTitle("Doctors");
                    settingFragment settingFragment=new settingFragment();
                    android.support.v4.app.FragmentTransaction tran4=getSupportFragmentManager().beginTransaction();
                    tran4.replace(R.id.frag,settingFragment);
                    tran4.commit();
                    return true;

            }
            return false;
        }

    };

}
