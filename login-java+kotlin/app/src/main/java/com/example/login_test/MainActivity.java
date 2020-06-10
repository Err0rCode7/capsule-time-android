package com.example.login_test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
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

    private RetrofitInterface retrofitInterface;
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

        retrofitInterface = retrofit_Client.retrofitInterface;

        idText = findViewById(R.id.et_nick_name);
        pwText = findViewById(R.id.editText2);
        button = findViewById(R.id.button);

        button.setOnClickListener(new Button.OnClickListener() {
           @Override
           public void onClick(View v){
               final String id = idText.getText().toString();
               final String pw = pwText.getText().toString();
               temp = id;
               retrofitInterface.requestLogin(id, pw).enqueue(new Callback<Success>() {
                   @Override
                   public void onResponse(Call<Success> call, Response<Success> response) {
                       Success auth = response.body();
                       AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                       dialog.setTitle("알람!");

                       if(auth.getSuccess().equals("true"))
                           dialog.setMessage("Success!!");
                       else
                           dialog.setMessage("Success : false");

                       dialog.show();
                   }

                   @Override
                   public void onFailure(Call<Success> call, Throwable t) {
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
        ForegroundColorSpan foregroundSpan = new ForegroundColorSpan(Color.GRAY);
        span.setSpan(foregroundSpan, 0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View v) {
                Intent intent = new Intent(MainActivity.this, SignUp1.class ); //(현재액티비티, 이동할액티비티)
                intent.putExtra("id", temp); // 인텐트 값을 전달
                startActivity(intent); // 액티비티 이동
            }

            @Override
            public void updateDrawState(final TextPaint textPaint) { // 색 정의
                textPaint.setColor(MainActivity.this.getResources().getColor(R.color.gray));
                textPaint.setUnderlineText(true);
            }

        },0,6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signUp.setMovementMethod(LinkMovementMethod.getInstance());

    }
}
