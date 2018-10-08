package com.example.aymanelkassas.ecgchecker;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class notificationFragment extends Fragment {


    FirebaseAuth mAuth;
    DatabaseReference mDatabase,mDoctor,mPatient,signal;
    ArrayList<Integer> data;

    ListView lstNotify;

    ValueEventListener e0,e1,e2;
    static String[] dfname,dlname,dDate,dAvatar,dComment,dTime;

    int counter=0;
    int schedule_count=0;
    String value="";

    AbsoluteLayout coverNotify;

    public notificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_notification, container, false);

        mDatabase= FirebaseDatabase.getInstance().getReference();
        mDoctor= mDatabase.child("doctor");
        mPatient= mDatabase.child("patient");
        mAuth=FirebaseAuth.getInstance();

        signal=mPatient.child(mAuth.getUid()).child("signal");

        coverNotify=(AbsoluteLayout)v.findViewById(R.id.coverMessage);
        lstNotify=v.findViewById(R.id.lstNotify);

        data = new ArrayList<Integer>();

        signal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int num=(int)dataSnapshot.getChildrenCount();
                for(DataSnapshot d:dataSnapshot.getChildren())
                {
                    if(counter<num-1)
                    {
                        if(d.getValue(Integer.class)==2)
                        {
                            data.add(Integer.parseInt((new StringBuilder(value).reverse().toString()),2));
                            value="";
                        }
                        else
                        {
                            value+=d.getValue(Integer.class).toString();
                        }
                        counter++;
                    }
            }
        }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        e1=mPatient.child(mAuth.getUid()).child("notify").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int count_schedule=(int)dataSnapshot.getChildrenCount();

                if(count_schedule!=0)
                {
                    coverNotify.setVisibility(View.GONE);

                    dfname=new String[(int)dataSnapshot.getChildrenCount()];//from id
                    dlname=new String[(int)dataSnapshot.getChildrenCount()];//from id
                    dDate=new String[(int)dataSnapshot.getChildrenCount()];
                    dAvatar=new String[(int)dataSnapshot.getChildrenCount()];//from id
                    dComment=new String[(int)dataSnapshot.getChildrenCount()];
                    dTime=new String[(int)dataSnapshot.getChildrenCount()];

                    final String[] id=new String[(int)dataSnapshot.getChildrenCount()];

                    for (DataSnapshot c:dataSnapshot.getChildren()) {

                        if(schedule_count<count_schedule) {
                            id[schedule_count] = c.child("id").getValue(String.class);
                            dfname[schedule_count] = c.child("dfname").getValue(String.class);
                            dlname[schedule_count] = c.child("dlname").getValue(String.class);
                            dDate[schedule_count] = c.child("date").getValue(String.class);
                            dAvatar[schedule_count] = c.child("davatar").getValue(String.class);
                            dComment[schedule_count] = c.child("comment").getValue(String.class);
                            dTime[schedule_count] = c.child("time").getValue(String.class);

                            schedule_count++;


                            Thread th = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (true) {
                                        if (dAvatar[schedule_count - 1] != null && dComment[schedule_count - 1] != null) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //Custom adapter
                                                    notificationFragment.myAdapter m = new notificationFragment.myAdapter(getActivity(), android.R.layout.simple_list_item_1, dfname);
                                                    lstNotify.setAdapter(m);
                                                    lstNotify.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                                            Intent in = new Intent(getActivity(), Analysis.class);
                                                            Bundle b = new Bundle();
                                                            b.putIntegerArrayList("ecgSignal", data);
                                                            Bundle bINFO = new Bundle();

                                                            bINFO.putString("fname", dfname[i]);
                                                            bINFO.putString("lname", dlname[i]);
                                                            bINFO.putString("avatar", dAvatar[i]);
                                                            bINFO.putString("time", dTime[i]);

                                                            b.putBundle("info", bINFO);
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

                }
                else
                {
                    Toast.makeText(getActivity(), "No messages for display", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        mPatient.child(mAuth.getUid()).child("notify").removeEventListener(e1);


        return v;
    }

    class myAdapter extends ArrayAdapter<String>
    {

        public myAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull String[] objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inf= (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v=inf.inflate(R.layout.item,parent,false);

            CircleImageView av=v.findViewById(R.id.patientAvatar);
            Picasso.with(getActivity()).load(dAvatar[position]).into(av);

            TextView mt= v.findViewById(R.id.senderName);
            mt.setText(dfname[position]+" "+dlname[position]);

            TextView ms=v.findViewById(R.id.subject);
            ms.setText(dComment[position]);

            TextView mm=v.findViewById(R.id.job);
            mm.setText(dDate[position]);

            TextView mtime=v.findViewById(R.id.time);
            mtime.setText(dTime[position]+" p.m.");

            return v;
        }
    }

}
