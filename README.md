# JavaMentor - Java面試互動學習平台

<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-blue.svg" alt="Java">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2-blue.svg" alt="Spring Boot">
  <img src="https://img.shields.io/badge/License-MIT-green.svg" alt="License">
</p>

一個互動式 Java 面試題庫學習平台，支持題目練習、AI 追問、進度追蹤等功能。

## ✨ 功能特點

- **題目練習** - 596+ 高質量 Java 面試題目
- **AI 追問** - 根據答題情況智能推薦相關題目
- **進度追蹤** - 記錄學習進度與正確率
- **錯題複習** - 自動收集錯題供複習
- **模擬面試** - 計時練習模式
- **多 Topic 支持** - 涵蓋 OOP、Collection、Thread、JVM、Spring、MySQL、Redis 等

## 🛠️ 技術棧

| 技術 | 版本 |
|------|------|
| Java | 17+ |
| Spring Boot | 3.2 |
| H2 Database | - |
| Thymeleaf | - |
| Maven | - |
| Docker | - |

## 🚀 快速開始

### 方法一：本地運行 (Maven)

```bash
# Clone the project
git clone https://github.com/Kitkwok1012/JavaMentor.git
cd JavaMentor

# Build
mvn clean package

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
docker-compose up -d

# 查看日誌
docker-compose logs -f

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

## ⚙️ 環境變量

| 變量 | 默認值 | 說明 |
|------|--------|------|
| `PORT` | 8080 | 服務端口 |
| `DB_PATH` | file:./data/javamentor | H2 數據庫路徑 |
| `H2_CONSOLE_ENABLED` | false | 啟用 H2 Console |
| `SHOW_SQL` | false | 顯示 SQL 日誌 |
| `THYMELEAF_CACHE` | true | 啟用 Thymeleaf 緩存 |
| `SWAGGER_UI_ENABLED` | false | 啟用 Swagger UI |
| `LOG_LEVEL` | WARN | 日誌級別 |

### 開發環境示例

```bash
java -jar target/javamentor-1.0.0.jar \
  --H2_CONSOLE_ENABLED=true \
  --SHOW_SQL=true \
  --THYMELEAF_CACHE=false \
  --LOG_LEVEL=DEBUG
```

## 📁 項目結構

```
JavaMentor/
├── src/
│   ├── main/
│   │   ├── java/com/javamentor/
│   ├── config/       # 配置   │   │類
│   │   │   ├── controller/   # 控制器
│   │   │   ├── service/      # 業務邏輯
│   │   │   ├── repository/   # 數據訪問
│   │   │   ├── entity/       # 實體類
│   │   │   └── dto/          # 數據傳輸對象
│   │   └── resources/
│   │       ├── data/         # 題目數據
│   │       └── templates/    # Thymeleaf 模板
│   └── test/                 # 單元測試
├── docker-compose.yml        # Docker Compose 配置
├── Dockerfile                # Docker 鏡像構建
├── pom.xml                  # Maven 配置
└── README.md                # 項目說明
```

## 🔧 API 文檔

啟用 Swagger UI 後訪問：http://localhost:8080/swagger-ui.html

API 文檔：http://localhost:8080/v3/api-docs

## 🧪 運行測試

```bash
# 運行所有測試
mvn test

# 運行特定測試
mvn test -Dtest=QuestionServiceTest

# 生成測試報告
mvn test surefire-report:report
```

## 📊 CI/CD

項目包含 GitHub Actions 工作流：

- ✅ 自動 Build + Test
- ✅ Security Scan (Trivy)
- ✅ Code Quality Check (SpotBugs)

詳見 `.github/workflows/ci.yml`

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

## 🤝 貢獻指南

1. Fork 本項目
2. 創建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送分支 (`git push origin feature/AmazingFeature`)
5. 創建 Pull Request

## 📄 License

本項目基於 MIT License 開源 - 詳見 [LICENSE](LICENSE) 文件

## 🙏 致謝

- [JavaGuide](https://github.com/Snailclimb/JavaGuide) - Java 學習指南
- [toBeBetterJavaer](https://github.com/itwanger/toBeBetterJavaer) - 面渣逆襲系列
