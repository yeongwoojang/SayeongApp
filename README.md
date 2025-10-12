## 안드로이드 앱 아키텍처 활용한 앱 구현 (Now In Android 참조)

### 프로젝트 구조
- app
- build-logic (android 모듈)
- core
  - data (android 모듈)
  - designsystem (android 모듈) 
  - domain (순수 kotlin 모듈)
  - model (순수 kotlin 모듈)
  - network (android 모듈)
- feature
  - home (android 모듈)
  - player (android 모듈)


### 활용 기술
* **Android**: MVVM, Compose, Xml, Hilt, Retrofit, OkHttp3, Media3
* **backend**: Node.js

### 구현 기능
* 오디오 재생 시 무음구간 자동 스킵
* 오디오 배속 재생
* videoPlayer CustomUI (고도화 예정)

### 앱 시연 영상

[앱시연1.webm](https://github.com/user-attachments/assets/8b4dd8b8-0849-463a-a4e1-bacbf60a6d6c)


[앱시연2.webm](https://github.com/user-attachments/assets/2a7a901d-61b1-4f0e-bab6-4fda3531e8d1)


