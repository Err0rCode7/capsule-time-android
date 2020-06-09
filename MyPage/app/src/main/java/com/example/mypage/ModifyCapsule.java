package com.example.mypage;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;
import retrofit2.http.Tag;

public class ModifyCapsule extends AppCompatActivity {

    private static final String TAG = "ModifyCapsule";
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_iMAGE = 2;
    private String absoultePath;
    private int capsule_id;
    private List<Uri> list;
    private Uri mImageCaptureUri;
    private Uri photoUri;
    private Uri albumUri;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private RetrofitInterface retrofitInterface;
    private String mCurrentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_capsule);

        RetrofitClient retrofitClient = new RetrofitClient();
        retrofitInterface = retrofitClient.retrofitInterface;

        list = new ArrayList<>();

        Intent intent = getIntent();
        capsule_id = intent.getIntExtra("capsule_id",-1);
        /*
        list.add("http://118.44.168.218:7070/contents/1.jpeg");
        list.add("http://118.44.168.218:7070/contents/2.jpeg");
        list.add("http://118.44.168.218:7070/contents/1.mp4");
        */
        ImageView imageView = (ImageView)findViewById(R.id.iv_back);
        Button button = (Button)findViewById(R.id.btn_set);
        TextInputEditText tv_title = (TextInputEditText)findViewById(R.id.tv_title);
        TextInputEditText tv_text = (TextInputEditText)findViewById(R.id.tv_text);
        TextInputEditText tv_tag = (TextInputEditText)findViewById(R.id.tv_tag);

        viewPager = (ViewPager)findViewById(R.id.const_vp);
        tabLayout = (TabLayout)findViewById(R.id.tab_layout);

        /*
        Uri uri = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.plus)
                + '/' + getResources().getResourceTypeName(R.drawable.plus) + '/' + getResources().getResourceEntryName(R.drawable.plus) );

        */
        Uri test = Uri.parse("");
        list.add(test);

        tabLayout.setupWithViewPager(viewPager, true);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, list, 1, tabLayout);
        viewPager.setAdapter(viewPagerAdapter);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지 등록
                String title = Objects.requireNonNull(tv_title.getText()).toString(); // null -> ""
                String text = Objects.requireNonNull(tv_text.getText()).toString();
                String tag = Objects.requireNonNull(tv_tag.getText()).toString();
                if (title.equals("")){
                    Toast.makeText(ModifyCapsule.this,"제목을 입력해주세요.",Toast.LENGTH_SHORT).show();
                   return;
                }
                RequestBody id_body = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(capsule_id));
                RequestBody title_body = RequestBody.create(MediaType.parse("text/plain"), title);
                RequestBody text_body = RequestBody.create(MediaType.parse("text/plain"), text);
                RequestBody tag_body = RequestBody.create(MediaType.parse("text/plain"), tag);

                List<MultipartBody.Part> parts = new ArrayList<>();
                List<String> tags = new ArrayList<>();
                //
                for (int i = 0; i < list.size(); i++){

                        String absolutePath = getPath(v.getContext(),list.get(i));
                        if (absolutePath != null)
                        {
                            File file = new File(absolutePath);
                            String type = getMimeType(file);
                            RequestBody requestFile = RequestBody.create(MediaType.parse(type), file);
                            parts.add(MultipartBody.Part.createFormData("file",file.getName(), requestFile));
                        }




                }

                /*
                File file = new File("/storage/emulated/0/Download/google.jpg");
                String type = getMimeType(file);
                RequestBody requestFile = RequestBody.create(MediaType.parse(type), file);
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                */
                if( capsule_id != -1) {
                    retrofitInterface.requestPutCapsule(id_body, title_body, text_body, parts).enqueue(new Callback<Success>() {
                        @Override
                        public void onResponse(Call<Success> call, Response<Success> response) {
                            Log.d(TAG,"success");

                            Toast.makeText(ModifyCapsule.this,"캡슐 수정이 완료되었습니다.",Toast.LENGTH_SHORT).show();

                            //refresh ViewPager
                            Intent intent = new Intent(ModifyCapsule.this, MyPage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(intent);

                        }

                        @Override
                        public void onFailure(Call<Success> call, Throwable t) {
                            Log.d(TAG,"fail");
                            Toast.makeText(ModifyCapsule.this,"캡슐 내용을 수정해주세요",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ModifyCapsule.this,"잘못된 캡슐 ID",Toast.LENGTH_SHORT).show();
                }



            }
        });
    }


    // 카메라에서 사진 촬영
    public void doTakePhotoAction() { // 카메라 촬영후 이미지 가져오기


        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) { // Camera Denied
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},0);
            if (permissionCheck != PackageManager.PERMISSION_DENIED) {

            }

        } else { // Camera Accept
            String state = Environment.getExternalStorageState();

            // 외장 메모리 검사
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getPackageManager()) != null){
                    File tempFile = null;
                    try {
                        tempFile = createImageFile();
                    } catch (IOException ex){
                        Log.e("captureCamera Error", ex.toString());
                    }

                    if(tempFile != null){
                        // getUriForFile의 두 번째 인자는 Manifest provier의 authorites와 일치해야 함

                        mImageCaptureUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", tempFile);
                        // 인텐트에 전달할 때는 FileProvier의 Return값인 content://로만!!, providerURI의 값에 카메라 데이터를 넣어 보냄
                        Log.d(TAG,mImageCaptureUri.toString());
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    }
                }
            }
        }


    }

    public void cropImage(){
        Log.i("cropImage", "Call");
        Log.i("cropImage", "photoURI : " + photoUri + " / albumURI : " + albumUri);

        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        // 50x50픽셀미만은 편집할 수 없다는 문구 처리 + 갤러리, 포토 둘다 호환하는 방법
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        cropIntent.setDataAndType(mImageCaptureUri, "image/*");
        //cropIntent.putExtra("outputX", 200); // crop한 이미지의 x축 크기, 결과물의 크기
        //cropIntent.putExtra("outputY", 200); // crop한 이미지의 y축 크기
        cropIntent.putExtra("aspectX", 1); // crop 박스의 x축 비율, 1&1이면 정사각형
        cropIntent.putExtra("aspectY", 1); // crop 박스의 y축 비율
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", albumUri); // 크랍된 이미지를 해당 경로에 저장
        startActivityForResult(cropIntent, CROP_FROM_iMAGE);
    }

    private void galleryAddPic(){
        Log.i("galleryAddPic", "Call");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // 해당 경로에 있는 파일을 객체화(새로 파일을 만든다는 것으로 이해하면 안 됨)
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "사진이 앨범에 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    // 앨범에서 이미지 가져오기
    public void doTakeAlbumAction() { //앨범에서 이미지 가져오기

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
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

        if (resultCode != RESULT_OK) {
            Log.d(TAG,"request Not OK");
            return;
        }


        switch (requestCode) {

            case PICK_FROM_ALBUM: {

                final List<Bitmap> bitmaps = new ArrayList<>();
                ClipData clipData = data.getClipData();

                if (clipData != null) {// 이미지 여러개 클
                    list.clear();
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        list.add(imageUri);
                        /*
                        try {
                            InputStream is = getContentResolver().openInputStream(imageUri);

                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            bitmaps.add(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                         */
                    }

                    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, list, 0, tabLayout);
                    viewPager.setAdapter(viewPagerAdapter);

                } else { // 이미지 한개 클릭
                    Uri imageUri = data.getData();

                    list.clear();
                    list.add(imageUri);
                    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, list, 0, tabLayout);
                    viewPager.setAdapter(viewPagerAdapter);
                    /*
                    try {
                        InputStream is = getContentResolver().openInputStream(imageUri);

                        Bitmap bitmap = BitmapFactory.decodeStream(is);

                        bitmaps.add(bitmap);
                        imageView.setImageBitmap(bitmap); //사진 출력

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                     */

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

                if (data.getData() != null) {
                    try {
                        File albumFile = null;
                        albumFile = createImageFile();
                        photoUri = data.getData();
                        albumUri = Uri.fromFile(albumFile);
                        Log.d("CropTAG","CROPCROP");
                        cropImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG,"data null");
                }

                break;
            }
            case CROP_FROM_iMAGE: {


                galleryAddPic();
                list.clear();
                list.add(albumUri);
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, list, 0, tabLayout);
                viewPager.setAdapter(viewPagerAdapter);
                // 임시 파일 삭제
                /*
                File f = new File(mImageCaptureUri.getPath());
                if(f.exists()){
                    f.delete();
                }
                 */

                break;

            }

        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "temp");
        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString());
            storageDir.mkdirs();
        }

        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    @NonNull
    static String getMimeType(@NonNull File file) {
        String type = null;
        final String url = file.toString();
        final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        if (type == null) {
            type = "image/*"; // fallback type. You might set it to */*
        }
        return type;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
