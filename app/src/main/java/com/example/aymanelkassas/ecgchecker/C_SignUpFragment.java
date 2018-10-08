package com.example.aymanelkassas.ecgchecker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.CompletionInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class C_SignUpFragment extends Fragment {

    Button signUp;
    EditText e1, e2, e3, e4;
    CheckBox ch;
    connectionDetector cd;

    DatabaseReference mdatabase, mDoctorDb, mPatientDb;
    FirebaseAuth mAuth;

    ProgressDialog mProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_c__sign_up, container, false);

        mAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference();
        mPatientDb = mdatabase.child("patient");
        mDoctorDb = mdatabase.child("doctor");

        mProgress = new ProgressDialog(getActivity());

        e1 = view.findViewById(R.id.Sfirstname);
        e2 = view.findViewById(R.id.Slastname);
        e3 = view.findViewById(R.id.Smail);
        e4 = view.findViewById(R.id.Spassword);
        ch = view.findViewById(R.id.Sdoctor);

        cd= new connectionDetector(getContext());
        signUp = view.findViewById(R.id.Ssubmit);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cd.isConnected())
                {
                    startRegister(view);
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

    private void startRegister(final View view) {

        final String fname = e1.getText().toString().trim();
        final String lname = e2.getText().toString().trim();
        final String email = e3.getText().toString().trim();
        final String password = e4.getText().toString().trim();
        final boolean Checked=ch.isChecked();
//
        if (!TextUtils.isEmpty(fname) && !TextUtils.isEmpty(lname) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            passingBundleToCompleteInfo(Checked,  fname, lname, email, password);
        }

    }

    private void passingBundleToCompleteInfo(boolean flag,String fname,String lname,String email,String password)
    {
        Intent in=new Intent(getActivity(), CompleteInfo.class);
        Bundle b=new Bundle();
        b.putBoolean("flag",flag);
        b.putString("fname",fname);
        b.putString("lname",lname);
        b.putString("email",email);
        b.putString("password",password);
        in.putExtras(b);
        startActivity(in);
    }

}
