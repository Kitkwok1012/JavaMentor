# JavaMentor - Java面試互動學習平台

<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-blue.svg" alt="Java">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2-blue.svg" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Docker-Ready-blue.svg" alt="Docker">
  <img src="https://img.shields.io/badge/License-MIT-green.svg" alt="License">
  <img src="https://img.shields.io/badge/Tests-28%20Passing-green.svg" alt="Tests">
</p>

一個互動式 Java 面試題庫學習平台，支持題目練習、智能推薦、進度追蹤等功能。

## ✨ 功能特點

- **題目練習** - 596+ 高質量 Java 面試題目
- **智能推薦** - 根據答題情況智能推薦相關題目 (7-Rule Algorithm)
- **進度追蹤** - 記錄學習進度與正確率
- **錯題複習** - 自動收集錯題供複習
- **模擬面試** - 計時練習模式 (30/60/90 題)
- **題目搜尋** - 關鍵字搜尋題目
- **Structured Logging** - JSON 格式日誌，便於監控分析

## 🏗️ 系統架構

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client (Browser)                        │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Spring Boot Application                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │   Controller  │  │    Service   │  │   Repository │         │
│  │  (REST/UI)    │  │  (Business)  │  │   (JPA)     │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
│                                │                                │
│         ┌──────────────────────┼──────────────────────┐        │
│         ▼                      ▼                      ▼        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │   Caffeine    │  │      H2      │  │  Structured  │         │
│  │    Cache      │  │  Database    │  │   Logging   │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
└─────────────────────────────────────────────────────────────────┘
```

### 數據流向

```
User Request → Controller → Service → Repository → H2 Database
                  │            │
                  │            ▼
                  │      [Cache Hit?] → Yes → Return Cached Data
                  │                           No → Query DB → Cache Result
                  │
                  ▼
            [Log to Console/File in JSON Format]
```

### 核心模組

| Module | Responsibility |
|--------|----------------|
| `question/` | 題目管理、搜尋 |
| `progress/` | 用戶學習進度追蹤 |
| `session/` | 用戶會話管理 |
| `mocktest/` | 模擬考試邏輯 |
| `service/` | 業務邏輯 (QuestionService, FollowUpRecommender) |
| `controller/` | REST API 與 Web Controller |
| `config/` | 配置類 (Cache, Logging, Session) |
| `exception/` | 全域異常處理 |

## 🛠️ 技術棧

| Category | Technology |
|----------|------------|
| **Language** | Java 17+ |
| **Framework** | Spring Boot 3.2 |
| **Template** | Thymeleaf |
| **Database** | H2 (file-based) |
| **Cache** | Caffeine (in-memory) |
| **Logging** | Logstash JSON Encoder |
| **API Doc** | SpringDoc OpenAPI |
| **Build** | Maven |
| **Container** | Docker |

## 🚀 快速開始

### 方法一：本地運行 (Maven)

```bash
# Clone the project
git clone https://github.com/Kitkwok1012/JavaMentor.git
cd JavaMentor

# Build
mvn clean package -DskipTests

# Run
java -jar target/javamentor-1.0.0.jar
```

訪問 http://localhost:8080

---

### 方法二：Docker Compose (推薦)

```bash
# Clone the project
git clone https://github.com/Kitkwok1012/JavaMentor.git
cd JavaMentor

# 啟動服務
docker-compose up -d --build

# 查看日誌
docker-compose logs -f javamentor

# 停止服務
docker-compose down
```

訪問 http://localhost:8080

---

### 方法三：Docker

```bash
# Build image
docker build -t javamentor .

# Run container
docker run -p 8080:8080 javamentor
```

---

## 📡 API Endpoints

### 學習相關

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/topics` | 獲取所有 Topics |
| GET | `/learn/{topicId}` | 開始學習某 Topic |
| GET | `/api/question/next` | 獲取下一題 |
| POST | `/api/answer` | 提交答案 |

### 模擬考試

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/mock/start` | 開始模擬考試 |
| GET | `/api/mock/{sessionId}/question` | 獲取當前題目 |
| POST | `/api/mock/{sessionId}/answer` | 提交答案 |
| GET | `/api/mock/{sessionId}/result` | 獲取考試結果 |

### 搜尋

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/search?keyword=xxx` | 關鍵字搜尋題目 |

### 進度

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/progress` | 獲取學習進度 |
| GET | `/api/stats` | 獲取統計數據 |

### Swagger API 文檔

啟用後訪問：http://localhost:8080/swagger-ui.html

---

## ⚙️ 環境變量

| 變量 | 默認值 | 說明 |
|------|--------|------|
| `PORT` | 8080 | 服務端口 |
| `DB_PATH` | file:./data/javamentor | H2 數據庫路徑 |
| `H2_CONSOLE_ENABLED` | false | 啟用 H2 Console |
| `SHOW_SQL` | false | 顯示 SQL 日誌 |
| `THYMELEAF_CACHE` | true | 啟用 Thymeleaf 緩存 |
| `LOG_LEVEL` | INFO | 日誌級別 (DEBUG/INFO/WARN/ERROR) |

---

## 🧪 運行測試

```bash
# 運行所有測試
mvn test

# 運行特定測試
mvn test -Dtest=QuestionServiceTest

# 生成測試報告
mvn test surefire-report:report
```

**測試覆蓋：**
- QuestionService (4 tests)
- FollowUpRecommender (3 tests)
- InputSanitizer (16 tests)
- Logging Configuration (5 tests)

---

## 📁 項目結構

```
JavaMentor/
├── src/
│   ├── main/
│   │   ├── java/com/javamentor/
│   │   │   ├── question/           # 題目模組
│   │   │   │   ├── entity/         # Question, Topic
│   │   │   │   ├── repository/     # QuestionRepository
│   │   │   │   └── dto/            # QuestionDto, SearchResultDto
│   │   │   ├── progress/           # 進度模組
│   │   │   │   ├── entity/         # UserProgress
│   │   │   │   ├── repository/
│   │   │   │   └── dto/            # TopicProgressDto
│   │   │   ├── session/            # 會話模組
│   │   │   │   ├── entity/         # UserSession
│   │   │   │   └── repository/
│   │   │   ├── mocktest/           # 模擬考試模組
│   │   │   │   ├── service/
│   │   │   │   └── dto/            # MockTestDto, AnswerRequest
│   │   │   ├── controller/         # REST Controllers
│   │   │   ├── service/            # Business Logic
│   │   │   ├── config/            # Config Classes
│   │   │   ├── exception/          # Global Exception Handler
│   │   │   └── validator/          # Input Validators
│   │   └── resources/
│   │       ├── data/               # Question JSON data
│   │       ├── templates/          # Thymeleaf templates
│   │       ├── application.yml     # Application config
│   │       └── logback-spring.xml  # Logging config
│   └── test/                      # Unit tests
├── docker-compose.yml              # Docker Compose config
├── Dockerfile                      # Docker image build
├── pom.xml                        # Maven config
└── README.md                      # This file
```

---

## 🔧 開發指南

### 添加新題目

1. 編輯 `src/main/resources/data/questions.json`
2. 遵循現有格式：

```json
{
  "topicId": "new-topic",
  "name": "New Topic",
  "description": "Topic description",
  "questions": [
    {
      "question": "Your question here?",
      "optionA": "Option A",
      "optionB": "Option B",
      "optionC": "Option C",
      "optionD": "Option D",
      "correct": "A",
      "explanation": "Explanation here...",
      "difficulty": 1,
      "tags": "tag1,tag2"
    }
  ]
}
```

### 添加新 API

1. 在對應模組的 `controller/` 創建新的 Controller
2. 在 `service/` 添加業務邏輯
3. 在 `dto/` 添加 Request/Response DTO
4. 添加單元測試

---

## 📊 CI/CD

項目包含 GitHub Actions 工作流：

| Workflow | Description |
|----------|-------------|
| Build + Test | 自動編譯與測試 |
| Security Scan | Trivy 安全掃描 |
| Code Quality | SpotBugs 靜態分析 |

詳見 `.github/workflows/`

---

## 📝 題目 Topics

| Category | Topics |
|----------|--------|
| **Core** | OOP, Java Collections, Multi-threading, JVM |
| **Framework** | Spring, Spring Boot, Spring Cloud |
| **Database** | MySQL, MySQL Advanced, Redis |
| **Architecture** | Microservices, Distributed System |
| **DevOps** | Docker, Kubernetes, CI/CD |
| **System** | Network, OS, Linux |

共 55+ Topics，596+ 題目

---

## 🤝 貢獻指南

1. Fork 本項目
2. 創建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送分支 (`git push origin feature/AmazingFeature`)
5. 創建 Pull Request

---

## 📄 License

本項目基於 MIT License 開源 - 詳見 [LICENSE](LICENSE) 文件

---

## 🙏 致謝

- [JavaGuide](https://github.com/Snailclimb/JavaGuide) - Java 學習指南
- [toBeBetterJavaer](https://github.com/itwanger/toBeBetterJavaer) - 面渣逆襲系列
