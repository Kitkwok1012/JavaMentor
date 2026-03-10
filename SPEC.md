# Java 互動學習平台 - 規格說明書

## 1. 項目概述

**項目名稱**: JavaMentor
**項目類型**: Web應用 (Spring Boot + Thymeleaf)
**核心功能**: 蘇格拉底式Java面試題學習平台，一題接一題AI追問
**目標用戶**: Java開發者、面試者

---

## 2. 功能範圍

### 2.1 題目系統

| 功能 | 描述 |
|------|------|
| Topic管理 | OOP、Collection、Thread、Exception、JVM、Spring、SQL、Design Pattern |
| 題目庫 | 每個Topic 10-20題高質素題目 |
| 題目類型 | 概念題、輸出題、場景題、優化題 |
| 答案儲存 | 正確答案 + 詳細解釋 + Follow-up問題庫 |

### 2.2 學習流程

```
選擇Topic → 開始學習 → 回答問題 → AI追問(視乎回答) → 下一題 → ... → 完成
```

- **一題接一題**: 答完先顯示下一題
- **即時反饋**: 提交即顯示對錯 + 解釋
- **AI追問**: 根據答案生成1-2條follow-up問題

### 2.3 追問邏輯

| 回答情況 | 追問內容 |
|----------|----------|
| 答對 | 「點解咁諗？」、「有咩優缺點？」、「同XXX有咩分別？」 |
| 答錯 | 正確答案解釋 + 「應該點諗？」 + 「哩個概念同邊個有關？」 |
| 部分啱 | 指出邊部分啱邊部分錯 + 解釋 |

### 2.4 進度追蹤

- **學習紀錄**: 邊題啱咗、邊題錯咗
- **錯題簿**: 收藏錯咗既題目
- **Topic進度**: 每個Topic完成度、準確率

### 2.5 數據存儲

- **H2 Database** (開發環境) / **MySQL** (生產環境)
- **Entity**: User, Question, Topic, UserProgress, WrongQuestion

---

## 3. 技術架構

### 3.1 Tech Stack

| 層 | 技術 |
|----|------|
| Backend | Java 17 + Spring Boot 3.x |
| Frontend | Thymeleaf + Tailwind CSS |
| DB | H2 (default) / MySQL |
| AI | MiniMax API (follow-up生成) |

### 3.2 目錄結構

```
src/
├── main/
│   ├── java/com/javamentor/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   └── config/
│   └── resources/
│       ├── templates/
│       ├── static/
│       └── data/questions.json
```

---

## 4. API設計

### 4.1 REST Endpoints

| Method | Path | 描述 |
|--------|------|------|
| GET | / | 首頁 |
| GET | /learn/{topic} | 開始某Topic學習 |
| GET | /question/{id} | 獲取題目 |
| POST | /answer | 提交答案 |
| POST | /answer/followup | 回覆AI追問 |
| GET | /progress | 學習進度 |
| GET | /wrong | 錯題簿 |

### 4.2 Request/Response Examples

**POST /answer**
```json
Request:
{
  "questionId": 1,
  "answer": "B",
  "topicId": "oop"
}

Response:
{
  "correct": true,
  "explanation": "...",
  "followUp": [
    {"question": "點解要咁做?", "options": ["...", "..."]}
  ]
}
```

---

## 5. 題目數據格式

```json
{
  "topics": [
    {
      "id": "oop",
      "name": "OOP",
      "questions": [
        {
          "id": 1,
          "type": "choice",
          "question": "以下邊個係SOLID既S原則？",
          "options": [
            {"key": "A", "text": "Single Responsibility Principle"},
            {"key": "B", "text": "Open/Closed Principle"},
            {"key": "C", "text": "Liskov Substitution Principle"},
            {"key": "D", "text": "Interface Segregation Principle"}
          ],
          "correctAnswer": "A",
          "explanation": "Single Responsibility Principle..."

        }
      ]
    }
  ]
}
```

---

## 6. 優先級

### Phase 1 (MVP)
- [x] 題目顯示（一題接一題）
- [x] 答案提交 + 即時對錯
- [x] 解釋顯示
- [x] AI追問功能
- [x] 錯題簿

### Phase 2
- [ ] Topic進度追蹤
- [ ] 多種題型（輸出題、場景題）
- [ ] 學習日誌

---

## 7. 配置

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:h2:mem:javamentor
  ai:
    minimax:
      api-key: ${MINIMAX_API_KEY}
      base-url: https://api.minimax.chat/v1
      model: MiniMax-M2.5
```

---

## 8. 驗收標準

1. 用戶可以選擇Topic開始學習
2. 題目一題接一題顯示，唔會一次過曬
3. 提交答案後即時顯示對錯 + 解釋
4. AI根據回答生成相關follow-up問題
5. 錯題會記錄低，可以翻查
6. 頁面responsive，手機都用得
