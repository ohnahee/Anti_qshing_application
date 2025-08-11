# 정적 분석 방법론



## 1. 개요

정적 분석(Static Analysis)은 애플리케이션을 실행하지 않고, 배포 파일(APK)·소스 코드·바이너리를 직접 분석하여 동작 방식과 잠재적인 보안 취약점을 식별하는 기법이다.\


본 문서에서는 MobSF(Mobile Security Framework) 도구를 활용하고, 사전에 정의된 보안 취약점 점검 체크리스트를 기반으로 안드로이드 애플리케이션의 보안성을 검증하는 정적 분석 방법론을 서술하고자 한다.



***



## 2. 분석 도구&#x20;

### 2.1. MobSF (Mobile Security Framework)

* 형태 : 오픈소스 기반, 웹 UI 및 API 제공
* 주요 분석 범위
  * Manifest 및 메타데이터 분석
  * 코드 스캔(Java, Kotlin, Smali)
  * 포함 라이브러리 및 서명 정보 분석
  * 위험 권한, 네트워크 설정, 하드코딩된 민감 정보(API Key, 토큰 등) 탐지
* 장점
  * APK 업로드만으로 자동 정적 분석 수행
  * 취약점을 위험도(Critical / High / Medium / Low)로 분류
  * 분석 결과를 PDF, HTML 등 다양한 형식으로 보고서 생성



***

## 3. 체크리스트&#x20;

### 3.1. OWASP MASVS / MSTG

**MASVS**(Mobile Application Security Verification Standard) 모바일 앱 보안 검증 표준

* V1: 아키텍처, 권한 관리
* V2: 데이터 저장 보안
* V3: 암호화
* V4: 인증 및 세션 관리
* V5: 네트워크 통신 보안
* V6: 플랫폼 상호작용 (WebView, IPC 등)
* V7: 코드 품질, 난독화
* V8: 빌드 설정 및 배포 보안



### 3.2. MobSF Rule Set

MobSF는 내부적으로 **Manifest Rules + Code Scan Rules + Network Rules**를 가지고 있다.

* Manifest Rules : 위험 권한, exported 컴포넌트, allowBackup, debuggable 등
* Code Scan Rules : 하드코딩 키, HTTP 사용, 약한 암호화 알고리즘
* Network Rules : SSL Pinning 미적용, HTTPS 강제 여부



### 3.3. CWE 취약점 분류

CWE(Common Weakness Enumeration) : 소프트웨어 보안 취약점의 유형을 분류하고 정리한 표준 목록



* CWE-312 : 민감 정보의 평문 저장
* CWE-295 : 부적절한 인증서 검증
* CWE-798 : 하드코딩된 자격 증명
* CWE-89 : SQL Injection 가능성
* CWE-749 : Exported 컴포넌트 취약점
* CWE-93: 코드 주입 취약점



3가지 레퍼런스를 기반으로 체크리스트 구성 후 점검



