package com.example.aymanelkassas.ecgchecker;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class settingFragment extends Fragment {


    EditText searchToken;
    CircleButton goSearch;
    RecyclerView listDoctors;

    DatabaseReference mDatabase;

    public settingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_setting,container,false);

        mDatabase= FirebaseDatabase.getInstance().getReference("doctor");

        searchToken =(EditText)view.findViewById(R.id.searchToken);
        goSearch=(CircleButton)view.findViewById(R.id.goSearch);
        listDoctors=(RecyclerView)view.findViewById(R.id.listDoctors);

        goSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fireBaseUserSearch(searchToken.getText().toString());
            }
        });

        listDoctors.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity());
        listDoctors.setLayoutManager(layoutManager);
        return view;
    }

    private void fireBaseUserSearch(String query) {
        if(!query.equals(""))
        {
            if (!Character.isUpperCase(query.charAt(0)))
            {
                query = query.substring(0, 1).toUpperCase() + query.substring(1);
            }
        }

        Query firebaseSearchQuery=mDatabase.orderByChild("fname").startAt(query).endAt(query+"\uf8ff");

        FirebaseRecyclerAdapter<DoctorsInfo,UserViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<DoctorsInfo, UserViewHolder>(

                DoctorsInfo.class,
                R.layout.doctors_item,
                UserViewHolder.class,
                firebaseSearchQuery

        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, DoctorsInfo model, int position) {
//                viewHolder.setDetails(model.getAvatar(),model.getFname()+" "+model.getLname(),model.getJob());
                viewHolder.setDetails(model.getAvatar(),model.getFname()+" "+model.getLname(),model.getJob(),getActivity());
            }
        };

        listDoctors.setAdapter(firebaseRecyclerAdapter);

    }

    public static class UserViewHolder extends RecyclerView.ViewHolder
    {

        View mView;

        public UserViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setDetails(String path, String doctorname, String djob, Context cx)
        {
            CircleImageView avatar=(CircleImageView)mView.findViewById(R.id.itemDoctorAvatar);
            TextView name=(TextView)mView.findViewById(R.id.doctorName);
            TextView job=(TextView)mView.findViewById(R.id.doctorJob);

            Picasso.with(cx).load(path).into(avatar);
            name.setText(doctorname);
            job.setText(djob);

        }

    }

}
