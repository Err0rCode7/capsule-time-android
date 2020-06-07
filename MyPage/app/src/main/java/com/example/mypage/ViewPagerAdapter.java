package com.example.mypage;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.loader.content.CursorLoader;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private static final String TAG = "ViewPagerAdapter";
    private Context context;
    private List<Uri> list;
    private int temp;

    public ViewPagerAdapter (Context context, List<Uri> list, int temp){
        this.context = context;
        this.list = list;
        this.temp = temp;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = null;
        if (temp == 1) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.image_view, container, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_pager);
            Log.d(TAG, "glide Test");

            File file = FileUtils.getFile(view.getContext(),list.get(position));
            Glide
                    .with(context)
                    .load("http://118.44.168.218:7070/contents/2.jpeg")
                    .centerCrop()
                    //.override(50,50)
                    .into(imageView);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomDialog customDialog = new CustomDialog(context);
                    customDialog.callFunction();
                }
            });
            return view;
        } else {

            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.image_view, container, false);
            // videoView imageView 구분 구문 필요
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_pager);
            Log.d(TAG, "glide Test");


            Glide
                    .with(context)
                    .load(list.get(position))
                    .centerCrop()
                    //.override(50,50)
                    .into(imageView);
            container.addView(view);
            return view;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }


}
