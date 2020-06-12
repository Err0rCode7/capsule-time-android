package com.example.capsuletime.mainpages.capsulemap;



import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.User;
import com.example.capsuletime.cap;
import com.example.capsuletime.mainpages.ar.ar_main;
import com.example.capsuletime.mainpages.mypage.mypage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class capsulemap extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "capsulemap";
    private String user_id;
    private User user;
    private RetrofitInterface retrofitInterface;
    private List<cap> capsuleList;
    private String drawablePath;
    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private double cur_lng;
    private double cur_lat;
    private Marker curMarker;
    private boolean firstFlag = false;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate (Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_capsulemap);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");

        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

        RetrofitClient retrofitClient = new RetrofitClient();
        retrofitInterface = retrofitClient.retrofitInterface;

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(user == null){
            retrofitInterface.requestUserData(user_id).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    user = response.body();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.d(TAG, "server-get-user fail");
                }
            });
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        //Set mypage Selected
        bottomNavigationView.setSelectedItemId(R.id.capsulemap);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);



        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.mypage: {
                        Intent intent = new Intent(getApplicationContext(), mypage.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                    }
                    case R.id.capsulemap:
                        return true;

                    case R.id.capsulear: {
                        Intent intent = new Intent(getApplicationContext(), ar_main.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                    }
                }
                return false;

            }
        });

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
            return;
        }

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, LocationListener);

        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();

        } else {

            latitude = 37.52487;
            longitude = 126.92723;
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getApplicationContext(), "권한이 허용됨", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한이 거됨", Toast.LENGTH_SHORT).show();
        }
    };

    final android.location.LocationListener LocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            longitude = location.getLongitude();
            latitude = location.getLatitude();
            //double altitude = location.getAltitude();
            Log.d(TAG, "좌표 ->" + longitude + latitude);


            userCurrentMark();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    public void userCurrentMark() {

        if (curMarker != null){
            Log.d("TAG","curMarker remove");
            curMarker.remove();
        }

        LatLng SEOUL = new LatLng(latitude, longitude);
        cur_lat = latitude;
        cur_lng = longitude;

        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(SEOUL);
        markerOptions2.title("user");
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.cur_user);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 80, false);
        markerOptions2.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        if (firstFlag == false){
            mMap.animateCamera(CameraUpdateFactory.newLatLng(SEOUL));
            firstFlag = true;
        }

        curMarker = mMap.addMarker(markerOptions2);
        Log.d(TAG,curMarker.toString());
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        RetrofitClient retrofitClient = new RetrofitClient();
        retrofitInterface = retrofitClient.retrofitInterface;

        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        drawablePath = "res:///" + R.drawable.capsule_temp;

        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        retrofitInterface.requestAllCapsules().enqueue(new Callback<List<cap>>() {
            @Override
            public void onResponse(Call<List<cap>> call, Response<List<cap>> response) {

                capsuleList = response.body();
                Log.d(TAG, response.body().toString() + "curMarkerAdd");

                for (int i = 0; i < capsuleList.size(); i++) {
                    Log.d(TAG, capsuleList.get(i).toString());
                }

                setUpCapsulesOnMap();
            }

            @Override
            public void onFailure(Call<List<cap>> call, Throwable t) {

            }
        });

    }
    private void setUpCapsulesOnMap() {
        //mMap.clear();

        List<cap> capsules = capsuleList;
        Log.i(TAG, "캡슐 정보 " + capsules);
        for (int i = 0; i < capsules.size(); i++) {
            Log.i(TAG, "start() ");

            LatLng latLng = new LatLng(capsules.get(i).getLat(), capsules.get(i).getLng());

            Log.d(TAG, latLng.toString());

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(capsules.get(i).getUser_id());
            //markerOptions.snippet(capsules.get(i).getCapsule_id());

            Log.d(TAG, markerOptions.getTitle());
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.capsule_marker);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 80, false);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            Marker marker = mMap.addMarker(markerOptions);

        }
        mMap.setOnMarkerClickListener(this);
        //OnclickMarker();
        userCurrentMark();

    }

    /*
    private void OnclickMarker () {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 마커 클릭시 호출되는 콜백 메서드

                Intent intent = new Intent(getApplicationContext(), PopUpActivity.class);
                intent.putExtra("user_id",marker.getTitle());
                startActivityForResult(intent, 1);

                //Toast.makeText(getApplicationContext(),
                //" 클릭했음" + capsuleList.get(1)
                //, Toast.LENGTH_SHORT).show();

                return false;
            }
        });
    }
    */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!marker.getId().equals(curMarker.getId())) {
            Intent intent = new Intent(getApplicationContext(), PopUpActivity.class);
            intent.putExtra("user_id",marker.getTitle());
            startActivityForResult(intent, 1);
            return false;
        } else {

            return false;
        }

    }
}
