-- 修复字段映射问题
-- 确保数据库字段名与实体类字段名保持一致

-- 1. 检查当前表结构
-- DESCRIBE edu_stu_card;

-- 2. 如果数据库字段还是 remain_balance，需要重命名为 balance
-- 检查字段是否存在
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'edu_stu_card' 
  AND COLUMN_NAME IN ('remain_balance', 'balance');

-- 3. 重命名字段（如果需要）
-- 如果字段名是 remain_balance，执行以下语句：
-- ALTER TABLE `edu_stu_card` 
-- CHANGE COLUMN `remain_balance` `balance` decimal(10,2) DEFAULT NULL COMMENT '余额（统一用于所有卡类型）';

-- 4. 检查 edu_card 表的字段
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'edu_card' 
  AND COLUMN_NAME IN ('init_times', 'init_balance');

-- 5. 删除 init_times 字段（如果存在）
-- ALTER TABLE `edu_card` DROP COLUMN IF EXISTS `init_times`;

-- 6. 验证最终表结构
-- DESCRIBE edu_stu_card;
-- DESCRIBE edu_card;

-- 注意事项：
-- 1. 执行前请确保已备份数据
-- 2. 先在测试环境验证
-- 3. 确保应用程序已更新实体类字段映射
