-- ============================================================
-- 删除 edu_transaction 表的 stu_card_id 字段
-- 该字段已废弃，现在通过 account_id 关联到 edu_account 表
-- 执行前请备份数据！
-- ============================================================

-- 删除 stu_card_id 字段
ALTER TABLE `edu_transaction` DROP COLUMN `stu_card_id`;

-- 验证修改结果
-- DESCRIBE edu_transaction;

