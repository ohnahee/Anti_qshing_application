package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class QushingInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qushing_info)

        // 액션바에 뒤로 가기 버튼 추가
        supportActionBar?.apply {
            title = "큐싱(Qushing) 이란?"
            setDisplayHomeAsUpEnabled(true) // 뒤로 가기 버튼 활성화
        }
    }

    // 뒤로 가기 버튼 동작 처리
    override fun onSupportNavigateUp(): Boolean {
        finish() // 현재 액티비티 종료
        return true
    }
}
