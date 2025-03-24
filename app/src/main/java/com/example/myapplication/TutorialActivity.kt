package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TutorialActivity : AppCompatActivity() {

    private lateinit var tutorialImage: ImageView
    private lateinit var tutorialText: TextView

    private val tutorialPages = listOf(
        TutorialPage(R.drawable.main1, "큐싱(Qshing) 예방 솔루션 버튼을 클릭하면\n큐싱 설명과 앱 튜토리얼 화면을\n다시 볼 수 있습니다"),
        TutorialPage(R.drawable.main2, "카메라로 QR code 스캔이 가능합니다"),
        TutorialPage(R.drawable.main3, "갤러리에서 QR code 이미지를 선택하여\n검사할 수 있습니다"),
        TutorialPage(R.drawable.main4, "URL을 입력하여 검사도 가능합니다")
    )

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        tutorialImage = findViewById(R.id.tutorialImage)
        tutorialText = findViewById(R.id.tutorialText)

        showCurrentPage()

        // 화면 클릭 시 다음 단계로 이동 또는 종료
        tutorialImage.setOnClickListener {
            currentIndex++
            if (currentIndex < tutorialPages.size) {
                showCurrentPage()
            } else {
                // 마지막 페이지 다음 클릭 → MainActivity 이동
                getSharedPreferences("app_prefs", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply()

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun showCurrentPage() {
        val page = tutorialPages[currentIndex]
        tutorialImage.setImageResource(page.imageResId)
        tutorialText.text = page.description
    }
}
