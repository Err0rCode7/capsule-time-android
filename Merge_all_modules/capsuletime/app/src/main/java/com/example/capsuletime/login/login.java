package com.example.capsuletime.login;

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
import android.widget.Toast;


import com.example.capsuletime.R;
import com.example.capsuletime.RetrofitClient;
import com.example.capsuletime.RetrofitInterface;
import com.example.capsuletime.Success;
import com.example.capsuletime.mainpages.mypage.mypage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// alt + enter : auto

public class login extends AppCompatActivity {

    private RetrofitInterface retrofitInterface;
    private EditText idText;
    private EditText pwText;
    private Button button;
    private TextView signUp;
    private String temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                Log.d("TAG",id+ " "+pw);
                temp = id;
                retrofitInterface.requestLogin(id, pw).enqueue(new Callback<Success>() {
                    @Override
                    public void onResponse(Call<Success> call, Response<Success> response) {
                        Success auth = response.body();
                        // Header Code 확인 Log.d("TAG",Integer.toString(response.code()));


                        if (auth.getSuccess().equals("true")) {

                            Toast.makeText(v.getContext(),"정상적으로 로그인 되었습니다.",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), mypage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("user_id",id);
                            startActivity(intent);
                        } else {
                            Toast.makeText(v.getContext(),"Id와 Password를 확인해주세요.",Toast.LENGTH_SHORT).show();
                        }
                        //Toast.makeText(v.getContext(),"Id와 Password를 확인해주세요.",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Success> call, Throwable t) {
                        Toast.makeText(v.getContext(),"서버와 통신이 불가능합니ㄷ다.",Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(login.this, SignUp1.class ); //(현재액티비티, 이동할액티비티)
                intent.putExtra("id", temp); // 인텐트 값을 전달
                startActivity(intent); // 액티비티 이동
            }

            @Override
            public void updateDrawState(final TextPaint textPaint) { // 색 정의
                textPaint.setColor(login.this.getResources().getColor(R.color.gray));
                textPaint.setUnderlineText(true);
            }

        },0,6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signUp.setMovementMethod(LinkMovementMethod.getInstance());

    }
}
