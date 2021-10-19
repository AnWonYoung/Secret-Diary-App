package com.example.secret_diary_app

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener

class DiaryActivity : AppCompatActivity() {

    // 스레드를 열였을 때 UI에서 처리되는 것은 ui, 혹은 메인 스레드이며
    // 새로운 스레드를 열었을 때는 이곳의 스레드가 따로 열림
    // ui와 새로운 생성 스레드를 함께 연결해주어야 하는 경우가 발생할 수 있음
    // 메인이 아닌 곳에서는 다른 스레드를 수행할 수 없기 때문이며, 이를 연결해주는 구현 중 하나가 handler

    private val handler = Handler(Looper.getMainLooper()) // MainLooper - 메인 스레드에 연결되어진 핸들러가 생성

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        val diaryContext = findViewById<EditText>(R.id.diaryContext) // context는 변하거나 밖에서 쓰일 일이 없으므로
                                                                     // onCreate 안에 선언해도 무방하다 (by lazy도 상관은 없음)

        val detailPrefer : SharedPreferences = getSharedPreferences("diary", Context.MODE_PRIVATE)
        diaryContext.setText(detailPrefer.getString("context", " ")) // diary안에 저장되어 있는 context를 가져올 것, 없다면 빈 text를 반환

// getSharedPreferences를 이용한 자동저장 및 임시저장 구현

        val runnable = Runnable {
            getSharedPreferences("diary", Context.MODE_PRIVATE).edit {
                putString("context", diaryContext.text.toString()) // 수시로 backGround의 글을 저장할 수 있도록 스레드를 비동기로 넘겨주는 구현
            }
            Log.d("DiaryActivity","SAVE~!~!~!~ ${diaryContext.text.toString()}")
        }

        diaryContext.addTextChangedListener{ // text 변경때마다 이곳이 실행되도록
            Log.d("DiaryActivty", "TextChanged :: ${it}")
            // 한 글자마다 저장하는 게 아니라 textChange가 일어나면 runnalbe을 지우고
            // textChange가 0.5초 안에 일어나지 않으면, 저장하는 방식으로 임시저장을 구현
            handler.removeCallbacks(runnable) // addTextChangedListener로 들어오면 이전의 runnalbe을 지우도록
            handler.postDelayed(runnable, 500) // 0.5 초 이후에 runnalbe이 실행되도록

        }
    }
}


/* <안드로이드 스튜디오 스레드>
 app 성능을 위해서 멀티 스레드를 이용하는 경우가 많다.
 다만, UI를 업데이트하는데는 단일 스레드가 적용되고 있다.
 멀티 스테드로 UI 업데이트를 이용하지 않는 이유는?
 -> 동일한 UI 자원이 사용될 때 교착 혹은 경합 상태와 같이
 여러가지 문제점이 발생할 수 있기 때문이다.

 앱이 실행되면서 메인(UI 스레드가 생성됨)
 UI는 메인 스레드에서만 동작할 수 있도록 함,
 따라서 서브 스레드에서 Handler, Looper를 이용하여
 메인 스레드로 UI 처리 작업을 전달하도록 해야 한다. */