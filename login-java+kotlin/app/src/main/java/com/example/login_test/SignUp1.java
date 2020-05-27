package com.example.login_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SignUp1 extends AppCompatActivity {

    private TextView tv_sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up1);

        tv_sub = findViewById(R.id.SignUpText);

        Intent intent = getIntent();
        String idStr = intent.getStringExtra("id"); // 보내는 곳과 받는 곳의 name을 맞춤

        tv_sub.setText(idStr);

    }
}
