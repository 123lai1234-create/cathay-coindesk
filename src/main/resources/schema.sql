-- ============================================================
--  Cathay Coindesk - Currency 表(幣別 + 中文名稱對應)
--  H2 schema
-- ============================================================
DROP TABLE IF EXISTS currency;
CREATE TABLE currency (
    code         VARCHAR(10)  PRIMARY KEY,
    chinese_name VARCHAR(50)  NOT NULL
);