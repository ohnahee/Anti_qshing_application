package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ActionBar 숨기기
        supportActionBar?.hide()

        setContentView(R.layout.activity_splash)

        // XML에서 정의된 뷰 찾기
        val shieldIcon = findViewById<ImageView>(R.id.shieldIcon)
        val appName = findViewById<TextView>(R.id.appName)
        val appDescription = findViewById<TextView>(R.id.appDescription)

        // 애니메이션 추가
        val bounceAnimation = ObjectAnimator.ofFloat(shieldIcon, "translationY", -20f, 20f).apply {
            duration = 3000
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
        bounceAnimation.start()

        // 1.5초 후 메인 액티비티로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1050)
    }
}
