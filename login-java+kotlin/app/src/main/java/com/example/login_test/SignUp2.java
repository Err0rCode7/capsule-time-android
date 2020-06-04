package com.example.login_test;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class SignUp2 extends AppCompatActivity {
    private static final int PICK_FROM_CAMERA = 0;

    private static final int PICK_FROM_ALBUM = 1;

    private static final int CROP_FROM_iMAGE = 2;


    private Uri mImageCaptureUri;

    private ImageView iv_UserPhoto;

    private int id_view;

    private String absoultePath;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        iv_UserPhoto = (ImageView) this.findViewById(R.id.user_image);
        CardView cv_agreeJoin =  (CardView) this.findViewById(R.id.btn_UploadPicture);
        Button btn_finish = (Button) this.findViewById(R.id.btn_signupfinish);
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "권한 설정 완료");
            } else {
                Log.d("TAG", "권한 설정 요청");
                ActivityCompat.requestPermissions(SignUp2.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        */

        cv_agreeJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog custom_dlg = new CustomDialog(SignUp2.this);
                custom_dlg.callFunction();
            }
        });

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 정보를 가져와서 서버와 통신 필요한 부분 //

                // 회원가입 정보를 가져와서 서버와 통신 필요한 부분 //

                Toast toast = Toast.makeText(getApplicationContext(), "SignUp Completed", Toast.LENGTH_SHORT);
                toast.show();

                Intent intent = new Intent(SignUp2.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
            }
        });

    }
    // 사진찍기 or 카메라 촬영 다이얼로그
    private void photoDialog() {
        final CharSequence[] items = {"갤러리", "카메라", "취소"};
        AlertDialog.Builder selectDialog = new AlertDialog.Builder(SignUp2.this);

        selectDialog.setTitle("사진선택");
        selectDialog.setCancelable(true);

        selectDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int index) {
                switch (index) {
                    case 0: {
                        doTakeAlbumAction();
                        break;
                    }
                    case 1: {
                        doTakePhotoAction();
                        break;
                    }
                    case 2: {
                        break;
                    }
                }
            }
        });

        selectDialog.show();
    }


    // 카메라에서 사진 촬영
    public void doTakePhotoAction() { // 카메라 촬영후 이미지 가져오기


        int permissionCheck = ContextCompat.checkSelfPermission(SignUp2.this, Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) { // Camera Denied
            ActivityCompat.requestPermissions(SignUp2.this, new String[]{Manifest.permission.CAMERA},0);
            if (permissionCheck != PackageManager.PERMISSION_DENIED) {

            }

        } else { // Camera Accept
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String filepath = this.getExternalFilesDir(null).getAbsolutePath();
            //임시로 사용할 파일의 경로 생성
            String url = "tmp_" + String.valueOf(System.currentTimeMillis())+ ".jpg";
            filepath = filepath + url;
            File tempFile = new File (filepath);
            mImageCaptureUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider" , tempFile);
            Log.d("msg",filepath);

            //intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            //intent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());
            startActivityForResult(intent, PICK_FROM_CAMERA);

        }


    }
    // 앨범에서 이미지 가져오기
    public void doTakeAlbumAction() { //앨범에서 이미지 가져오기

        if (ActivityCompat.checkSelfPermission(SignUp2.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(SignUp2.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            return ;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_FROM_ALBUM);
        /*
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
        */

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK)
            return;

        switch (requestCode) {

            case PICK_FROM_ALBUM: {
                //
                final ImageView imageView = findViewById(R.id.user_image);

                final List<Bitmap> bitmaps = new ArrayList<>();
                ClipData clipData = data.getClipData();

                if (clipData != null) {// 이미지 여러개 클
                    for (int i = 0; i < clipData.getItemCount(); i ++){
                        Uri imageUri = clipData.getItemAt(i).getUri();

                        try {
                            InputStream is = getContentResolver().openInputStream(imageUri);

                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            bitmaps.add(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 이미지 한개 클릭
                    Uri imageUri = data.getData();
                    try {
                        InputStream is = getContentResolver().openInputStream(imageUri);

                        Bitmap bitmap = BitmapFactory.decodeStream(is);

                        bitmaps.add(bitmap);
                        imageView.setImageBitmap(bitmap); //사진 출력

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                break;
                // 이미지 뷰 후처리 필요

                /* 쓰레드를 이용한 이미지 리스트가 변화하면서 띄워지는 예제
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (final Bitmap b : bitmaps) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(b);
                                }
                            });

                            try {
                                Thread.sleep(3000);
                            } catch ( InterruptedException e ) {
                                e.printStackTrace();;
                            }
                        }
                    }
                });
                */

                /*
                mImageCaptureUri = data.getData();
                Log.d("user_img", mImageCaptureUri.getPath().toString());
                */

            }
            case PICK_FROM_CAMERA: {
                // image resize 후
                // 크롭 어플리케이션 호출

                //File file = new file (mImageCaptureUri);
                try {
                    if (data.hasExtra("data"))
                    {
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        //InputStream in = getContentResolver().openInputStream(data.getData());
                        //Bitmap bitmap = BitmapFactory.decodeStream(in);

                        if ( bitmap != null){
                            ImageView imageView = findViewById(R.id.user_image);
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                // CROP할 이미지를 200*200 크기로 저장

                intent.putExtra("outputX", 200); // CROP한 이미지의 x축 크기
                intent.putExtra("outputY", 200); // CROP한 이미지의 y축 크기
                intent.putExtra("aspectX", 1); // CROP 박스의 X축 비율
                intent.putExtra("aspectY", 1); // CROP 박스의 Y축 비율
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_iMAGE);
                */

                break;
            }
            case CROP_FROM_iMAGE: {
                // 크롭이 된 이후의 이미지를 넘겨 받는다
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일 삭제
                /*
                if(resultCode != RESULT_OK) {
                    return;
                }
                */
                final Bundle extras = data.getExtras();

                String filePath = this.getExternalFilesDir(null).getAbsolutePath()+"/temppp/" + System.currentTimeMillis()+".jpg";

                if(extras != null) {
                    Bitmap photo = extras.getParcelable("data"); // CROP된 BITMAP
                    iv_UserPhoto.setImageBitmap(photo); // 이미지칸에 크롭된 비트맵 보여줌

                    // storeCropImage(photo, filePath); db에 image저장
                    absoultePath = filePath;
                    break;

                }
                // 임시 파일 삭제
                /*
                File f = new File(mImageCaptureUri.getPath());
                if(f.exists()){
                    f.delete();
                }

                 */
            }
            break;

        }

    }

}
