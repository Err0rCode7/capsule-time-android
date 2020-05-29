package com.example.filegettest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    private static final int PICK_FROM_CAMERA = 0;

    private static final int PICK_FROM_ALBUM = 1;

    private static final int CROP_FROM_iMAGE = 2;


    private Uri mImageCaptureUri;

    private ImageView iv_UserPhoto;

    private int id_view;

    private String absoultePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_UserPhoto = (ImageView) this.findViewById(R.id.user_image);
        Button btn_agreeJoin =  (Button) this.findViewById(R.id.btn_UploadPicture);

        btn_agreeJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doTakePhotoAction();
            }
        });




    }

    // 카메라에서 사진 촬영
    public void doTakePhotoAction() { // 카메라 촬영후 이미지 가져오기


        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) { // Camera Denied
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},0);
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

            intent.putExtra(MediaStore.EXTRA_OUTPUT,mImageCaptureUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }


    }
    // 앨범에서 이미지 가져오기
    public void doTakeAlbumAction() { //앨범에서 이미지 가져오기

        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK)
            return;

        switch (requestCode) {

            case PICK_FROM_ALBUM: {
                //
                mImageCaptureUri = data.getData();
                Log.d("user_img", mImageCaptureUri.getPath().toString());
            }
            case PICK_FROM_CAMERA: {
                // image resize 후
                // 크롭 어플리케이션 호출

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
                break;
            }
            case CROP_FROM_iMAGE: {
                // 크롭이 된 이후의 이미지를 넘겨 받는다
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일 삭제
                if(resultCode != RESULT_OK) {
                    return;
                }
                final Bundle extras = data.getExtras();

                String filePath = this.getExternalFilesDir(null).getAbsolutePath()+"/SmartWhell/" + System.currentTimeMillis()+".jpg";

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
        }

    }


}
