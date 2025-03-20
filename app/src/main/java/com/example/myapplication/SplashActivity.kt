package com.example.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()  // 상단바 숨기기
        setContentView(R.layout.activity_splash)

        val sharedPref: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isFirstRun = sharedPref.getBoolean("isFirstRun", true)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isFirstRun) {
                // 최초 실행 시 큐싱 설명 페이지로 이동
                startActivity(Intent(this, QshingIntroActivity::class.java))
                sharedPref.edit().putBoolean("isFirstRun", false).apply()
            } else {
                // 이후 실행 시 메인 페이지로 이동
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }, 2000)
    }
}

