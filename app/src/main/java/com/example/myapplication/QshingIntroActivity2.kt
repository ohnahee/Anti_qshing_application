package com.example.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class QshingIntroActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qshing_info2) // 큐싱 설명 두 번째 화면

        val nextButton = findViewById<Button>(R.id.nextButton)
        nextButton.setOnClickListener {
            val fromMain = intent.getBooleanExtra("fromMain", false)

            if (fromMain) {
                // 메인 화면에서 온 경우, 앱 설명 화면을 건너뛰고 바로 메인 화면으로 이동
                val mainIntent = Intent(this, MainActivity::class.java)
                mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(mainIntent)
            } else {
                // 최초 실행 시, 앱 설명 화면으로 이동
                val sharedPreferences: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                sharedPreferences.edit().putBoolean("isFirstRun", false).apply()

                val tutorialIntent = Intent(this, TutorialActivity::class.java)
                tutorialIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(tutorialIntent)
            }
            finish() // 현재 액티비티 종료
        }
    }
}
