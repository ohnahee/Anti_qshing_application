package com.example.myapplication

import android.os.Bundle
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class CustomCaptureActivity : CaptureActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 스캐너 뷰의 크기 자동 조정
        val barcodeScannerView = findViewById<DecoratedBarcodeView>(com.google.zxing.client.android.R.id.zxing_barcode_scanner)
        barcodeScannerView.statusView.visibility = android.view.View.GONE
        barcodeScannerView.barcodeView.cameraSettings.isAutoFocusEnabled = true
    }
}
