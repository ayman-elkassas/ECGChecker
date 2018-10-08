package com.example.aymanelkassas.ecgchecker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class C_loginFragment extends Fragment {

    Button Lsubmit;
    EditText e1, e2;
    ProgressDialog mProgress;
    private ValueEventListener dListner;

    //TODO:Class check if there is a connection of internet
    connectionDetector cd;

    //TODO:GET AUTHENTCATION OBJECT
    FirebaseAuth mAuth;
    //TODO:GET DATABASE TABLES
    DatabaseReference mDatabase,mDoctor,mPatient;

    //TODO:onCreateView() calling when fragment inner views appears and already created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_c_login, container, false);

        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mDoctor= mDatabase.child("doctor");
        mPatient= mDatabase.child("patient");

        mProgress = new ProgressDialog(getActivity());

        e1=view.findViewById(R.id.Lmail);
        e2=view.findViewById(R.id.Lpassword);

        cd= new connectionDetector(getContext());

        Lsubmit=view.findViewById(R.id.Lsubmit);
        Lsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if(cd.isConnected())
                {
                    checkLogin(view);
                }
                else
                {
                    Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .setActionTextColor(getResources().getColor(R.color.secondColor))
                            .show();
                }

            }
        });
        return view;
    }

    private void checkLogin(final View view) {

        final String mail=e1.getText().toString();
        final String password=e2.getText().toString();

        if(!TextUtils.isEmpty(mail) && !TextUtils.isEmpty(mail))
        {
            mProgress.setMessage("Login Advance..");
            mProgress.show();
            mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        checkUserExist();
                    }
                    else
                    {
                        Snackbar.make(view, "Error in login in", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });
        }
        else
        {
            Snackbar.make(view, "Fields may be empty", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

    private void checkUserExist() {

        //TODO:TO GET ALL INFORMATION ABOUT THIS USER
        final String user_id=mAuth.getCurrentUser().getUid();
        dListner=mDoctor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id))
                {
                    mProgress.dismiss();
                    Intent intent=new Intent(getActivity(),DoctorProfile.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    mProgress.dismiss();
                    Intent intent=new Intent(getActivity(),E_patientProfile.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                mDoctor.removeEventListener(dListner);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
