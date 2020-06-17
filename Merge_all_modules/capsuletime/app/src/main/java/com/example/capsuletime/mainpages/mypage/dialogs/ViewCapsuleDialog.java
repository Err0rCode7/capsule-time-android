package com.example.capsuletime.mainpages.mypage.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.example.capsuletime.CapsuleLogData;
import com.example.capsuletime.mainpages.mypage.CapsuleLogAdapter;

import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.Success;
import com.example.capsuletime.mainpages.mypage.ViewPagerAdapter;
import com.example.capsuletime.mainpages.mypage.ViewPagerAdapterOnlyView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewCapsuleDialog {
    private Context context;
    private CapsuleLogData capsuleLogData;
    private RetrofitInterface retrofitInterface;
    private CapsuleLogAdapter capsuleLogAdapter;
    private List<Uri> listUri;
    private int position;

    public ViewCapsuleDialog (Context context, CapsuleLogData capsuleLogData, CapsuleLogAdapter capsuleLogAdapter, int position){
        this.context = context;
        this.capsuleLogData = capsuleLogData;
        this.capsuleLogAdapter = capsuleLogAdapter;
        this.position = position;
    }

    public void call() {
        final Dialog dlg = new Dialog(context);

        //dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_view_capsule);
        dlg.setCancelable(true);
        dlg.show();
        RetrofitClient retrofitClient = new RetrofitClient();
        retrofitInterface = retrofitClient.retrofitInterface;

        TextView tv_title = (TextView) dlg.findViewById(R.id.tv_title);
        TextView tv_location = (TextView) dlg.findViewById(R.id.tv_location);
        TextView tv_text = (TextView) dlg.findViewById(R.id.tv_text);
        TextView tv_d_day = (TextView) dlg.findViewById(R.id.tv_d_day);
        ImageView iv_delete = (ImageView) dlg.findViewById(R.id.btn_delete);

        ViewPager viewPager = (ViewPager) dlg.findViewById(R.id.const_vp);
        TabLayout tabLayout = (TabLayout) dlg.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        listUri = new ArrayList<>();

        if (capsuleLogData.getContentList() != null){
            Log.d("list", capsuleLogData.getContentList().toString());
            for(int i = 0; i < capsuleLogData.getContentList().size(); i++){
                Uri uri = Uri.parse(capsuleLogData.getContentList().get(i).getUrl());
                listUri.add(uri);
            }
        } else {
            String drawablePath = "res:///" + R.drawable.capsule_temp;
            Uri uri = Uri.parse(drawablePath);
            listUri.add(uri);
        }

        ViewPagerAdapterOnlyView viewPagerAdapterOnlyView = new ViewPagerAdapterOnlyView(context,listUri, 0, tabLayout);
        viewPager.setAdapter(viewPagerAdapterOnlyView);

        tv_title.setText(capsuleLogData.getTv_title());
        tv_location.setText(capsuleLogData.getTv_location());
        tv_text.setText(capsuleLogData.getTv_text() != null ? capsuleLogData.getTv_text() : "");
        tv_d_day.setText(capsuleLogData.getD_day());

        /*
        if (capsuleLogData.getIv_url() != null) {
            Glide
                    .with(context)
                    .load(capsuleLogData.getIv_url())
                    .centerCrop()
                    //.override(50,50)
                    .into(iv_thumb);
        }
         */

        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete 통신
                // if 문으로 재확인 구문 필요
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE: {
                                // server 에서 delete 시
                                retrofitInterface.requestDeleteCapsule(capsuleLogData.getCapsule_id()).enqueue(new Callback<Success>() {
                                    @Override
                                    public void onResponse(Call<Success> call, Response<Success> response) {
                                        capsuleLogAdapter.remove(position);
                                        capsuleLogAdapter.notifyDataSetChanged();
                                        dlg.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<Success> call, Throwable t) {
                                        Toast.makeText(v.getContext(), "삭제를 실패했습니다.",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            }

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();



            }
        });



    }
}
