## 손짓의 순간
<p align="center">
  <img src="https://github.com/user-attachments/assets/a3f1bf93-2e05-4f9f-a4f0-8d2dcb813488" alt="손짓의 순간"/>
</p> 
<div align="center">
 농인을 위한 수화 번역 앱 손짓의 순간 안드로이드 파트
</div>

## 목차
  - [개요](#개요)
  - [내용](#내용)
  - [화면](#화면)
  - [개발 환경](#개발환경)
  - [인프라 구조](#인프라구조)
  - [기술 스택](#기술스택)

## 개요
- 프로젝트 이름: 손짓의 순간 (2024 한성대학교 캡스톤 디자인 우수상 수상작)
- 프로젝트 지속기간: 2024.01.10 ~ 05/30
- 개발 엔진 및 언어: Android Studio, Kotlin
- 멤버: 박지원, 김성민, 신정인

## 내용
- 수화 번역 : Yolo v5로 만든 수화번역 모델을 PyTorch Lightning로 경량화하여 CameraX에 탑재하여 실시간 수화 번역이 가능하다.

- Speech Flow : 동영상을 텍스트로 변환하여 전체자막을 한번에 확인할 수 있다.

- 수어 통역센터 길찾기 : 위치정보를 파악하여 최단거리에 있는 수어통역센터로 길찾기 기능을 수행할 수 있고 일반 지도 길찾기 기능도 사용 가능하다.

- 소음 감지 : 소음을 감지하여 일정 데시벨 이상의 소리가 들리면 핸드폰 진동이 울린다.

-  Speech to Text : 카메라 화면에서 버튼을 누르고 말을하면 말한 내용을 텍스트로 표시한다.

- 음성 분류, 단어 감지 : 주변 소리를 분석해서 어떤 음성인지 분류하고 학습된 단어를 수치로 보여준다.

- 수어 사전 : 단어, 자음, 모음, 숫자로 이루어진 사전이 있다.

- 스트리밍 : 사용자가  방을 생성하면 다른 사용자가 해당 방 id를 입력하고 들어가서 스트리밍에 참여할 수 있다.

## 개발 환경
<p align="center">
  <img src="https://github.com/user-attachments/assets/8bbe30c4-3b40-4dbc-ba9b-49e4f70927fd" alt="손짓의 순간"/>
</p>

## 인프라 구조
<p align="center">
  <img src="https://github.com/user-attachments/assets/accb5790-2ea5-42a3-8cc8-1e01e543b268" alt="손짓의 순간"/>
</p>

## 화면
<p align="center">
  <img src="https://github.com/user-attachments/assets/45744a90-e30a-4e74-a18b-40986066f3e5" alt="손짓의 순간"/>
</p>

## 기술스택

### **🤖** 안드로이드
| **Category** | **TechStack** |
| --- | --- |
| Architecture | Clean Architecture, MVVM |
| DI | Hilt |
| Network | Retrofit, OkHttp, Gson |
| Service | Service |
| Asynchronous | Coroutines, Flow |
| Jetpack | DataBinding, Navigation, DataStore, LiveData, CameraX |
|   AI  | PytorchMobile |
| Image | Glide |
