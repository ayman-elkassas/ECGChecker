package com.example.aymanelkassas.ecgchecker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class report extends AppCompatActivity {

    DatabaseReference mRef,signal;
    ArrayList<Integer> data;
    GraphView graph;
    final LineGraphSeries<DataPoint> series= new LineGraphSeries<>();

    TextView head,who_send,time;
    CircleImageView avatar_patient;

    int point=0;
    int itrate=0;
    String value="";
    int counter=0;

    double duration=.04;

    static double average=0;

    Button progress;

    static Bundle bINFO;

    static String header,sender,id,avatar,t="";

    FirebaseAuth mAuth;

    LinearLayout reportContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Report");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mAuth=FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

        graph = (GraphView) findViewById(R.id.graph);

        progress=(Button)findViewById(R.id.process);

        reportContainer=findViewById(R.id.reportContainer);


        data = new ArrayList<Integer>();

        bINFO = getIntent().getExtras();

        if (bINFO != null) {
            if (bINFO.getString("fname") == null && bINFO.getString("lname") == null) {
                sender = bINFO.getString("full_name");
            } else {
                sender = bINFO.getString("fname") + " " + bINFO.getString("lname");
            }

            id=bINFO.getString("id");

            header = bINFO.getString("message_content");
            avatar = bINFO.getString("avatar");
            t = bINFO.getString("time");
        }

        signal=mRef.child("patient").child(id).child("signal");

        if(!id.equals("G6B7stSxsXbKOMGYBMGrnbkCNNy1"))
        {
            reportContainer.setVisibility(View.GONE);
            Toast.makeText(this, "No ECG Signal For processing...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            head = (TextView) findViewById(R.id.header);
            who_send = (TextView) findViewById(R.id.sender);
            avatar_patient = (CircleImageView) findViewById(R.id.itemPatientAvatar);
            time = (TextView) findViewById(R.id.time);

            head.setText(header);
            who_send.setText(sender);
            Picasso.with(getBaseContext()).load(avatar).into(avatar_patient);
            time.setText(t);

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
                        else
                        {
                            double sum=0;
                            int c=0;
                            for(int i=0;i<data.size();i++)
                            {
                                point = data.get(i);
                                if(point<1000) {
                                    sum+=point;
                                    c++;
                                }
                            }

                            average=sum/c;
                            Toast.makeText(report.this, "avg: "+average, Toast.LENGTH_SHORT).show();

                            for(int i=50;i<data.size();i++)
                            {
                                point = data.get(i);

                                if(point<1000) {
                                    if((point<(average+50) && point >(average-50)) || point==(int)average)
                                    {
                                        point=(int)average;
                                    }
                                    DataPoint p = new DataPoint(i, point);
                                    series.appendData(p, true, num);
                                }
                            }

                            graph.addSeries(series);
                            // styling series
                            series.setColor(R.color.green);
                            series.setAnimated(true);
                            series.setDrawAsPath(true);
                            series.setDrawBackground(true);
                            series.setDrawDataPoints(true);
                            series.setDataPointsRadius(4);
                            series.setThickness(2);
//                        Toast.makeText(report.this, "x: "+series.getHighestValueX()+", y: "+series.getHighestValueY(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            progress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in =new Intent(report.this,Analysis.class);
                    Bundle b=new Bundle();
                    b.putIntegerArrayList("ecgSignal",data);
                    b.putBundle("info",bINFO);
                    in.putExtras(b);
                    startActivity(in);
                }
            });

        }

    }

    //for options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_doctor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.quickly:
//                Toast.makeText(this, "Determine time quickly", Toast.LENGTH_SHORT).show();
                Intent in=new Intent(this,Appointment.class);
                Bundle b=new Bundle();
                b.putString("id",id);
                b.putString("full_name",sender);
//              Toast.makeText(this, "Null Bundle"+sender, Toast.LENGTH_SHORT).show();
                in.putExtras(bINFO);
                startActivity(in);

                break;
            case R.id.rosheta:

//                Toast.makeText(this, "Schedule", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(this,Schedule.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
                break;
            case R.id.logoutReport:
                mAuth.signOut();
                break;
            case R.id.setting:
                Intent intent1 =new Intent(getBaseContext(),configurations.class);
                startActivity(intent1);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in =new Intent(this,DoctorProfile.class);
        startActivity(in);
    }
}
