package com.example.secret_diary_app

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit

// by lazy를 먼저 선언해주는 이유는, 혅재 MainActivity가 생성될 시점에서는 view가 다 만들어지지 않았기 때문
// 아래의 onCreate에서 함수부터는 view가 다 만들어졌음을 의미
class MainActivity : AppCompatActivity() {
    private val numberPicker1 : NumberPicker by lazy{
        findViewById<NumberPicker>(R.id.numberPicker1)
            .apply { // 미리 초기값을 줄 때는 findViewById 타입 명시가 필요함
                minValue = 0
                maxValue = 9
            }
    }
    private val numberPicker2 : NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker2)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }
    private val numberPicker3 : NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker3)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }

    private val openButton : AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.openButton)
    }

    private val changePasswordButton : AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.changePasswordButton)
    }

    private var changePasswordMode = false // 예외처리

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // numberPicker 접근, view에 대해서 apply를 통해 속성을 할당, 이곳에서 호출하지 않으면
        // lazy하게 init되는 시점이 바로 numberPicker를 사용할 때가 됨.
        // 하지만 numberPicker는 직접적으로 사용하는 게 아니기 때문에 이곳에서 먼저 호출
        numberPicker1
        numberPicker2
        numberPicker3
        // 눌렀을 때 저장된 비번을 가져와서, numberPicker와 비교를 해주어야 함
        // 그전에 미리 비밀번호를 저장해야 하는데, 방법은 총 두 가지가 있음
        // 1. local DB 2. 파일에 직접 할때 (편하게 sharedPreferences를 사용할 수 있음)
        openButton.setOnClickListener{ // Preferences 파일을 다른 앱과 같이 사용할 수 있도록 하는 기능
            if(changePasswordMode) { //  비밀번호를 변경했을 땐 다른 작동이 불가능 하도록
                Toast.makeText(this, "비밀번호 변경 중입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passwordPreferences = getSharedPreferences("password", Context.MODE_PRIVATE) // 비밀번호를 다른 앱과 공유하지 않도록 MODE_PRIVATE 설정

            val userPassword = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            if (passwordPreferences.getString("password", "000") // key 값은 password
                .equals(userPassword)) {
//                성공했을 때
//                startActivity()
            } else {
                alertDialog()
            }
        }

        changePasswordButton.setOnClickListener{
            val passwordPreferences = getSharedPreferences("password", Context.MODE_PRIVATE) // 비밀번호를 다른 앱과 공유하지 않도록 MODE_PRIVATE 설정
            val userPassword = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"
            //  비밀번호를 변겅하던 중에 다른 동작이 하지 못하도록 예외처리가 필요함
            if(changePasswordMode) {
                // 변경한 비밀번호를 저장할 수 있도록 구현
                val passwordPreferences = getSharedPreferences("password", Context.MODE_PRIVATE)

                passwordPreferences.edit { // edit을 람다형식으로 돌리면서 commit에 관한 오류를 예방할 수 있음
                    val userPassword = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"
                     putString("password", userPassword)
                     commit() // commit은 다 저장될 때까지 ui를 기다리는, apply는 비동기적으로 다음 실행을 할 수 있도록 함
                              // 긴 시간이 필요하지 않기 때문에 commit을 통해서 기다렸다가 다음이 실행될 수 있도록 함
                }
                changePasswordMode = false  // 비밀변경 변동 완료 후 비활성화
                changePasswordButton.setBackgroundColor(Color.parseColor("FF6200EE"))

            }else {
                // Mode가 활성화 되도록 > 비밀번호가 맞는지를 체크
                // 현재는 변경 모드가 활성화 된 것
                if (passwordPreferences.getString("password", "000") // key 값은 password
                        .equals(userPassword)) {
                    changePasswordMode = true // << 패스워드 버튼을 누르고 변경이 가능할 때
                    Toast.makeText(this, "변경할 패스워드를 입력해주세요", Toast.LENGTH_SHORT).show()
                    changePasswordButton.setBackgroundColor(Color.RED) // 레드가 들어오면 활성화가 된 것

                } else {
                    alertDialog()
                }
            }
        }

    }
// Dialog 함수로 빼기
    private fun alertDialog() {
        AlertDialog.Builder(this)
//                 실패했을 때
            .setTitle("실패")
            .setMessage("비밀번호가 틀렸습니다.")
            // setPositiveButton는 두 개의 인자값을 가지고 있음
            .setPositiveButton("확인") { dialog, which -> } // _ , _로 대체 가능, 불필요한 코드 생략
            .create()
            .show()
    }
}