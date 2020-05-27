package com.example.login_test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// alt + enter : auto

public class MainActivity extends AppCompatActivity {

    private Auth_service auth_service;
    private EditText idText;
    private EditText pwText;
    private Button button;
    private TextView signUp;
    private String temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RetrofitClient retrofit_Client = new RetrofitClient();

        auth_service = retrofit_Client.auth_service;

        idText = findViewById(R.id.editText);
        pwText = findViewById(R.id.editText2);
        button = findViewById(R.id.button);

        button.setOnClickListener(new Button.OnClickListener() {
           @Override
           public void onClick(View v){
               final String id = idText.getText().toString();
               final String pw = pwText.getText().toString();
               temp = id;
               auth_service.requestLogin(id, pw).enqueue(new Callback<Auth_data>() {
                   @Override
                   public void onResponse(Call<Auth_data> call, Response<Auth_data> response) {
                       Auth_data auth = response.body();
                       AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                       dialog.setTitle("알람!");

                       if(auth.getSuccess().equals("true"))
                           dialog.setMessage("Success!!");
                       else
                           dialog.setMessage("Success : false");

                       dialog.show();
                   }

                   @Override
                   public void onFailure(Call<Auth_data> call, Throwable t) {
                       Log.d("DEBUG :",t.getMessage());
                       AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                       dialog.setTitle("알람!");
                       dialog.setMessage("통신에 실패했습니다.");
                       dialog.show();
                   }
               });




           }


        });
        signUp = findViewById(R.id.SignUp);
        Spannable span = (Spannable)signUp.getText();

        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View v) {
                Intent intent = new Intent(MainActivity.this, SignUp1.class ); //(현재액티비티, 이동할액티비티)
                intent.putExtra("id", temp); // 인텐트 값을 전달
                startActivity(intent); // 액티비티 이동
            }
        },0,6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signUp.setMovementMethod(LinkMovementMethod.getInstance());

    }
}
