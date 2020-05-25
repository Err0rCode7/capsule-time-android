package com.example.capsule_time

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var retrofit = Retrofit.Builder() //retrofit 객체 생성
            .baseUrl("http://118.44.168.218:7070") // server url 등록
            .addConverterFactory(GsonConverterFactory.create()) // JSON을 java object로 바꿔준다.
            .build()

        var loginService = retrofit.create(LoginService::class.java) // LoginService를 retrofit에 올려준다.


        button.setOnClickListener{
            var textId = editText.text.toString()
            var textPw = editText2.text.toString()

            loginService.requestLogin(textId, textPw).enqueue(object: Callback<Login>{ // object 우클-> generate -> onFailure, onResponse 자동생성
                override fun onFailure(call: Call<Login>, t: Throwable) {
                    Log.d("DEBUG :",t.message)
                    // 웹통신에 실패했을때 실행
                    var dialog = AlertDialog.Builder(this@MainActivity) //this class
                    dialog.setTitle("실패!")
                    dialog.setMessage("통신에 실패했습니다.")
                    dialog.show()
                }

                override fun onResponse(call: Call<Login>, response: Response<Login>) {
                    // 웹통신에 성공했을때, 응답값을 받아옴.
                    var login = response.body()

                    var dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("알람!")
                    dialog.setMessage(" success = " + login?.success) // response가 null일 때를 대비하여 ?로 safe 처리
                    dialog.show()
                }

            })


        }
    }
}
