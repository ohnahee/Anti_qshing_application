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
        integrator.setCaptureActivity(CustomCaptureActivity::class.java)
        integrator.setOrientationLocked(true) // 화면 회전 잠금
        integrator.setPrompt("QR 코드를 스캔하세요")
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.initiateScan()
    }

    // QR 코드 스캔 결과 처리
    private fun handleQrScanResult(contents: String) {
        Toast.makeText(this, "QR 코드 내용: $contents", Toast.LENGTH_LONG).show()
        if (contents.contains("http")) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = android.net.Uri.parse(contents)
            startActivity(intent)
        } else {
            Toast.makeText(this, "텍스트 QR 코드: $contents", Toast.LENGTH_LONG).show()
        }
    }
}
