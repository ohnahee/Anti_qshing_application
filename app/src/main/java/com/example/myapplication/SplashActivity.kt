package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
            val isFirstRun = prefs.getBoolean("isFirstRun", true)

            if (isFirstRun) {
                // 최초 실행: 큐싱 설명 액티비티로 이동
                val intent = Intent(this, QshingIntroActivity::class.java)
                startActivity(intent)

                // prefs.edit().putBoolean("isFirstRun", false).apply()

            } else {
                // 일반 실행: 메인 화면으로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            finish()
        }, 1500)
    }
}
