package com.example.aymanelkassas.ecgchecker;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import java.io.ObjectStreamException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Analysis extends AppCompatActivity {

    NumberProgressBar  pb;

    GraphView graph;
    final LineGraphSeries<DataPoint> series= new LineGraphSeries<>();
    int point=0;

    TextView head,who_send,time;
    CircleImageView avatar_Analysis;

    static int count=0;
    static int times=0;

    TextView resState,resDCC,resHR,resPE,resLVH,resBBB,resArithmia,resSPVH;
    TextView completeAnalysis;

    static Bundle b;

    static String header,sender,id,avatar,t="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Analysis");
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        graph = (GraphView) findViewById(R.id.analysisGraph);

        resState=findViewById(R.id.resState);
        resDCC=findViewById(R.id.resDCC);
        resHR=findViewById(R.id.resHR);
        resPE=findViewById(R.id.resPE);
        resLVH=findViewById(R.id.resLVH);
        resBBB=findViewById(R.id.resBBB);
        resArithmia=findViewById(R.id.resArithmia);
        resSPVH=findViewById(R.id.resSPVH);

        completeAnalysis=findViewById(R.id.completeAnalysis);

        ArrayList<Integer> ecgSignal= getIntent().getIntegerArrayListExtra("ecgSignal");
        b=getIntent().getBundleExtra("info");

        if (b != null) {

            if (b.getString("fname") == null && b.getString("lname") == null) {
                sender = b.getString("full_name");
            } else {
                sender = b.getString("fname") + " " + b.getString("lname");
            }

            avatar = b.getString("avatar");
            t = b.getString("time");

        }

        head = (TextView) findViewById(R.id.senderAnalysis);
        who_send = (TextView) findViewById(R.id.senderAnalysis);
        avatar_Analysis = (CircleImageView) findViewById(R.id.avatarAnalysis);
        time = (TextView) findViewById(R.id.time);

//            head.setText(header);
        who_send.setText(sender);
        Picasso.with(getBaseContext()).load(avatar).into(avatar_Analysis);
        time.setText(t);




        double sum=0;
        int c=0;
        for(int i=0;i<ecgSignal.size();i++)
        {
            point = ecgSignal.get(i);
            if(point<1000) {
                sum+=point;
                c++;
            }
        }

        double average=sum/c;

        for(int i=50;i<ecgSignal.size();i++)
        {
            point = ecgSignal.get(i);

            if(point<1000) {
                if((point<(average+50) && point >(average-50)) || point==(int)average)
                {
                    point=(int)average;
                }
                DataPoint p = new DataPoint(i, point);
                series.appendData(p, true, ecgSignal.size());
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

        pb=findViewById(R.id.number_progress_bar);
        new AsyncTaskProcess().execute(ecgSignal);
        Toast.makeText(this, "ECG Signal Processing...", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed() {

        //TODO:Assurance message dialogue
        AlertDialog.Builder alert=new AlertDialog.Builder(Analysis.this);
        alert.setMessage("Cancel Processing !").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog al=alert.create();
        alert.setTitle("Are you sure ?");
        alert.show();
    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }

    class AsyncTaskProcess extends AsyncTask<ArrayList<Integer>,Integer,ArrayList<Object>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Object> doInBackground(ArrayList<Integer>... signal)
        {
            ArrayList<Integer>ecg=signal[0];

            EcgProcessing ecgprocessing=new EcgProcessing(ecg);

            ArrayList<Object> result=new ArrayList<>();
            try {

                times=1;
                //TODO:Regular or not
                String state=ecgprocessing.regularOrIrregular();
                result.add(state);

                for (int i=count;i<13*times;i++)
                {
                    Thread.sleep(100);
                    publishProgress(i);
                    count++;
                }

                times++;
                //TODO:Duration of cardia cycle
                double DOCC=ecgprocessing.Duration_Of_Cardio_Cycle();
                result.add(DOCC);

                for (int i=count;i<13*times;i++)
                {
                    Thread.sleep(100);
                    publishProgress(i);
                    count++;
                }

                times++;
                //TODO:Heart Rate
                double HR=ecgprocessing.HR();
                result.add(HR);

                for (int i=count;i<13*times;i++)
                {
                    Thread.sleep(100);
                    publishProgress(i);
                    count++;
                }

                times++;
                //TODO:1st degree Heart Block or Pre-Excitation Syndrome
                ArrayList<String> PR_Interval=ecgprocessing.PR_Normality();
                result.add(PR_Interval);

                for (int i=count;i<13*times;i++)
                {
                    Thread.sleep(100);
                    publishProgress(i);
                    count++;
                }

                times++;
                //TODO:LT.RT Ventricular Hypertrophy
                ArrayList<String> LVH=ecgprocessing.LVH();
                result.add(LVH);

                for (int i=count;i<13*times;i++)
                {
                    Thread.sleep(100);
                    publishProgress(i);
                    count++;
                }

                times++;
                //TODO:Bundle Branch Block
                String BBB=ecgprocessing.BundleBranchBlock();
                result.add(BBB);

                for (int i=count;i<13*times;i++)
                {
                    Thread.sleep(100);
                    publishProgress(i);
                    count++;
                }

                times++;
                //TODO:Tachy Arithmia Or Brady
                String Arithmia=ecgprocessing.TachyORBrady_Arithmia();
                result.add(Arithmia);

                for (int i=count;i<13*times;i++)
                {
                    Thread.sleep(100);
                    publishProgress(i);
                    count++;
                }

                times++;
                //TODO:Strain Pattern of ventricular Hypertrophy
                String SPVH=ecgprocessing.StrainPatternOfVH();
                result.add(SPVH);

                for (int i=count;i<13*times;i++)
                {
                    Thread.sleep(100);
                    publishProgress(i);
                    count++;
                }

            }catch (Exception ex)
            {
                ex.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            pb.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Object> results) {
            super.onPostExecute(results);
            ArrayList<Object> result=results;

            resState.setText(result.get(0).toString());
            resDCC.setText(result.get(1).toString()+" "+"seconds");
            resHR.setText(result.get(2).toString()+" "+"b/m");
            resPE.setText(result.get(3).toString());
            resLVH.setText(result.get(4).toString());
            resBBB.setText(result.get(5).toString());
            resArithmia.setText(result.get(6).toString());
            resSPVH.setText(result.get(7).toString());

            completeAnalysis.setText("Complete...");
            completeAnalysis.setTextColor(getResources().getColor(R.color.green));

        }
    }


}

