package com.example.aymanelkassas.ecgchecker;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class homeFragment extends Fragment {

    connectionDetector cd;
    CircleButton connect;

    FirebaseAuth mAuth;
    DatabaseReference mRef, mPatients, mDoctors, mDevice;

    static ArrayList<DoctorsInfo> ArrDoctors = new ArrayList<DoctorsInfo>();
    static int count =0;

    private ValueEventListener mListener;
    private ValueEventListener dListner;
    private ValueEventListener e1,e2;


    public homeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        mPatients = mRef.child("patient");
        mDoctors = mRef.child("doctor");
        mDevice = mRef.child("device");

        cd = new connectionDetector(getActivity());

        connect = (CircleButton) view.findViewById(R.id.connect);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (cd.isConnected()) {

                    mPatients.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("signal"))
                            {
                                searchDoctors();
                            }
                            else
                            {
                                Snackbar.make(view, "There is no signal for upload", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {
                    Snackbar.make(view, "No internet connection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        return view;
    }

    public void searchDoctors()
    {
        e2=mPatients.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mAuth.getUid())) {

                    mListener=mPatients.child(mAuth.getUid()).child("my_doctors").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final int num=(int)dataSnapshot.getChildrenCount();

                            for (DataSnapshot child : dataSnapshot.getChildren()) {

                                final String id = child.getValue(String.class);

                                dListner = mDoctors.child(id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        DoctorsInfo di = new DoctorsInfo();
                                        count++;
                                        di.setId((dataSnapshot.child("id").getValue(String.class)));
                                        di.setFname(dataSnapshot.child("fname").getValue(String.class));
                                        di.setLname(dataSnapshot.child("lname").getValue(String.class));
                                        di.setMail(dataSnapshot.child("email").getValue(String.class));
                                        di.setAvatar(dataSnapshot.child("avatar").getValue(String.class));
                                        di.setBio(dataSnapshot.child("bio").getValue(String.class));
                                        di.setJob(dataSnapshot.child("job").getValue(String.class));
                                        di.setLocation(dataSnapshot.child("location").getValue(String.class));
                                        di.setPhone(dataSnapshot.child("phone").getValue(String.class));
                                        di.setWorking_hours(dataSnapshot.child("working_hours").getValue(String.class));
                                        ArrDoctors.add(di);

                                        mDoctors.child(id).removeEventListener(dListner);

                                        if (count == num) {
                                            count = 0;
                                            Intent in = new Intent(getActivity(), ListOfDoctors.class);
                                            mDoctors.child(id).removeEventListener(dListner);
                                            startActivity(in);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            mPatients.child(mAuth.getUid()).child("my_doctors").removeEventListener(mListener);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

                mPatients.removeEventListener(e2);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
