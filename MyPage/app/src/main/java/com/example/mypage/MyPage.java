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
        Button btn_add = (Button)findViewById(R.id.btn_add);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                String title = String.valueOf(capsule.getContent().get(0).getContent_id());
                                String url = capsule.getContent().get(0).getUrl() != null ?
                                        capsule.getContent().get(0).getUrl() : drawablePath;
                                String created_date = capsule.getDate_created();
                                String opened_date = capsule.getDate_created();
                                String location = "대한민";
                                // UTC Time control국


                                try {
                                    // UTC -> LOCAL TIME
                                    SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String utcDate = capsule.getDate_created();
                                    Date date = getLocalTime(utcDate);

                                    String localDate = created_date = fm.format(date);
                                    created_date = localDate.substring(0, 4) + "년 " + localDate.substring(5, 7) +
                                            "월 " + localDate.substring(8, 10) + "일 " + localDate.substring(11, 13) + "시";

                                    utcDate = capsule.getDate_opened();
                                    date = getLocalTime(utcDate);
                                    localDate = fm.format(date);
                                    opened_date = localDate.substring(0, 4) + "년 " + localDate.substring(5, 7) +
                                            "월 " + localDate.substring(8, 10) + "일 " + localDate.substring(11, 13) + "시";

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

                                CapsuleLogData capsuleLogData = new CapsuleLogData(url,
                                        title, "#절친 #평생친구", created_date,
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
