-- 删除 edu_stu_card 表中的 remain_times 字段
-- 统一使用 remain_balance 管理所有卡类型的余额

-- 1. 备份现有数据（可选）
-- CREATE TABLE edu_stu_card_backup AS SELECT * FROM edu_stu_card;

-- 2. 删除 remain_times 字段
ALTER TABLE `edu_stu_card` DROP COLUMN `remain_times`;

-- 3. 更新注释说明
ALTER TABLE `edu_stu_card` 
MODIFY COLUMN `remain_balance` decimal(10,2) DEFAULT NULL COMMENT '剩余余额（统一用于所有卡类型）';

-- 4. 验证表结构
-- DESCRIBE edu_stu_card;

-- 注意事项：
-- 1. 执行前请确保已备份数据
-- 2. 如果有现有的次卡数据，需要先将 remain_times 的值迁移到 remain_balance
-- 3. 建议在测试环境先执行验证
