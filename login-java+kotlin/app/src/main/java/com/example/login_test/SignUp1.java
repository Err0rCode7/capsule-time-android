package com.example.login_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

        final TextView tv_id = findViewById(R.id.tv_id);
        final TextView tv_pw = findViewById(R.id.tv_pw);
        Button bt_next = findViewById(R.id.bt_next);

        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent next = new Intent(SignUp1.this, SignUp2.class);
                if ( !tv_id.getText().equals("") && !tv_pw.getText().equals("")){

                    String id = tv_id.getText().toString();
                    String pw = tv_id.getText().toString();
                    next.putExtra("id",id);
                    next.putExtra("pw",pw);
                    startActivity(next);
                }
            }
        });


    }
}
