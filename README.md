# QR 피싱(큐싱) 탐지 안드로이드 앱

## 1. 프로젝트 소개

QR 코드의 사용이 대중화되며, 이를 악용한 피싱(큐싱) 사례도 함께 증가하고 있다.  
본 프로젝트는 사용자가 스캔한 QR 코드 또는 입력한 URL에 대해 악성 여부를 판별하는 안드로이드 기반 애플리케이션이다.  
사용자의 보안과 편의성을 함께 고려하여 개발하게 되었다.

---

## 2. 주요 기능

1. 큐싱에 대한 설명 제공 (최초 실행 시)
2. 앱 사용법 안내 제공 (최초 실행 시)
3. 설명 다시 보기 기능 제공 (메인 화면에서 큐싱 버튼 클릭 시)
4. 카메라를 통한 QR 코드 스캔 후 판별 가능 
5. 갤러리에서 QR 코드 이미지 불러와서 판별 가능
6. URL 직접 입력을 통한 악성 여부 판별 기능

## 3. 주요 화면
### 3.1. 큐싱(qshing) 소개 화면
<img src="https://github.com/user-attachments/assets/03b11b4d-2864-469e-b533-275bfeb47f02" width="220"/> <img src= "https://github.com/user-attachments/assets/66807141-e6ac-4eb7-9e81-686493642c01" width="220" height="445">   

### 3.2. 앱 튜토리얼 화면 

<img src= "https://github.com/user-attachments/assets/8b666731-4b22-40aa-8a59-d9488fdc3751" width="220"/> <img src= "https://github.com/user-attachments/assets/e87b448d-a18b-48d9-8ca1-6bb922ac53ab" width="220">  

<img src= "https://github.com/user-attachments/assets/69944a00-6a82-4a74-9a35-3445b2a671c3" width="220"/>
<img src= "https://github.com/user-attachments/assets/8ef1f36b-d3fd-44c2-a9b2-2f616e7ee507" width="220"/>


---

## 4. 기술 스택 및 특징

### 4.1. Android (Kotlin)

-  CameraX 및 ML Kit를 활용하여 QR 코드 스캔 기능을 안정적으로 구현하였다.  
- 직관적인 UI/UX 구성으로 초보 사용자도 앱을 쉽게 사용할 수 있도록 설계하였다.  
- 앱 최초 실행 시 큐싱 개념과 앱 사용법 안내 기능이 포함되어 있으며, 메인 화면에서도 언제든지 설명을 다시 확인할 수 있다.  
- QR 코드 스캔 / 이미지 불러오기 / URL 직접 입력 등 다양한 입력 방식을 지원하여 사용자 편의성을 극대화하였다.  

### 4.2. AI 기반 악성 URL 판별 서버 (Flask + Python)
- Flask 기반의 서버는 URL을 입력받아 악성 여부를 반환한다.
- 약 100만 개의 데이터셋을 기반으로 모델을 학습하여 기존 연구 대비 두 배 수준의 데이터를 활용하였다.
- 기존의 Random Forest 대신 Logistic Regression을 사용하였다.
- PyCaret의 compare_models 알고리즘을 통해 최적의 모델을 선택하였고, train_test_split 을 통해 데이터 분할을 수행하였다.

*기존 논문에서는 DGA, WHOIS 등과 딥러닝을 활용한 사례도 있었지만, 본 프로젝트는 머신러닝 기반 인공지능 모델을 사용함으로써 모델의 해석 가능성과 경량화를 동시에 달성하였다.

---

## 5. 설치 및 실행 방법
아래 GitHub 릴리즈 페이지에서 최신 APK 파일을 다운로드한다.  
👉 [릴리즈 페이지 바로가기](https://github.com/SoftwareCreativeDesign/Anti_qshing_application/releases)

1. Android 기기에서 .apk 파일을 실행하여 설치한다.
2. 설치 중 다음과 같은 설정이 필요할 수 있다.
- 출처를 알 수 없는 앱 허용  
- 경로: 설정 > 보안 > 알 수 없는 출처 허용  

3. 설치가 완료되면 앱을 실행한다. 
- 첫 실행 시 큐싱에 대한 설명과 앱 사용법이 표시된다.  
- 이후 바로 QR 코드 스캔 기능을 사용할 수 있다. 
