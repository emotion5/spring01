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

## 3단계: Entity + DTO 레이어 추가 (다섯 번째 커밋)

### 구조
```
com.dani.simplememo/
├── SimpleMemoApplication.java
├── controller/
│   └── MemoController.java (DTO 사용)
├── service/
│   └── MemoService.java (Entity 사용)
├── entity/                    ✨ 신규
│   └── Memo.java
└── dto/                       ✨ 신규
    ├── MemoRequest.java
    └── MemoResponse.java
```

### 변경 사항

#### 1. Entity 레이어 생성
**Memo.java** - 도메인 모델 (비즈니스 데이터 구조)
- `Long id`: 고유 식별자
- `String content`: 메모 내용
- `LocalDateTime createdAt, updatedAt`: 생성/수정 시간
- JPA 어노테이션 없이 순수 Java 클래스로 시작 (나중에 JPA 전환 용이)

#### 2. DTO 레이어 생성
**MemoRequest.java** - API 요청 데이터
- 클라이언트가 보내는 데이터 구조
- `String content`만 포함

**MemoResponse.java** - API 응답 데이터
- 클라이언트에게 보내는 데이터 구조
- `id`, `content`, `createdAt`, `updatedAt` 포함
- static 팩토리 메서드로 Entity → DTO 변환

#### 3. MemoService 리팩토링
- `List<String>` → `List<Memo>` 변경
- AtomicLong으로 ID 자동 생성
- 타임스탬프 자동 관리

#### 4. MemoController 리팩토링
- `@RequestBody MemoRequest` 사용
- `MemoResponse` 반환
- Entity ↔ DTO 변환 처리

### 핵심 개념

#### Entity vs DTO
- **Entity**: 비즈니스 로직과 데이터를 가진 도메인 객체
- **DTO**: 계층 간 데이터 전송용 객체 (Data Transfer Object)

#### 왜 분리하는가?
1. **관심사 분리**: API 구조 변경이 도메인 모델에 영향 X
2. **보안**: 민감한 정보를 선택적으로 노출
3. **유연성**: API 응답과 도메인 모델을 독립적으로 설계
4. **유지보수성**: 각 계층의 변경이 다른 계층에 미치는 영향 최소화

#### entity vs domain 패키지 네이밍
- **entity/**: JPA Entity를 명확히 표현 (이번 프로젝트 선택)
- **domain/**: DDD 철학, 더 포괄적 개념 (Entity + Value Object + Domain Service)
- 작은 프로젝트에서는 `entity/`가 더 직관적

### 장점
- String에서 구조화된 객체로 변경 (타입 안정성)
- ID와 타임스탬프 자동 관리
- API 계약(Request/Response)이 명확해짐
- 나중에 H2/JPA 추가 시 최소한의 변경으로 전환 가능

### 데이터 흐름
```
Client → MemoRequest → Controller → Memo(Entity) → Service
         (JSON)                      (비즈니스 객체)

Service → Memo(Entity) → Controller → MemoResponse → Client
          (비즈니스 객체)              (JSON)
```

---

## 4단계: Repository 레이어 추가 (여섯 번째 커밋)

### 구조
```
com.dani.simplememo/
├── SimpleMemoApplication.java
├── controller/
│   └── MemoController.java
├── service/
│   └── MemoService.java (Repository 사용)
├── entity/
│   └── Memo.java
├── dto/
│   ├── MemoRequest.java
│   └── MemoResponse.java
└── repository/                    ✨ 신규
    ├── MemoRepository.java         ✨ 신규 (인터페이스)
    └── InMemoryMemoRepository.java ✨ 신규 (구현체)
```

### 변경 사항

#### 1. Repository 인터페이스 생성
**MemoRepository.java** - 데이터 접근 계약 정의
- `Memo save(Memo memo)`: 저장/수정
- `List<Memo> findAll()`: 전체 조회
- `Optional<Memo> findById(Long id)`: ID로 조회
- `boolean deleteById(Long id)`: ID로 삭제

#### 2. Repository 구현체 생성
**InMemoryMemoRepository.java** - ArrayList 기반 구현
- `@Repository` 어노테이션으로 Spring이 관리
- Service에서 옮겨온 데이터 저장 로직
- `List<Memo> memos` + `AtomicLong idCounter` 관리

#### 3. MemoService 리팩토링
**Before (데이터 접근 로직 포함):**
- ArrayList 직접 관리
- ID 생성 로직 포함
- 데이터 조회 로직 포함

**After (비즈니스 로직만):**
- Repository 의존성 주입
- 모든 데이터 접근을 Repository로 위임
- 비즈니스 로직에만 집중

### 핵심 개념

#### Repository 패턴이란?
데이터 저장소(ArrayList, Database 등)에 대한 접근을 추상화하는 패턴

#### 계층별 책임 분리
```
Controller (HTTP 처리)
    ↓
Service (비즈니스 로직)
    ↓
Repository (데이터 접근)
    ↓
Data Store (ArrayList/Database)
```

#### 왜 분리하는가?
1. **단일 책임 원칙**: Service는 비즈니스 로직만, Repository는 데이터 접근만
2. **변경 용이성**: ArrayList → H2로 변경 시 Repository만 교체
3. **테스트 용이성**: Service 테스트 시 가짜 Repository 사용 가능
4. **재사용성**: 다른 Service에서도 같은 Repository 사용 가능

### 장점

#### 1. Service 코드 간결화
**Before (18줄):**
```java
@Service
public class MemoService {
    private final List<Memo> memos = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public Memo addMemo(String content) {
        Memo memo = new Memo(
                idCounter.getAndIncrement(),
                content
        );
        memos.add(memo);
        return memo;
    }
}
```

**After (5줄):**
```java
@Service
@RequiredArgsConstructor
public class MemoService {
    private final MemoRepository memoRepository;

    public Memo addMemo(String content) {
        Memo memo = new Memo(null, content);
        return memoRepository.save(memo);
    }
}
```

#### 2. 저장소 교체가 쉬움
```java
// 현재: ArrayList 사용
public class InMemoryMemoRepository implements MemoRepository { ... }

// 나중에: JPA 사용 (Service 코드 변경 없음!)
public interface MemoRepository extends JpaRepository<Memo, Long> { ... }
```

#### 3. 관심사 분리
- **Service**: "메모를 추가한다" (비즈니스 규칙)
- **Repository**: "ArrayList에 저장한다" (기술적 세부사항)

### 다음 단계를 위한 준비
- Repository 인터페이스는 그대로 유지
- InMemoryMemoRepository를 JpaRepository로 교체만 하면 됨
- Service 코드는 전혀 수정할 필요 없음

---

## 다음 단계 예정
- **5단계: H2 Database + JPA 적용** (영속성 추가)
- 예외 처리
- Validation
- 등등...
