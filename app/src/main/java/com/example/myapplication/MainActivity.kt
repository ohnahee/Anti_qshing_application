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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var qrScanLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        val qrScanButton = findViewById<LinearLayout>(R.id.qrScanButton)
        qrScanButton.setOnClickListener {
            initiateQrScan()
        }

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

    private fun initiateQrScan() {
        val integrator = IntentIntegrator(this)
        integrator.setCaptureActivity(CustomCaptureActivity::class.java)
        integrator.setOrientationLocked(true)
        integrator.setPrompt("QR 코드를 스캔하세요")
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.initiateScan()
    }

    private fun handleQrScanResult(contents: String) {
        Toast.makeText(this, "QR 코드 내용: $contents", Toast.LENGTH_LONG).show()
        sendUrlToServer(contents)
    }

    private fun sendUrlToServer(url: String) {
        val request = ScanRequest(url)

        ApiClient.instance.sendScanRequest(request)
            .enqueue(object : Callback<ScanResponse> {
                override fun onResponse(call: Call<ScanResponse>, response: Response<ScanResponse>) {
                    if (response.isSuccessful) {
                        try {
                            val result = response.body()?.result ?: "unknown"
                            when (result) {
                                "malicious" -> showToast("악성 URL입니다!")
                                "safe" -> showToast("정상 URL입니다.")
                                "not found url" -> showToast("데이터베이스에 없는 URL입니다.")
                                else -> showToast("알 수 없는 결과: $result")
                            }
                        } catch (e: Exception) {
                            showToast("데이터 처리 오류: ${e.message}")
                        }
                    } else {
                        showToast("서버 오류 발생: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ScanResponse>, t: Throwable) {
                    showToast("서버 연결 실패: ${t.message}")
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
