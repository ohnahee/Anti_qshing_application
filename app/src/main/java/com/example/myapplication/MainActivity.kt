package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ExperimentalGetImage
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private val client = OkHttpClient()
    private val serverUrl = "https://86d4-14-56-209-110.ngrok-free.app/predict"

    @OptIn(ExperimentalGetImage::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // 액션 바 숨기기
        setContentView(R.layout.activity_main)

        val qrScanButton = findViewById<LinearLayout>(R.id.qrScanButton)
        val selectImageButton = findViewById<Button>(R.id.btn_select_qr_image)
        val sendUrlButton = findViewById<Button>(R.id.btnSendUrl)
        val urlInput = findViewById<EditText>(R.id.urlInput)

        qrScanButton.setOnClickListener {
            val intent = Intent(this, QRScannerActivity::class.java)
            startActivity(intent)
        }

        selectImageButton.setOnClickListener { pickQRCodeImage() }

        sendUrlButton.setOnClickListener {
            val url = urlInput.text.toString().trim()
            if (url.isNotEmpty()) {
                sendUrlToServer(url) // 직접 입력한 URL 서버로 전송
            } else {
                showAlertDialog("알림", "URL을 입력하세요.")
            }
        }

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                result.data!!.data?.let { uri ->
                    Log.d("ImageURI", "선택한 이미지 URI: $uri")

                    val inputStream: InputStream? = contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)

                    if (bitmap != null) {
                        decodeQRCode(bitmap) { qrText ->
                            if (!qrText.isNullOrEmpty()) {
                                sendUrlToServer(qrText) // 판별 후에만 팝업 표시
                            } else {
                                showAlertDialog("QR 코드 인식 실패", "QR 코드를 인식하지 못했습니다.")
                            }
                        }
                    } else {
                        showAlertDialog("오류", "이미지를 불러오지 못했습니다.")
                    }
                }
            }
        }
    }

    private fun pickQRCodeImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun decodeQRCode(bitmap: android.graphics.Bitmap, onResult: (String?) -> Unit) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val scanner: BarcodeScanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val qrText = barcode.rawValue
                    if (!qrText.isNullOrEmpty()) {
                        Log.d("QRCode", "QR 코드 해독 성공: $qrText")
                        onResult(qrText)
                        return@addOnSuccessListener
                    }
                }
                Log.e("QRCode", "QR 코드 감지 실패")
                onResult(null)
            }
            .addOnFailureListener { e ->
                Log.e("QRCode", "QR 코드 해독 실패: ${e.message}")
                onResult(null)
            }
    }

    private fun sendUrlToServer(url: String) {
        val jsonObject = JSONObject().put("url", url)
        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(serverUrl)
            .post(requestBody)
            .header("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    showAlertDialog("서버 요청 실패", "서버 요청에 실패했습니다.\n오류: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            showAlertDialog("오류", "서버 응답 오류: ${response.code}")
                        }
                        return
                    }

                    val responseData = response.body?.string()
                    runOnUiThread {
                        parseAndShowResult(responseData ?: "{}") // 서버 판별 후에만 팝업 띄움
                    }
                }
            }
        })
    }

    private fun parseAndShowResult(responseText: String) {
        try {
            val jsonObject = JSONObject(responseText)
            val result = jsonObject.optString("Result", "결과 없음")
            val safeProb = jsonObject.optJSONObject("Probabilities")?.optString("Safe", "0%") ?: "0%"
            val maliciousProb = jsonObject.optJSONObject("Probabilities")?.optString("Malicious", "0%") ?: "0%"

            val message = "결과: $result\n안전 확률: $safeProb\n위험 확률: $maliciousProb"

            showAlertDialog("스캔 결과", message)
        } catch (e: Exception) {
            showAlertDialog("오류", "서버 응답을 해석할 수 없습니다.\n오류: ${e.message}")
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        runOnUiThread {
            val titleView = TextView(this).apply {
                text = title
                textSize = 20f
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
                setPadding(20, 20, 20, 20)
            }

            val messageView = TextView(this).apply {
                text = message
                textSize = 18f
                gravity = Gravity.CENTER
                setPadding(40, 40, 40, 40)
            }

            val layout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                addView(titleView)
                addView(messageView)
            }

            AlertDialog.Builder(this)
                .setView(layout)
                .setPositiveButton("확인", null)
                .show()
        }
    }
}
