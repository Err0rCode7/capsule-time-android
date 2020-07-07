package com.example.capsuletime.mainpages.mypage;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capsuletime.Capsule;
import com.example.capsuletime.CapsuleLogData;
import com.example.capsuletime.Content;
import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.User;
import com.example.capsuletime.mainpages.ar.UnityPlayerActivity;
import com.example.capsuletime.mainpages.capsulemap.capsulemap;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class mypage extends AppCompatActivity {

    private String user_id;
    private ArrayList<CapsuleLogData> arrayList;
    private CapsuleLogAdapter capsuleLogAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RetrofitInterface retrofitInterface;
    private static final String TAG = "MyPage";
    private List<Capsule> capsuleList;
    private String drawablePath;
    private User user;
    private int fromArFlag;

    @Override
    protected void onCreate (Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_mypage);
        Log.d(TAG,"FFFF is mypage");
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        user = intent.getParcelableExtra("user");
        fromArFlag = intent.getIntExtra("fromAr",0);
        Log.d("Hello","mypage");


        ImageView iv_user = (ImageView) this.findViewById(R.id.user_image);
        TextView tv_id = (TextView) this.findViewById(R.id.tv_userId);

        if(user_id != null)
            tv_id.setText(user_id);

        RetrofitClient retrofitClient = new RetrofitClient();
        retrofitInterface = retrofitClient.retrofitInterface;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        drawablePath = "res:///" + R.drawable.capsule_temp;

        recyclerView = (RecyclerView)findViewById(R.id.rv);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        capsuleLogAdapter = new CapsuleLogAdapter(arrayList,this);
        recyclerView.setAdapter(capsuleLogAdapter);

        iv_user.setImageResource(R.drawable.user);

        if(user == null){
            retrofitInterface.requestUserData(user_id).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    user = response.body();
                    if (user != null) {
                        if(user.getImage_url() == null || Objects.equals(user.getImage_url(), "")){
                            Log.d(TAG,"url null");
                            iv_user.setImageResource(R.drawable.user);
                        } else {
                            Log.d(TAG,"url not null");
                            Glide
                                .with(getApplicationContext())
                                .load(user.getImage_url())
                                .into(iv_user);
                        }

                        tv_id.setText(user.getUser_id());
                    } else {
                        iv_user.setImageResource(R.drawable.user);
                        tv_id.setText("서버통신오류");
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.d(TAG, "server-get-user fail");
                }
            });
        } else {
            if(user.getImage_url() == null || Objects.equals(user.getImage_url(), "")){
                Log.d(TAG,"user not null url null");
                iv_user.setImageResource(R.drawable.user);
            } else {
                Log.d(TAG,"user not null url not null");
                Glide
                    .with(getApplicationContext())
                    .load(user.getImage_url())
                    .into(iv_user);
            }
            tv_id.setText(user.getUser_id());
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        //Set mypage Selected
        bottomNavigationView.setSelectedItemId(R.id.mypage);
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_UNLABELED);

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (user != null) {

                    switch (menuItem.getItemId()) {
                        case R.id.mypage:
                            return true;

                        case R.id.capsulemap: {
                            if (Build.VERSION.SDK_INT >= 23 &&
                                    ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(mypage.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        0);
                                break;
                            } else if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                                Intent intent = new Intent(getApplicationContext(), capsulemap.class);
                                intent.putExtra("user", user);
                                startActivity(intent);
                                overridePendingTransition(0, 0);

                                return true;

                            }
                        }
                        case R.id.capsulear: {
                            //bottomNavigationView.getMenu().getItem(0).setChecked(true);
                            Intent intent = new Intent(getApplicationContext(), UnityPlayerActivity.class);
                            intent.putExtra("userId", user);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            //return true;
                        }
                    }
                }
                bottomNavigationView.setSelectedItemId(R.id.mypage);
                return false;
            }
        });

        String inStr = (user_id != null) ? user_id : user.getUser_id();
        if (inStr != null)
            retrofitInterface.requestSearchUserCapsule(inStr).enqueue(new Callback<List<Capsule>>() {

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call<List<Capsule>> call, Response<List<Capsule>> response) {
                    capsuleList = response.body();
                    if (capsuleList != null) {
                        for (Capsule capsule : capsuleList) {
                            int state_temp = capsule.getStatus_temp();
                            int capsule_id = capsule.getCapsule_id();
                            String title = capsule.getTitle() != null ? capsule.getTitle() : "";
                            String url = capsule.getContent().get(0).getUrl() != null ?
                                    capsule.getContent().get(0).getUrl() : Integer.toString(R.drawable.capsule_marker_angry);
                            List<Content> contentList = capsule.getContent();
                            String created_date = capsule.getDate_created();
                            String opened_date = capsule.getDate_created();
                            String location = "Default";
                            String d_day = "0";
                            String text = capsule.getText();
                            // UTC Time control


                            try {
                                // UTC -> LOCAL TIME
                                SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String created_utcDate = capsule.getDate_created();
                                Date crt_date = getLocalTime(created_utcDate);

                                String localDate = fm.format(crt_date);
                                created_date = localDate.substring(0, 4) + "년 " + localDate.substring(5, 7) +
                                        "월 " + localDate.substring(8, 10) + "일 " + localDate.substring(11, 13) + "시";

                                String opened_utcDate = capsule.getDate_opened();
                                Date opn_date = getLocalTime(opened_utcDate);
                                localDate = fm.format(opn_date);
                                opened_date = localDate.substring(0, 4) + "년 " + localDate.substring(5, 7) +
                                        "월 " + localDate.substring(8, 10) + "일 " + localDate.substring(11, 13) + "시";

                                Date date = new Date();

                                long diff = date.getTime() - opn_date.getTime();
                                d_day = "D - " + Long.toString( diff/ (1000 * 60 * 60 * 24) );

                                //Log.d(TAG,created_date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                Log.d(TAG, "asdasd");
                            }

                            try {
                                DecimalFormat df = new DecimalFormat();
                                df.setMaximumFractionDigits(3);
                                double lat = Double.parseDouble(df.format(capsule.getLat()));
                                double lng = Double.parseDouble(df.format(capsule.getLng()));

                                List<Address> list = geocoder.getFromLocation(lat, lng, 3);
                                if (list != null) {
                                    if (list.size() == 0){
                                        // no
                                    } else {
                                        location = list.get(0).getAddressLine(0);
                                        Log.d(TAG, "location good");
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Log.d(TAG,url+" "+title+" "+created_date+" "+opened_date+" "+location+" "+state_temp);

                            CapsuleLogData capsuleLogData = new CapsuleLogData(inStr, capsule_id, d_day,
                                    url, title, text, "#절친 #평생친구", created_date,
                                    opened_date, location, state_temp, contentList);
                            arrayList.add(capsuleLogData);
                            capsuleLogAdapter.notifyDataSetChanged(); // redirect
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Capsule>> call, Throwable t) {
                    Log.d(TAG, "fail");
                }
            });
    }
    private Date getLocalTime(String time) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = sdf.parse(time);
        long utctime = date.getTime();
        TimeZone tz = TimeZone.getDefault();
        int offset = tz.getOffset(utctime);
        long localtime = utctime + offset;
        Date local = new Date();
        local.setTime(localtime);

        return local;
    }
}
