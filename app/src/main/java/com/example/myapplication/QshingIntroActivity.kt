package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class QshingIntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qshing_info1) // 큐싱 설명 화면 레이아웃

        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            val fromMain = intent.getBooleanExtra("fromMain", false)

            val intent = Intent(this, QshingIntroActivity2::class.java)
            intent.putExtra("fromMain", fromMain) // 다음 액티비티에도 전달
            startActivity(intent)
            finish()
        }
    }
}
