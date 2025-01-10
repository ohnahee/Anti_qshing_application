package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class MainActivity : AppCompatActivity() {
    private lateinit var qrScanLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ActionBar 숨기기
        supportActionBar?.hide()

        // 메인 레이아웃 설정
        setContentView(R.layout.activity_main)

        // QR 코드 스캔 영역 클릭 이벤트 처리
        val qrScanButton = findViewById<LinearLayout>(R.id.qrScanButton)
        qrScanButton.setOnClickListener {
            initiateQrScan()
        }

        // "큐싱(Qushing) 이란?" 카드 클릭 이벤트 처리
        val qushingInfoCard = findViewById<LinearLayout>(R.id.qushingInfoCard)
        qushingInfoCard.setOnClickListener {
            // QushingInfoActivity로 이동
            val intent = Intent(this, QushingInfoActivity::class.java)
            startActivity(intent)
        }

        // QR 코드 스캔 결과 처리
        qrScanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val intentResult: IntentResult? = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
            if (intentResult != null) {
                if (intentResult.contents == null) {
                    Toast.makeText(this, "QR 코드 스캔이 취소되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    handleQrScanResult(intentResult.contents)
                }
            } else {
                Toast.makeText(this, "QR 코드 스캔 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // QR 코드 스캔 초기화
    private fun initiateQrScan() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE) // QR 코드 형식만 허용
        integrator.setPrompt("QR 코드를 스캔하세요") // 스캔 화면의 메시지
        integrator.setCameraId(0) // 기본 카메라 사용
        integrator.setBeepEnabled(true) // 스캔 시 소리
        integrator.setOrientationLocked(true) // 화면 방향 고정
        integrator.setBarcodeImageEnabled(true) // QR 코드 이미지 저장
        qrScanLauncher.launch(integrator.createScanIntent())
    }

    // QR 코드 스캔 결과 처리
    private fun handleQrScanResult(contents: String) {
        Toast.makeText(this, "QR 코드 내용: $contents", Toast.LENGTH_LONG).show()
        // QR 코드 내용을 처리하는 로직 추가
        if (contents.contains("http")) {
            // QR 코드 내용이 URL인 경우
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = android.net.Uri.parse(contents)
            startActivity(intent)
        } else {
            // QR 코드 내용이 일반 텍스트인 경우
            Toast.makeText(this, "텍스트 QR 코드: $contents", Toast.LENGTH_LONG).show()
        }
    }
}