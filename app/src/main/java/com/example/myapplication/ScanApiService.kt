package com.example.myapplication

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// 요청 및 응답 데이터 클래스
data class ScanRequest(val url: String)
data class ScanResponse(val result: String)

// API 서비스 인터페이스
interface ScanApiService {
    @Headers("Content-Type: application/json")
    @POST("/scan") // 엔드포인트 수정
    fun sendScanRequest(@Body request: ScanRequest): Call<ScanResponse>
}