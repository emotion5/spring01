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

## 2-1단계: Lombok 적용 (세 번째 커밋)

### 변경 사항

#### 1. pom.xml에 Lombok 의존성 추가
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

#### 2. MemoController 리팩토링

**Before (생성자 직접 작성):**
```java
@RestController
public class MemoController {
    private final MemoService memoService;

    // 생성자 직접 작성
    public MemoController(MemoService memoService) {
        this.memoService = memoService;  // this 사용
    }
}
```

**After (Lombok 적용):**
```java
@RestController
@RequiredArgsConstructor  // Lombok이 생성자 자동 생성
public class MemoController {
    // Service 의존성 주입 (생성자 자동 생성)
    private final MemoService memoService;

    // 생성자 코드 삭제됨!
}
```

### 핵심 개념

#### `@RequiredArgsConstructor`
- `final` 필드를 파라미터로 받는 생성자를 자동 생성
- `this` 키워드 불필요
- 코드 간결화 (보일러플레이트 제거)

### 효과
- 생성자 코드 3줄 제거
- `this` 키워드 제거
- 코드 더 깔끔하고 간결해짐
- 실무 표준 패턴 적용

### Lombok이란?
- 반복적인 코드를 자동으로 생성해주는 라이브러리
- Getter, Setter, 생성자, toString 등을 어노테이션으로 자동 생성
- 실무 사용률: ~90%

---

## 2-2단계: @RequestMapping으로 공통 경로 설정 (네 번째 커밋)

### 변경 사항

#### MemoController에 공통 경로 적용

**Before (개별 경로):**
```java
@RestController
@RequiredArgsConstructor
public class MemoController {

    @PostMapping("/memo")      // /memo
    @GetMapping("/memos")      // /memos
    @PutMapping("/memo/{index}")   // /memo/{index}
    @DeleteMapping("/memo/{index}") // /memo/{index}
}
```

**After (@RequestMapping 적용):**
```java
@RestController
@RequestMapping("/api/v1/memos")  // 공통 경로
@RequiredArgsConstructor
public class MemoController {

    @PostMapping              // /api/v1/memos
    @GetMapping               // /api/v1/memos
    @PutMapping("/{index}")   // /api/v1/memos/{index}
    @DeleteMapping("/{index}") // /api/v1/memos/{index}
}
```

### 핵심 개념

#### `@RequestMapping`
- 클래스 레벨에 적용하여 **공통 경로** 설정
- 모든 메서드의 경로 앞에 자동으로 붙음
- API 버전 관리, 리소스 그룹화에 유용

### 장점
1. **경로 일관성**: 모든 메모 관련 API가 `/api/v1/memos` 아래에 통일
2. **유지보수성**: 공통 경로 변경 시 한 곳만 수정
3. **API 버전 관리**: `/api/v1`, `/api/v2` 등으로 버전 구분 가능
4. **RESTful 규칙 준수**: 리소스 중심 URL 구조

### 변경된 API 엔드포인트

| Before | After | 설명 |
|--------|-------|------|
| `POST /memo` | `POST /api/v1/memos` | 메모 추가 |
| `GET /memos` | `GET /api/v1/memos` | 전체 조회 |
| `PUT /memo/{index}` | `PUT /api/v1/memos/{index}` | 메모 수정 |
| `DELETE /memo/{index}` | `DELETE /api/v1/memos/{index}` | 메모 삭제 |

### 실무 패턴
- `/api`: API임을 명시
- `/v1`: 버전 정보
- `/memos`: 리소스명 (복수형)

---

## 다음 단계 예정
- **3단계: Repository + Entity 레이어 추가** (데이터 계층 분리)
- DTO(Data Transfer Object) 사용
- 예외 처리
- 등등...
