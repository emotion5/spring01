# Spring Boot 학습 기록

## 1단계: Controller 구현 (첫 번째 커밋)

### 구조
```
com.dani.simplememo/
├── SimpleMemoApplication.java
└── controller/
    └── MemoController.java
```

### 특징
- **Controller가 모든 것을 처리**
- HTTP 요청 처리 + 비즈니스 로직 + 데이터 저장을 모두 Controller에서 담당
- `List<String> memos`를 Controller가 직접 관리

### 코드 예시
```java
@RestController
public class MemoController {
    private List<String> memos = new ArrayList<>();  // 직접 데이터 관리

    @PostMapping("/memo")
    public String addMemo(@RequestBody String content) {
        memos.add(content);  // 직접 로직 처리
        return "메모가 추가되었습니다: " + content;
    }
}
```

### 문제점
- Controller가 너무 많은 책임을 가짐 (단일 책임 원칙 위반)
- 비즈니스 로직이 Controller에 섞여 있어 재사용 어려움

---

## 2단계: Service 레이어 분리 (두 번째 커밋)

### 구조
```
com.dani.simplememo/
├── SimpleMemoApplication.java
├── controller/
│   └── MemoController.java (HTTP 요청/응답만 처리)
└── service/
    └── MemoService.java (비즈니스 로직 처리)
```

### 변경 사항

#### 1. MemoService.java 생성
- `@Service` 어노테이션으로 Spring이 관리
- 비즈니스 로직(메모 추가, 조회, 수정, 삭제)을 담당
- 데이터 저장소(`List<String> memos`) 관리

```java
@Service
public class MemoService {
    private List<String> memos = new ArrayList<>();

    public String addMemo(String content) {
        memos.add(content);
        return "메모가 추가되었습니다: " + content;
    }
    // ...
}
```

#### 2. MemoController.java 수정
- Service를 **의존성 주입(Dependency Injection)** 받음
- HTTP 요청만 받아서 Service에 위임
- 비즈니스 로직 제거

```java
@RestController
public class MemoController {
    private final MemoService memoService;

    // 생성자 주입
    public MemoController(MemoService memoService) {
        this.memoService = memoService;
    }

    @PostMapping("/memo")
    public String addMemo(@RequestBody String content) {
        return memoService.addMemo(content);  // Service에 위임
    }
}
```

### 핵심 개념

#### 1. 계층 분리 (Layered Architecture)
```
Controller (HTTP 처리)
    ↓
Service (비즈니스 로직)
```

#### 2. 의존성 주입 (Dependency Injection)
- Spring이 `MemoService` 객체를 자동으로 생성
- `MemoController` 생성 시 자동으로 주입
- `@Service`와 생성자를 통해 자동으로 이루어짐

#### 3. 단일 책임 원칙 (SRP)
- **Controller**: HTTP 요청/응답만 담당
- **Service**: 비즈니스 로직만 담당

### 장점
- 코드의 역할이 명확해짐
- 비즈니스 로직 재사용 가능
- 테스트하기 쉬워짐
- Spring Boot의 표준 구조를 따름

---

## 다음 단계 예정
- Repository 레이어 추가 (데이터베이스 연동)
- DTO(Data Transfer Object) 사용
- 예외 처리
- 등등...
