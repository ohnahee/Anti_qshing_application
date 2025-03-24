package com.example.myapplication

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@androidx.camera.core.ExperimentalGetImage
class QRScannerActivity : AppCompatActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var previewView: PreviewView
    private var isQrScanned = false // QR 코드 중복 인식 방지

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanner)

        previewView = findViewById(R.id.viewFinder)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .setTargetResolution(android.util.Size(1280, 720))
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetResolution(android.util.Size(1280, 720))
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (e: Exception) {
                Log.e(TAG, "카메라 바인딩 실패", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val scanner: BarcodeScanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (isQrScanned) {
                    imageProxy.close()
                    return@addOnSuccessListener
                }

                for (barcode in barcodes) {
                    val qrText = barcode.rawValue
                    if (!qrText.isNullOrEmpty()) {
                        isQrScanned = true // ✅ 중복 방지 플래그 설정
                        Log.d(TAG, "QR 코드 인식 성공: $qrText")
                        sendUrlToServer(qrText)
                        break
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "QR 코드 인식 실패", e)
                showAlertDialog("QR 코드 오류", "QR 코드 인식을 실패했습니다.")
            }
            .addOnCompleteListener {
                imageProxy.close()
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
            val jsonObject = JSONObject(responseText)
            val result = jsonObject.getString("result")

            val message = when (result) {
                "malicious" -> "⚠️ 악성 URL입니다!"
                "safe" -> "✅ 안전한 URL입니다!"
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
                restartCamera() // 확인 후 재스캔 가능
            }
            .setCancelable(false)

        val dialog = builder.create()
        dialog.show()
    }

    private fun restartCamera() {
        isQrScanned = false // 팝업 닫히면 다시 스캔 가능하게
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "QRScannerActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
