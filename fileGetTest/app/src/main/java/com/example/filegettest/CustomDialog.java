package com.example.filegettest;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class CustomDialog {
    private Context context;

    public CustomDialog(Context context){
        this.context = context;
    }

    public void callFunction() {

        final Dialog dlg = new Dialog(context, R.style.AppBaseTheme);

        // 액티비티의 타이틀 바를 숨김
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.custom_dialog);
        dlg.show();

        final ConstraintLayout cl_camera = (ConstraintLayout) dlg.findViewById(R.id.camera);
        final ConstraintLayout cl_gallery = (ConstraintLayout) dlg.findViewById(R.id.gallery);
        final ConstraintLayout cl_outside = (ConstraintLayout) dlg.findViewById(R.id.outside);

        cl_camera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((MainActivity) context).doTakePhotoAction();
                dlg.dismiss();
            }
        });

        cl_gallery.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ((MainActivity) context).doTakeAlbumAction();
                dlg.dismiss();
            }
        });
        cl_outside.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
    }


}
