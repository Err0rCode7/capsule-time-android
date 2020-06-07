package com.example.mypage.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mypage.CapsuleLogAdapter;
import com.example.mypage.CapsuleLogData;
import com.example.mypage.R;
import com.example.mypage.RetrofitClient;
import com.example.mypage.RetrofitInterface;
import com.example.mypage.Success;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewCapsuleDialog {
    private Context context;
    private CapsuleLogData capsuleLogData;
    private RetrofitInterface retrofitInterface;
    private CapsuleLogAdapter capsuleLogAdapter;
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
        TextView tv_location = (TextView) dlg.findViewById(R.id.tv_text);
        TextView tv_created_date = (TextView) dlg.findViewById(R.id.tv_created_date);
        TextView tv_d_date = (TextView) dlg.findViewById(R.id.tv_d_day);
        ImageView iv_thumb = (ImageView) dlg.findViewById(R.id.iv_thumb);
        ImageView iv_delete = (ImageView) dlg.findViewById(R.id.iv_delete);

        tv_title.setText(capsuleLogData.getTv_title());
        tv_location.setText(capsuleLogData.getTv_location());
        tv_created_date.setText(capsuleLogData.getTv_created_date());
        tv_d_date.setText(capsuleLogData.getD_day());

        if (capsuleLogData.getIv_url() != null) {
            Glide
                    .with(context)
                    .load(capsuleLogData.getIv_url())
                    .centerCrop()
                    //.override(50,50)
                    .into(iv_thumb);
        }

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
