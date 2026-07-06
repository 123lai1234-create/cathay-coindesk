# 國泰世華 JAVA 工程師線上作業 — Coindesk API 整合

使用 **Maven + Spring Boot 2.7.18 + JDK 8 + H2 + Spring Data JPA**,實作幣別 CRUD
與 coindesk API 呼叫 + 資料轉換。

---

## 專案需求對應

| 作業需求 | 實作位置 |
|---------|---------|
| Build Tool: Maven | `pom.xml` |
| JDK 8 | `pom.xml` `<java.version>1.8</java.version>` |
| Spring Boot | `pom.xml` `spring-boot-starter-parent 2.7.18` |
| H2 + ORM | `pom.xml` `h2 1.4.200` + `spring-boot-starter-data-jpa` |
| 測試資料初始化 SQL | `src/main/resources/schema.sql` + `data.sql` |
| 幣別 CRUD API | `CurrencyController` |
| 呼叫 coindesk API | `CoindeskService` + `CoindeskController.getRaw()` |
| 資料轉換 API | `CoindeskController.getTransformed()` + `CoindeskTransformService` |
| 4 項測試 | `src/test/java/.../`(見下方) |

---

## 專案結構

```
cathay-coindesk/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/com/cathay/coindesk/
│   │   │   ├── CathayCoindeskApplication.java
│   │   │   ├── config/RestTemplateConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── CurrencyController.java        # CRUD API
│   │   │   │   └── CoindeskController.java        # /raw 與 /transformed
│   │   │   ├── dto/
│   │   │   │   ├── CoindeskRawResponse.java
│   │   │   │   ├── CurrencyInfo.java
│   │   │   │   └── TransformedResponse.java
│   │   │   ├── entity/Currency.java
│   │   │   ├── repository/CurrencyRepository.java
│   │   │   ├── service/
│   │   │   │   ├── CurrencyService.java
│   │   │   │   ├── CoindeskService.java
│   │   │   │   └── CoindeskTransformService.java
│   │   │   └── exception/
│   │   │       ├── ResourceNotFoundException.java
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── schema.sql                          # currency 表 DDL
│   │       └── data.sql                            # 測試資料(USD/GBP/EUR/JPY/TWD)
│   └── test/
│       └── java/com/cathay/coindesk/
│           ├── service/CoindeskTransformServiceTest.java       # 單元測試(邏輯)
│           └── controller/
│               ├── CurrencyControllerTest.java                  # CRUD API
│               ├── CoindeskControllerRawTest.java               # coindesk 原始 API
│               └── CoindeskControllerTransformedTest.java       # 轉換 API
```

---

## API 列表

| Method | Path | 說明 |
|--------|------|------|
| GET | `/api/currencies` | 查詢全部幣別 |
| GET | `/api/currencies/{code}` | 查詢單筆幣別 |
| POST | `/api/currencies` | 新增幣別(`{"code":"CNY","chineseName":"人民幣"}`) |
| PUT | `/api/currencies/{code}` | 修改幣別中文名稱 |
| DELETE | `/api/currencies/{code}` | 刪除幣別 |
| GET | `/api/coindesk/raw` | 呼叫 coindesk API 原始內容 |
| GET | `/api/coindesk/transformed` | 呼叫並轉換後的內容 |

### 轉換後 API 格式範例

```json
{
  "updatedTime": "2024/09/02 07:07:20",
  "currencies": [
    { "code": "USD", "chineseName": "美元", "rate": 57756.2984 },
    { "code": "GBP", "chineseName": "英鎊", "rate": 43984.0203 },
    { "code": "EUR", "chineseName": "歐元", "rate": 52243.2865 }
  ]
}
```

---

## 測試對應

| 作業測試需求 | 測試檔 |
|-------------|--------|
| 1. 資料轉換相關邏輯單元測試 | `CoindeskTransformServiceTest`(7 個 case,涵蓋 parseTime 與 transform) |
| 2. CRUD API 呼叫並顯示內容 | `CurrencyControllerTest`(9 個 case) |
| 3. 呼叫 coindesk API 並顯示內容 | `CoindeskControllerRawTest`(印出 Response Body) |
| 4. 呼叫資料轉換 API 並顯示內容 | `CoindeskControllerTransformedTest`(印出 Response Body) |

每個測試都用 `MockMvc.andDo(print())` 印出 HTTP Request/Response 內容。

---

## 執行方式

### 編譯 + 跑測試

```bash
mvn clean test
```

### 啟動 Spring Boot 服務

```bash
mvn spring-boot:run
```

啟動後:

- 主站: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:cathaydb`
  - User: `sa` / Password: (空)

### 打 API 範例

```bash
# 查詢全部幣別
curl http://localhost:8080/api/currencies

# 呼叫 coindesk 並轉換
curl http://localhost:8080/api/coindesk/transformed

# 新增幣別
curl -X POST http://localhost:8080/api/currencies \
  -H "Content-Type: application/json" \
  -d '{"code":"CNY","chineseName":"人民幣"}'
```

---

## 環境

- JDK: Temurin 8 (1.8.0_492)
- Maven: 3.9.16
- Spring Boot: 2.7.18(最後一個支援 JDK 8 的版本)
- H2: 1.4.200(最後一個支援 JDK 8 的 H2 1.x 版;H2 2.x 需 JDK 11)
- Hibernate: 5.6.x(Spring Boot 2.7.18 預設)