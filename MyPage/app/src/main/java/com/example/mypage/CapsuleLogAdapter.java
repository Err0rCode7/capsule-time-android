package com.example.mypage;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.module.AppGlideModule;

import java.util.ArrayList;


public class CapsuleLogAdapter extends RecyclerView.Adapter<CapsuleLogAdapter.CustomViewHolder> {

    private ArrayList<CapsuleLogData> arrayList;
    private Context context;
    private static final String TAG = "CapsuleLogAdapter";

    public CapsuleLogAdapter(ArrayList<CapsuleLogData> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public CapsuleLogAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_capsule_log,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CapsuleLogAdapter.CustomViewHolder holder, int position) {

        Glide
            .with(context)
            .load(arrayList.get(position).getIv_url())
            .centerCrop()
            .override(50,50)
            .into(holder.iv_thumb);
        //holder.iv_thumb.setImageResource(arrayList.get(position).getIv_thumb());
        holder.tv_title.setText(arrayList.get(position).getTv_title());
        holder.tv_tags.setText(arrayList.get(position).getTv_tags());
        holder.tv_location.setText(arrayList.get(position).getTv_location());
        holder.tv_opened_date.setText(arrayList.get(position).getTv_opened_date());
        holder.tv_created_date.setText(arrayList.get(position).getTv_created_date());

        if (arrayList.get(position).getState_temp() == 1) {
            // TODO 임시저장 색깔
            Log.d(TAG,"state_temp: 1");
            holder.cl_capsule.setBackgroundColor(Color.parseColor("#3Fff91b4"));
        } else {
            //
            holder.cl_capsule.setBackgroundColor(Color.parseColor("#3Fa9c8fd"));
        }

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curName = holder.tv_title.getText().toString();
                Toast.makeText(v.getContext(), curName, Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                remove(holder.getAdapterPosition());

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public void remove(int position) {
        try {
            arrayList.remove(position);
            notifyItemRemoved(position);
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView iv_thumb;
        protected TextView tv_title;
        protected TextView tv_tags;
        protected TextView tv_location;
        protected TextView tv_opened_date;
        protected TextView tv_created_date;
        protected ConstraintLayout cl_capsule;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.iv_thumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
            this.tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            this.tv_tags = (TextView) itemView.findViewById(R.id.tv_tags);
            this.tv_location = (TextView) itemView.findViewById(R.id.tv_location);
            this.tv_opened_date = (TextView) itemView.findViewById(R.id.tv_opened_date);
            this.tv_created_date = (TextView) itemView.findViewById(R.id.tv_created_date);
            this.cl_capsule = (ConstraintLayout) itemView.findViewById(R.id.cl_capsule);

        }
    }
}
