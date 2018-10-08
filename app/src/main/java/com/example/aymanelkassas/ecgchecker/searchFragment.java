package com.example.aymanelkassas.ecgchecker;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.location.Location;
import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.aymanelkassas.ecgchecker.R;
import com.example.aymanelkassas.ecgchecker.model.NearByApiResponse;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



/**
 * A simple {@link Fragment} subclass.
 */
public class searchFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

//    public GoogleMap googleMap;
//    MapView mMapView;
//
//    private GoogleApiClient mGoogleApiClient;
//    private Button btnHospitalFind;
//    private LocationRequest mLocationRequest;
//    private Location location;
//    private int PROXIMITY_RADIUS = 8000;

    ListView lsv;

    static String[] hospitalName={"Talkha Central Hospital",
            "Gezeera International Hospital - El Sallab",
            "Intrinsic Specialized Hospital",
            "Gastroenterology Surgical Center - GEC",
            "OCMU Oncology Center - Mansoura University - OCMU",
            "General Authority For Health Insurance - El Daqahleya",
            "Tabarak Hospital for Children",
            "Urology and Nephrology Center",
            "Alnokhba Center"
            ,"مستشفي الطلبه الجامعي"};

    static String[] addressHospital={"Talkha Al Mansoura",
            "Al Doctor Atef Abd Al Menagi, EL MANSOURA, Mansoura",
            "el gomhoureya st.، ميت خميس وكفر الموجي، EL DAKAHLEYA",
            "Gihan St.، المنصورة (قسم 2)، EL DAKAHLEYA",
            "Gehan Al Sadat, Mit Khamis WA Kafr Al Mougi, Mansoura",
            "Street 97، المنصورة (قسم 2)، المنصورة",
            "Maamon Al Shanawi, Mansoura",
            "Gehan Al Sadat, Mit Khamis WA Kafr Al Mougi, Mansoura",
            "حى الجامعة, Mansoura Qism 2, Mansoura",
            "Mansoura Qism 2, Mansoura"};

    static String[] rating={"4.8","2","5.2","9.3","7.5","3.1","7.4","5.8","9.2","8.1"};

    static String imagesUrl="https://maps.gstatic.com/mapfiles/place_api/icons/school-71.png";

    public searchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        lsv=v.findViewById(R.id.lstHospital);

//        //To check permissions above M as below it making issue and gives permission denied on samsung and other phones.
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            checkLocationPermission();
//        }
//        btnHospitalFind = (Button) v.findViewById(R.id.btnHospitalFind);
//
//        //To check google play service available
//        if(!isGooglePlayServicesAvailable()){
//            Toast.makeText(getActivity(),"Google Play Services not available.",Toast.LENGTH_LONG).show();
//            getActivity().finish();
//        }else{
//            // when the map is ready to be used.
////            SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
////            mapFragment.getMapAsync(this);
//            mMapView=v.findViewById(R.id.map);
//            mMapView.getMapAsync(this);
//        }
//
//        btnHospitalFind.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                findPlaces("hospital");
//            }
//        });

        searchFragment.myAdapter m=new searchFragment.myAdapter(getActivity(),android.R.layout.simple_list_item_1,hospitalName);
        lsv.setAdapter(m);

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

            TextView name=(TextView)v.findViewById(R.id.senderName);
            TextView address=(TextView)v.findViewById(R.id.subject);
            TextView rate=(TextView)v.findViewById(R.id.job);

            CircleImageView hospitalImage=(CircleImageView) v.findViewById(R.id.patientAvatar);

            name.setText(hospitalName[position]);
            address.setText(addressHospital[position]);
            rate.setText(rating[position]);
            rate.setTextColor(Color.rgb(254,202,66));
            hospitalImage.setImageResource(R.drawable.hospitalbuild);

            return v;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

//    private boolean isGooglePlayServicesAvailable() {
//        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
//        int result = googleAPI.isGooglePlayServicesAvailable(getActivity());
//        if (result != ConnectionResult.SUCCESS) {
//            if (googleAPI.isUserResolvableError(result)) {
//                googleAPI.getErrorDialog(getActivity(), result, 0).show();
//            }
//            return false;
//        }
//        return true;
//    }
//
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Toast.makeText(getActivity(),"Could not connect google api", Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//
//        if(location!=null){
//            this.location = location;
//            if(!btnHospitalFind.isEnabled()){
//                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
//                btnHospitalFind.setEnabled(true);
//            }
//        }
//    }
//
//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
//        mGoogleApiClient.connect();
//    }
//
//    public void findPlaces(String placeType){
//        Call<NearByApiResponse> call = MyLocationNearby.getApp().getApiService().getNearbyPlaces(placeType, 33.225 + "," + 53.333, PROXIMITY_RADIUS);
//
//        call.enqueue(new Callback<NearByApiResponse>() {
//            @Override
//            public void onResponse(Call<NearByApiResponse> call, Response<NearByApiResponse> response) {
//                try {
//                    // This loop will go through all the results and add marker on each location.
//                    for (int i = 0; i < response.body().getResults().size(); i++) {
//                        Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
//                        Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();
//                        String placeName = response.body().getResults().get(i).getName();
//                        String vicinity = response.body().getResults().get(i).getVicinity();
//
//                        if(i==0)
//                        {
//                            Toast.makeText(getActivity(), ""+placeName, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.d("onResponse", "There is an error");
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<NearByApiResponse> call, Throwable t) {
//                Log.d("onFailure", t.toString());
//                t.printStackTrace();
//                PROXIMITY_RADIUS += 10000;
//            }
//        });
//    }
//
//    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
//
//    public boolean checkLocationPermission() {
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            // Asking user if explanation is needed
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//                //Prompt the user once explanation has been shown
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
//
//
//            } else {
//                // No explanation needed, we can request the permission.
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
//            }
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted. Do the
//                    // contacts-related task you need to do.
//                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//
//                        if (mGoogleApiClient == null) {
//                            buildGoogleApiClient();
//                        }
//                    }
//
//                } else {
//                    Toast.makeText(getActivity(), "Location Permission has been denied, can not search the places you want.", Toast.LENGTH_LONG).show();
//                }
//                return;
//            }
//        }
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        startLocationUpdates();
//    }
//
//    protected void startLocationUpdates() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(1000);
//        mLocationRequest.setFastestInterval(1000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//        }
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        this.googleMap = googleMap;
//        //Initialize Google Play Services
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                buildGoogleApiClient();
//                googleMap.setMyLocationEnabled(true);
//            }
//        } else {
//            buildGoogleApiClient();
//            googleMap.setMyLocationEnabled(true);
//        }
//    }
}
