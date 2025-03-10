package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
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

    @OptIn(ExperimentalGetImage::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
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
            val url = urlInput.text.toString()
            if (url.isNotEmpty()) {
                sendUrlToServer(url)
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
                                handleQrScanResult(qrText)
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

    private fun handleQrScanResult(contents: String) {
        if (contents.contains("http")) {
            showAlertDialog("QR 코드 내용", contents)
            sendUrlToServer(contents)
        } else {
            showAlertDialog("QR 코드 오류", "QR 코드에서 URL을 감지하지 못했습니다.")
        }
    }

    private fun sendUrlToServer(url: String) {
        val client = OkHttpClient()
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()

        val jsonObject = JSONObject()
        jsonObject.put("url", url)

        val requestBody = jsonObject.toString().toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url("http://hogbal.synology.me:13333/scan")
            .post(requestBody)
            .header("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    showAlertDialog("서버 요청 실패", "서버 요청에 실패했습니다.\n오류: ${e.message}")
                    Log.e("ServerRequest", "서버 요청 실패", e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    val responseText = it.string()
                    runOnUiThread {
                        Log.d("ServerResponse", "서버 응답: $responseText")
                        parseAndShowResult(responseText)
                    }
                }
            }
        })
    }

    private fun parseAndShowResult(responseText: String) {
        try {
            Log.d("JSON_PARSING", "응답 JSON: $responseText")

            val jsonObject = JSONObject(responseText)

            if (!jsonObject.has("result")) {
                showAlertDialog("오류", "서버 응답이 올바르지 않습니다.")
                return
            }

            val result = jsonObject.getString("result")

            val message = when (result) {
                "malicious" -> "⚠️ 악성 URL입니다!"
                "safe" -> "✅ 안전한 URL입니다!"
                "not found url" -> "데이터베이스에 존재하지 않는 URL입니다."
                else -> "알 수 없는 응답: $result"
            }

            showAlertDialog("스캔 결과", message)
        } catch (e: Exception) {
            showAlertDialog("오류", "서버 응답을 해석할 수 없습니다.\n오류: ${e.message}")
            Log.e("ParseError", "JSON 파싱 오류", e)
        }
    }

    private fun showAlertDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)

        val dialog = builder.create()
        dialog.show()
    }
}
