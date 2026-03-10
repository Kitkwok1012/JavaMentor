# JavaMentor - Java面試互動學習平台

[![Java Version](https://img.shields.io/badge/Java-8%2B-blue)](https://www.oracle.com/java/technologies/downloads/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

275+高質量Java面試題目，支援多選、相關題目推薦、學習進度追蹤。

## 功能特性

- **275+題目** - 涵蓋OOP、Collection、Thread、JVM、Exception、Spring、SQL、Design Patterns
- **多選題支援** - 4選2、4選3等多選格式
- **相關題目推薦** - 基於tag同難度既智能推薦
- **學習進度追蹤** - 顯示已完成/錯題/正確率
- **進階困難題目** - 為4年經驗工程師準備既深度題目

## 技術棧

- Java 8+
- Spring Boot 3.2
- Spring Data JPA
- H2 Database (In-Memory)
- Thymeleaf + Tailwind CSS

## 快速開始

```bash
# Clone the project
git clone https://github.com/Kitkwok1012/JavaMentor.git
cd JavaMentor

# Run
mvn spring-boot:run

# 訪問 http://localhost:8080
```

## Branch Strategy

```
main (production)
  ↑
develop (integration)
  ↑
feature/* (new features)
  ↑
bugfix/* (bug fixes)
```

- **main** - 生產環境，只接受merge request
- **develop** - 開發整合分支
- **feature/** - 新功能分支
- **bugfix/** - Bug修復分支

## Commit Message Convention

```
feat: 新功能
fix: Bug修復
docs: 文檔更新
refactor: 重構
test: 測試
chore: 構建/工具
```

## Project Structure

```
src/main/java/com/javamentor/
├── JavaMentorApplication.java
├── config/
│   ├── DataInitializer.java    # 題目初始化
│   └── DataFixer.java          # 數據修復
├── controller/
│   └── LearnController.java    # Web控制器
├── dto/
│   ├── AnswerResponseDto.java
│   ├── QuestionDto.java
│   └── TopicProgressDto.java
├── entity/
│   ├── Question.java
│   ├── Topic.java
│   └── UserProgress.java
├── repository/
│   ├── QuestionRepository.java
│   ├── TopicRepository.java
│   └── UserProgressRepository.java
└── service/
    └── QuestionService.java    # 業務邏輯
```

## License

MIT
