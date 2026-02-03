-- 删除 edu_card 表中的 init_times 字段
-- 统一使用 init_balance 管理所有卡类型的初始余额

-- 1. 备份现有数据（可选）
-- CREATE TABLE edu_card_backup AS SELECT * FROM edu_card;

-- 2. 数据迁移：将 init_times 的值合并到 init_balance
-- 对于次卡类型，如果 init_times 有值而 init_balance 为空，则将 init_times 转换为 init_balance
UPDATE `edu_card` 
SET `init_balance` = `init_times` 
WHERE (`type` = 'TL' OR `type` = 'TU') 
  AND `init_times` IS NOT NULL 
  AND (`init_balance` IS NULL OR `init_balance` = 0);

-- 3. 删除 init_times 字段
ALTER TABLE `edu_card` DROP COLUMN `init_times`;

-- 4. 更新字段注释
ALTER TABLE `edu_card` 
MODIFY COLUMN `init_balance` decimal(10,2) DEFAULT NULL COMMENT '初始余额（统一用于所有卡类型）';

-- 5. 验证表结构
-- DESCRIBE edu_card;

-- 注意事项：
-- 1. 执行前请确保已备份数据
-- 2. 数据迁移步骤会将次卡的 init_times 值转换为 init_balance
-- 3. 建议在测试环境先执行验证
-- 4. 执行后所有卡类型都使用 init_balance 字段管理初始余额
