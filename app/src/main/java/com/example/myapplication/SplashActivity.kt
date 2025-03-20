package com.example.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 다크 모드 강제 비활성화 (항상 라이트 모드 유지)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        supportActionBar?.hide() // 상단 액션바 숨기기
        setContentView(R.layout.activity_splash)

        val sharedPref: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isFirstRun = sharedPref.getBoolean("isFirstRun", true)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isFirstRun) {
                startActivity(Intent(this, QshingIntroActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish() // SplashActivity를 완전히 종료하여 백그라운드에 남지 않도록 함
        }, 2000) // 2초 후 실행
    }
}
