package com.example.aymanelkassas.ecgchecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Splash_Scren extends AppCompatActivity {

    //TODO:ViewPager class used to manage all fragments
    private ViewPager mViewPager;

    //TODO:SectionPagetAdapter class Adapter which data of current fragment in ViewPager using index is contained in adapter
    private SectionsPagerAdapter mSectionsPagerAdapter;

    //TODO:GET START BUTTON
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__scren);

        //TODO:ViewPager that contain all fragments and skew between them
        mViewPager = (ViewPager) findViewById(R.id.container);
        //TODO:SectionsPagerAdapter class that contain each fragment data and return according to index but should pass to constructor FRAGMENTMANAGER CLASS
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        b1= (Button) findViewById(R.id.next);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(Splash_Scren.this,C_LoginChhose.class);
                startActivity(in);
            }
        });

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_splash__scren, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            ImageView imgV=rootView.findViewById(R.id.imgV);
            RelativeLayout frag=rootView.findViewById(R.id.frag);
            if(getArguments().getInt(ARG_SECTION_NUMBER)==1)
            {
                textView.setText(R.string.section_format_1);
                imgV.setImageResource(R.drawable.sample);
                frag.setBackgroundColor(getResources().getColor(R.color.splash1));
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==2)
            {
                textView.setText(R.string.section_format_2);
                imgV.setImageResource(R.drawable.sample);
                frag.setBackgroundColor(getResources().getColor(R.color.splash2));
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==3)
            {
                textView.setText(R.string.section_format_3);
                imgV.setImageResource(R.drawable.sample);
                frag.setBackgroundColor(getResources().getColor(R.color.splash3));
            }

            return rootView;
        }
    }
}
