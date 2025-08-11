# 들어가기 전



시작일자 : 2025.08.11 -&#x20;



본 문서는 Safe QR Scanner 앱의 취약점을 정적파악하는 목적으로 작성되었다.



## 주요 디렉터리 및 파일 구조화&#x20;



Anti\_qshing\_application-master

```
/app/
└ build.gradle.kts 
└ release/
 └ app-release.apk 

/src/
 └ main/
  └ AndroidManifest.xml
  └ /java/com/example/myapplication/
   └ MainActivity.kt
   └ QRScannerActivity.kt
   └ SplashActivity.kt
   └ QshingIntroActivity.kt / QshingIntroActivity2.kt
   └ TutorialActivity.kt / TutorialAdapter.kt / TutorialPage.kt
```



***





