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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListOfDoctors extends AppCompatActivity {

    static ArrayList<DoctorsInfo> ArrDoctors = new ArrayList<DoctorsInfo>();

    ListView lst;

    String[] doctorsName=new String[homeFragment.ArrDoctors.size()];
    String[] jobs=new String[homeFragment.ArrDoctors.size()];
    String[] avatars=new String[homeFragment.ArrDoctors.size()];

    String[] id=new String[homeFragment.ArrDoctors.size()];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_doctors);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Social");
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        lst= (ListView) findViewById(R.id.lst);

        ArrDoctors=homeFragment.ArrDoctors;

        for(int i=0;i<ArrDoctors.size();i++)
        {
            doctorsName[i]=ArrDoctors.get(i).getFname()+" "+ArrDoctors.get(i).getLname();
            jobs[i]=ArrDoctors.get(i).getJob();
            avatars[i]=ArrDoctors.get(i).getAvatar();
            id[i]=ArrDoctors.get(i).getId();
        }


        myAdapter m=new myAdapter(this,android.R.layout.simple_list_item_1,doctorsName);
        lst.setAdapter(m);

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent in=new Intent(ListOfDoctors.this,F_SendDataToDoctor.class);
                Bundle b=new Bundle();
                b.putString("did",id[i]);
                b.putString("dname",doctorsName[i]);
                in.putExtras(b);
                startActivity(in);
            }
        });

        ArrDoctors.clear();

//        Toast.makeText(this, ""+ArrDoctors.get(1).getFname(), Toast.LENGTH_SHORT).show();

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
            View v=inf.inflate(R.layout.doctors_item,parent,false);

            ImageView img=v.findViewById(R.id.itemDoctorAvatar);
            Picasso.with(getBaseContext()).load(avatars[position]).into(img);

            TextView tn= v.findViewById(R.id.doctorName);
            tn.setText(doctorsName[position]);

            TextView tj= v.findViewById(R.id.doctorJob);
            tj.setText(jobs[position]);

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
