package com.example.mypage;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Locale;
import java.util.TimeZone;

public class MyPage extends AppCompatActivity {

    private ArrayList<CapsuleLogData> arrayList;
    private CapsuleLogAdapter capsuleLogAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RetrofitInterface retrofitInterface;
    private static final String TAG = "MyPage";
    private List<Capsule> capsuleList;
    private String drawablePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // 데이터 추가

        retrofitInterface.requestSearchUserCapsule("id0").enqueue(new Callback <List<Capsule>>() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<Capsule>> call, Response<List<Capsule>> response) {
                capsuleList = response.body();
                Log.d(TAG,capsuleList.toString());
                if (capsuleList != null) {
                    for (Capsule capsule : capsuleList) {
                        int state_temp = capsule.getStatus_temp();
                        int capsule_id = capsule.getCapsule_id();
                        String title = capsule.getTitle() != null ? capsule.getTitle() : "";
                        String url = capsule.getContent().get(0).getUrl() != null ?
                                capsule.getContent().get(0).getUrl() : drawablePath;
                        String created_date = capsule.getDate_created();
                        String opened_date = capsule.getDate_created();
                        String location = "Default";
                        String d_day = "0";
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
                            List<Address> list = geocoder.getFromLocation(capsule.getLat(), capsule.getLng(), 3);
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

                        CapsuleLogData capsuleLogData = new CapsuleLogData(capsule_id, d_day,
                                url, title, "#절친 #평생친구", created_date,
                                opened_date, location, state_temp);
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
