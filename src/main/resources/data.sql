-- ============================================================
--  Cathay Coindesk - 測試資料初始化
--  對應 coindesk API 目前有的幣別(USD/GBP/EUR)
--  也預先放 JPY/TWD 方便 CRUD 測試
-- ============================================================
INSERT INTO currency (code, chinese_name) VALUES ('USD', '美元');
INSERT INTO currency (code, chinese_name) VALUES ('GBP', '英鎊');
INSERT INTO currency (code, chinese_name) VALUES ('EUR', '歐元');
INSERT INTO currency (code, chinese_name) VALUES ('JPY', '日圓');
INSERT INTO currency (code, chinese_name) VALUES ('TWD', '新台幣');