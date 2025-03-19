package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class QushingIntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qshing_info1)

        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            // 추가 설명 페이지로 이동
            startActivity(Intent(this, QushingIntroActivity2::class.java))
            finish()
        }
    }
}
