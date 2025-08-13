---
description: OWASP MASVS + MobSF 규칙 + CWE 분류
---

# 체크 리스트

## 1. 앱 구성 및 권한 (MASVS V1 / CWE-276, CWE-732)

* [ ] 불필요한 권한 선언 여부\
  예: `READ_CONTACTS`, `ACCESS_FINE_LOCATION` 등 사용하지 않는 권한
* [ ] `android:exported="true"` 설정 여부 (Activity, Service, BroadcastReceiver)
* [ ] `allowBackup="true"` 여부 (데이터 백업을 통한 유출 가능성)
* [ ] `android:debuggable="true"` 여부 (디버그 빌드 배포 금지)

***

## 2. 데이터 보안 (MASVS V2 / CWE-312, CWE-922)

* [ ] QR 스캔 결과(문자열, URL, 연락처 등) 평문 저장 여부
* [ ] 민감 데이터(API Key, 토큰, 암호) 하드코딩 여부
* [ ] SQLite/SharedPreferences 암호화 적용 여부
* [ ] 스캔 결과를 다른 앱과 공유 시 MIME 타입·데이터 검증 수행 여부

***

## 3. 네트워크 보안 (MASVS V5 / CWE-295, CWE-319)

* [ ] HTTP 통신 사용 여부 (HTTPS 강제 필요)
* [ ] SSL Pinning 적용 여부 (OkHttp CertificatePinner 등)
* [ ] 자체 서명 인증서 사용 여부
* [ ] 리디렉션 처리 시 최종 도메인 검증 여부

***

## 4. QR 코드 처리 로직 (MASVS V6 / CWE-601, CWE-94)

* [ ] 허용 스킴 화이트리스트 적용 (`https`, `tel`, `sms`, `mailto`, `geo`, `wifi` 등)
* [ ] 위험 스킴 차단 (`intent://`, `javascript:`, `data:` 등)
* [ ] URL 인코딩/디코딩 및 정규화 처리 여부 (IDN, 퍼니코드 공격 방지)
* [ ] QR 코드 내용 실행 전 사용자 확인 단계 존재 여부

***

## 5. WebView 보안 (MASVS V6 / CWE-749, CWE-829)

* [ ] `addJavascriptInterface` 사용 여부 및 안전성
* [ ] `setAllowFileAccess(true)` 사용 여부
* [ ] `WebView.setWebContentsDebuggingEnabled(false)` 설정 여부
* [ ] HTTPS 전송 및 인증서 검증 여부

***

## 6. 로그·디버깅 (MASVS V7 / CWE-532)

* [ ] Logcat에 민감 정보 출력 여부
* [ ] 배포 버전에 Debug/Verbose 로그 존재 여부

***

## 7. 네이티브 라이브러리 보안 (MASVS V8 / CWE-119, CWE-190)

* [ ] .so 라이브러리에 Stack Canary, NX, PIE, RELRO 적용 여부
* [ ] JNI 호출 시 입력 검증 여부



