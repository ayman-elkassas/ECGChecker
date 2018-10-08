package com.example.aymanelkassas.ecgchecker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class CompleteInfo extends AppCompatActivity {

    DatabaseReference mdatabase, mDoctorDb, mPatientDb;
    private StorageReference mStorage;
    FirebaseAuth mAuth;
    Uri downloadUri;

    ProgressDialog mProgress;

    connectionDetector cd;

    EditText job,phone,location,bio,workinghours;
    CircleImageView avatar;
    private static final int galaryCode=1;
    private Uri mImageUri=null;

    Button complete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_info);

        mdatabase = FirebaseDatabase.getInstance().getReference();
        mPatientDb = mdatabase.child("patient");
        mDoctorDb = mdatabase.child("doctor");

        mAuth = FirebaseAuth.getInstance();
        mStorage= FirebaseStorage.getInstance().getReference();


        mProgress = new ProgressDialog(this);

        job=(EditText) findViewById(R.id.job);
        phone=(EditText)findViewById(R.id.phone);
        location=(EditText)findViewById(R.id.location);
        bio=(EditText)findViewById(R.id.bio);
        workinghours=(EditText)findViewById(R.id.workingTime);

        complete=(Button)findViewById(R.id.complete);

        cd= new connectionDetector(this);

        final Bundle b= getIntent().getExtras();

        avatar=(CircleImageView)findViewById(R.id.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galaryIntent=new Intent(Intent.ACTION_PICK);
                galaryIntent.setType("image/*");
                startActivityForResult(galaryIntent,galaryCode);
            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InsertCompleteInfo(b,view);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==galaryCode && resultCode==RESULT_OK)
        {
            mImageUri=data.getData();
            avatar.setImageURI(mImageUri);
            Toast.makeText(CompleteInfo.this, ""+mImageUri.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void InsertCompleteInfo(Bundle b, final View view) {

        final boolean flag=b.getBoolean("flag");

        final String fname=b.getString("fname");
        final String lname=b.getString("lname");
        final String email=b.getString("email").trim();
        final String password=b.getString("password").trim();

        if(cd.isConnected()) {

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        mProgress.setMessage("Waiting for build your own profile......");
                        mProgress.show();

                        if(mImageUri !=null)
                        {
                            StorageReference filePath=mStorage.child("Avatar").child(mImageUri.getLastPathSegment());

                            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    downloadUri=taskSnapshot.getDownloadUrl();

                                    String[] fields = {"fname", "lname", "email", "password", "job",
                                            "phone", "location", "bio", "working_hours","avatar"};

                                    String[] values = {fname, lname, email, password, job.getText().toString()
                                            , phone.getText().toString(), location.getText().toString(),
                                            bio.getText().toString(), workinghours.getText().toString(),
                                            downloadUri.toString()};

                                    if (flag == true) {
                                        if(InsertedInId(mDoctorDb, fields, values))
                                        {
                                            mProgress.dismiss();
                                            Intent intent = new Intent(CompleteInfo.this, DoctorProfile.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    } else if (flag == false) {

                                        if(InsertedInId(mPatientDb, fields, values))
                                        {
                                            mProgress.dismiss();
                                            Intent intent = new Intent(CompleteInfo.this, E_patientProfile.class);
                                            startActivity(intent);
                                        }
                                    }

                                }

                            });

                            filePath.putFile(mImageUri).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("error",e.getMessage());
                                }
                            });
                        }
                    }
                    else
                    {
                        Snackbar.make(view, "This account already exist !!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null)
                                .setActionTextColor(getResources().getColor(R.color.secondColor))
                                .show();
                    }

                }
            });
        }
        else {
            Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .setActionTextColor(getResources().getColor(R.color.secondColor))
                    .show();
        }
    }

    private boolean InsertedInId(DatabaseReference db,String [] fields,String [] values) {

        db.child(mAuth.getUid()).child("id").setValue(mAuth.getUid());
        for (int i=0;i<values.length;i++)
        {
            db.child(mAuth.getUid()).child(fields[i]).setValue(values[i]);
        }
        return true;
    }


}
