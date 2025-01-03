package com.example.myapplication

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ActionBar 숨기기
        supportActionBar?.hide()

        // 메인 레이아웃 설정
        setContentView(R.layout.activity_main)

        // QR 코드 스캔 영역 클릭 이벤트 처리
        val qrScanButton = findViewById<LinearLayout>(R.id.qrScanButton)
        qrScanButton.setOnClickListener {
            showQrScanFeatureUnavailableToast()
        }
    }

    // QR 코드 스캔 기능이 아직 구현되지 않았음을 알리는 메시지 표시
    private fun showQrScanFeatureUnavailableToast() {
        Toast.makeText(this, "QR 코드 스캔 기능은 아직 구현되지 않았습니다.", Toast.LENGTH_SHORT).show()
    }
}
