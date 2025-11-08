# Lombok 학습 정리

## Lombok이란?

**Boilerplate Code(반복 코드)를 자동으로 생성해주는 라이브러리**

- Java의 장황한(verbose) 문법을 간결하게 만들어줌
- 컴파일 시점에 코드 자동 생성
- 실무 사용률: ~90%

---

## Lombok이 해결하는 문제

### 문제: Java는 반복 코드가 너무 많다

```java
// Lombok 없이 - 50줄 이상
public class Memo {
    private Long id;
    private String content;
    private String author;

    // 기본 생성자
    public Memo() {}

    // 전체 생성자
    public Memo(Long id, String content, String author) {
        this.id = id;
        this.content = content;
        this.author = author;
    }

    // Getter
    public Long getId() { return id; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }

    // Setter
    public void setId(Long id) { this.id = id; }
    public void setContent(String content) { this.content = content; }
    public void setAuthor(String author) { this.author = author; }

    // toString
    @Override
    public String toString() {
        return "Memo{" +
            "id=" + id +
            ", content='" + content + '\'' +
            ", author='" + author + '\'' +
            '}';
    }

    // equals, hashCode 생략... (추가 20줄)
}
```

### 해결: Lombok 사용 - 5줄!

```java
@Data
public class Memo {
    private Long id;
    private String content;
    private String author;
}
// Getter, Setter, toString, equals, hashCode 모두 자동 생성!
```

---

## 주요 어노테이션

### 1. `@Getter` / `@Setter`

```java
// Before
public class Memo {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

// After
@Getter
@Setter
public class Memo {
    private String content;
}
```

---

### 2. `@RequiredArgsConstructor` ⭐ (가장 많이 사용)

**`final` 필드만 받는 생성자 자동 생성**

```java
// Before - this 키워드 사용
@Service
public class MemoService {
    private final MemoRepository memoRepository;
    private final UserService userService;

    public MemoService(MemoRepository memoRepository, UserService userService) {
        this.memoRepository = memoRepository;
        this.userService = userService;
    }
}

// After - this 불필요
@Service
@RequiredArgsConstructor
public class MemoService {
    private final MemoRepository memoRepository;
    private final UserService userService;

    // 생성자 자동 생성!
}
```

**실무에서 가장 많이 사용하는 패턴!**

---

### 3. `@NoArgsConstructor` / `@AllArgsConstructor`

```java
@NoArgsConstructor      // 기본 생성자 (파라미터 없음)
@AllArgsConstructor     // 모든 필드를 받는 생성자
public class Memo {
    private Long id;
    private String content;
}

// 자동 생성:
// public Memo() {}
// public Memo(Long id, String content) { ... }
```

---

### 4. `@ToString`

```java
// Before
@Override
public String toString() {
    return "Memo{" +
        "id=" + id +
        ", content='" + content + '\'' +
        '}';
}

// After
@ToString
public class Memo {
    private Long id;
    private String content;
}
```

---

### 5. `@EqualsAndHashCode`

```java
// Before - 20줄 이상의 코드
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Memo memo = (Memo) o;
    return Objects.equals(id, memo.id) &&
           Objects.equals(content, memo.content);
}

@Override
public int hashCode() {
    return Objects.hash(id, content);
}

// After
@EqualsAndHashCode
public class Memo {
    private Long id;
    private String content;
}
```

---

### 6. `@Data` ⭐ (올인원)

**가장 편리하지만 주의해서 사용**

```java
@Data
public class MemoDto {
    private Long id;
    private String content;
}
```

**`@Data`가 포함하는 것:**
- `@Getter`
- `@Setter`
- `@ToString`
- `@EqualsAndHashCode`
- `@RequiredArgsConstructor`

**주의사항:**
- Entity에는 사용하지 말 것 (양방향 연관관계 시 무한루프)
- DTO에만 사용 권장

---

### 7. `@Slf4j` (로깅)

```java
// Before
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MemoService {
    private static final Logger log = LoggerFactory.getLogger(MemoService.class);

    public void addMemo(String content) {
        log.info("Adding memo: {}", content);
    }
}

// After
@Slf4j
@Service
public class MemoService {

    public void addMemo(String content) {
        log.info("Adding memo: {}", content);  // log 바로 사용!
    }
}
```

---

### 8. `@Builder` (빌더 패턴)

```java
@Builder
public class Memo {
    private Long id;
    private String content;
    private String author;
}

// 사용
Memo memo = Memo.builder()
    .id(1L)
    .content("메모 내용")
    .author("작성자")
    .build();
```

**장점:** 가독성 좋고, 선택적으로 필드 설정 가능

---

## 실무 패턴

### Controller
```java
@RestController
@RequiredArgsConstructor  // 생성자 주입
public class MemoController {
    private final MemoService memoService;
}
```

### Service
```java
@Service
@RequiredArgsConstructor  // 생성자 주입
@Slf4j                    // 로깅
public class MemoService {
    private final MemoRepository memoRepository;

    public Memo addMemo(MemoRequest request) {
        log.info("Adding memo: {}", request);
        return memoRepository.save(new Memo(request));
    }
}
```

### DTO (Data Transfer Object)
```java
@Data  // 모든 기능
public class MemoRequest {
    private String content;
    private String author;
}

@Getter  // Setter 없이 읽기 전용
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemoResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
}
```

### Entity (JPA)
```java
@Entity
@Getter              // ⚠️ @Data 사용 금지!
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Memo {
    @Id @GeneratedValue
    private Long id;

    private String content;
    private String author;
}
```

**Entity에 `@Data` 사용 금지 이유:**
- `@ToString`, `@EqualsAndHashCode`가 연관관계에서 무한루프 유발
- `@Setter`로 인한 의도치 않은 변경 가능

---

## Lombok 설치

### 1. Maven (pom.xml)
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

### 2. Gradle (build.gradle)
```gradle
dependencies {
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
}
```

### 3. IDE 플러그인
- **IntelliJ IDEA**: 기본 내장 (별도 설치 불필요)
- **Eclipse**: Lombok 설치 필요
- **VS Code**: Extension 설치 필요

---

## 장점

1. **코드 간결화**: 50% 이상 코드 감소
2. **가독성 향상**: 핵심 로직에 집중 가능
3. **생산성 증가**: 반복 작업 제거
4. **유지보수 쉬움**: 필드만 수정하면 자동 반영
5. **실수 방지**: Getter/Setter 오타 방지

---

## 단점

1. **IDE 의존성**: 플러그인 필요
2. **디버깅 어려움**: 자동 생성된 코드는 직접 볼 수 없음
3. **학습 곡선**: 어노테이션 동작 이해 필요
4. **과도한 사용**: `@Data`를 Entity에 사용하면 문제 발생

---

## 사용 가이드라인

### ✅ 권장

| 상황 | 어노테이션 |
|------|-----------|
| Service, Controller | `@RequiredArgsConstructor` |
| DTO | `@Data` 또는 `@Getter` + `@Setter` |
| 로깅 | `@Slf4j` |
| 빌더 패턴 | `@Builder` |

### ⚠️ 주의

| 상황 | 주의사항 |
|------|----------|
| Entity | `@Data` 사용 금지 (대신 `@Getter` 사용) |
| 불변 객체 | `@Setter` 사용 금지 |

---

## this 키워드와의 관계

### Lombok 없이
```java
public MemoController(MemoService memoService) {
    this.memoService = memoService;  // this 필수
}

public void setContent(String content) {
    this.content = content;  // this 필수
}
```

### Lombok 사용
```java
@RequiredArgsConstructor  // 생성자 자동
@Setter                   // Setter 자동
public class MemoController {
    private final MemoService memoService;

    // this를 쓸 일이 거의 없음!
}
```

**Lombok을 쓰면 `this` 키워드를 거의 안 쓰게 됩니다!**

---

## 다른 언어와의 비교

### Kotlin
```kotlin
data class Memo(
    val id: Long,
    val content: String
)
// 자동으로 Getter, toString, equals, hashCode 생성
```

### Python
```python
@dataclass
class Memo:
    id: int
    content: str
```

### Java + Lombok
```java
@Data
public class Memo {
    private Long id;
    private String content;
}
// 다른 언어처럼 간결!
```

---

## 정리

**Lombok = Java의 문법 설탕(Syntactic Sugar)**

- ❌ 새 기능 추가 (X)
- ✅ 반복 코드 자동화 (O)
- ✅ 문법 간소화 (O)
- ✅ 실무 필수 (O)

**실무 사용률: ~90%**

Lombok이 없는 Java는 상상하기 힘들 정도로 필수 도구가 되었습니다!
