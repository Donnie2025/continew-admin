-- ================================
-- 课时账户表改造迁移方案
-- 目标：支持学生、教师、后台管理员三种用户类型
-- 作者：系统生成
-- 日期：2026-08-05
-- ================================

-- ================================
-- 第一步：备份相关表
-- ================================

-- 备份 edu_account 表
CREATE TABLE `edu_account_backup_20260805` LIKE `edu_account`;
INSERT INTO `edu_account_backup_20260805` SELECT * FROM `edu_account`;

-- 备份 edu_transaction 表
CREATE TABLE `edu_transaction_backup_20260805` LIKE `edu_transaction`;
INSERT INTO `edu_transaction_backup_20260805` SELECT * FROM `edu_transaction`;

-- 备份 edu_order 表
CREATE TABLE `edu_order_backup_20260805` LIKE `edu_order`;
INSERT INTO `edu_order_backup_20260805` SELECT * FROM `edu_order`;


-- ================================
-- 第二步：修改 edu_account 表结构
-- ================================


-- 4. 删除旧的唯一索引
ALTER TABLE `edu_account` DROP INDEX `uk_student_account_type`;

-- 5. 添加新的唯一索引
ALTER TABLE `edu_account`
  ADD UNIQUE KEY `uk_user_account_type` (`user_type`, `user_id`, `account_type`),
  ADD INDEX `idx_user` (`user_type`, `user_id`);

-- 6. 删除旧字段
ALTER TABLE `edu_account`
  DROP COLUMN `student_id`,
  DROP COLUMN `student_name`;

-- 7. 修改表注释
ALTER TABLE `edu_account` COMMENT='课时账户表（支持学生/教师/管理员）';

-- ================================
-- 第三步：修改 edu_transaction 表结构
-- ================================

-- 1. 添加新字段
ALTER TABLE `edu_transaction`
  ADD COLUMN `user_type` varchar(20) NOT NULL DEFAULT 'student' COMMENT '用户类型（student-学生 teacher-教师 admin-管理员）' AFTER `account_id`,
  ADD COLUMN `user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '用户ID' AFTER `user_type`,
  ADD COLUMN `user_name` varchar(50) DEFAULT NULL COMMENT '用户姓名（快照）' AFTER `user_id`;

-- 2. 迁移现有数据
UPDATE `edu_transaction` SET
  `user_type` = 'student',
  `user_id` = `student_id`,
  `user_name` = `student_name`
WHERE `student_id` IS NOT NULL;

-- 3. 添加索引
ALTER TABLE `edu_transaction`
  ADD INDEX `idx_user` (`user_type`, `user_id`);

-- 4. 删除旧字段
ALTER TABLE `edu_transaction`
  DROP COLUMN `student_id`,
  DROP COLUMN `student_name`;

-- 5. 修改表注释
ALTER TABLE `edu_transaction` COMMENT='课时账户交易流水表（支持学生/教师/管理员）';

-- ================================
-- 第四步：修改 edu_order 表结构
-- ================================

-- 1. 添加新字段
ALTER TABLE `edu_order`
  ADD COLUMN `user_type` varchar(20) NOT NULL DEFAULT 'student' COMMENT '用户类型（student-学生 teacher-教师 admin-管理员）' AFTER `order_no`,
  ADD COLUMN `user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '用户ID' AFTER `user_type`,
  ADD COLUMN `user_name` varchar(50) DEFAULT NULL COMMENT '用户姓名（快照）' AFTER `user_id`;

-- 2. 迁移现有数据
UPDATE `edu_order` SET
  `user_type` = 'student',
  `user_id` = `student_id`,
  `user_name` = `student_name`
WHERE `student_id` IS NOT NULL;

-- 3. 删除旧的外键约束
ALTER TABLE `edu_order` DROP FOREIGN KEY `fk_order_student`;

-- 4. 删除旧字段
ALTER TABLE `edu_order`
  DROP COLUMN `student_id`,
  DROP COLUMN `student_name`;

-- 5. 添加索引
ALTER TABLE `edu_order`
  ADD INDEX `idx_user` (`user_type`, `user_id`);

-- 6. 修改表注释
ALTER TABLE `edu_order` COMMENT='课包订单表（支持学生/教师/管理员）';

-- ================================
-- 第五步：验证数据完整性
-- ================================

-- 验证迁移后所有表中 user_id 均有效（无 0 值代表迁移成功）
SELECT 'edu_account' as tbl, COUNT(*) as total, SUM(CASE WHEN user_id = 0 THEN 1 ELSE 0 END) as invalid FROM `edu_account`
UNION ALL
SELECT 'edu_transaction', COUNT(*), SUM(CASE WHEN user_id = 0 THEN 1 ELSE 0 END) FROM `edu_transaction`
UNION ALL
SELECT 'edu_order', COUNT(*), SUM(CASE WHEN user_id = 0 THEN 1 ELSE 0 END) FROM `edu_order`;

-- ================================
-- 第六步：回滚方案（如果迁移失败）
-- ================================

/*
-- 恢复 edu_account 表
DROP TABLE IF EXISTS `edu_account`;
RENAME TABLE `edu_account_backup_20260805` TO `edu_account`;

-- 恢复 edu_transaction 表
DROP TABLE IF EXISTS `edu_transaction`;
RENAME TABLE `edu_transaction_backup_20260805` TO `edu_transaction`;

-- 恢复 edu_order 表
DROP TABLE IF EXISTS `edu_order`;
RENAME TABLE `edu_order_backup_20260805` TO `edu_order`;
*/

-- ================================
-- 使用示例
-- ================================

-- 示例1：为学生创建账户（保持现有逻辑）
/*
INSERT INTO `edu_account`
  (`user_type`, `user_id`, `user_name`, `account_type`, `balance`, `expire_date`, `create_user`)
VALUES
  ('student', 1001, '张三', 'PAID', 10.00, '2027-12-31', 1);
*/

-- 示例2：为教师创建账户
/*
INSERT INTO `edu_account`
  (`user_type`, `user_id`, `user_name`, `account_type`, `balance`, `expire_date`, `create_user`)
VALUES
  ('teacher', 2001, '李老师', 'PAID', 50.00, NULL, 1);
*/

-- 示例3：为管理员创建账户
/*
INSERT INTO `edu_account`
  (`user_type`, `user_id`, `user_name`, `account_type`, `balance`, `expire_date`, `create_user`)
VALUES
  ('admin', 3001, '王管理员', 'GIFT', 100.00, NULL, 1);
*/

-- 示例4：查询特定用户的所有账户
/*
SELECT * FROM `edu_account`
WHERE `user_type` = 'student' AND `user_id` = 1001;
*/

-- 示例5：查询所有教师的账户统计
/*
SELECT
  user_name,
  SUM(balance) as total_balance,
  COUNT(*) as account_count
FROM `edu_account`
WHERE user_type = 'teacher'
GROUP BY user_id, user_name;
*/

-- ================================
-- 执行建议
-- ================================

/*
执行顺序：
1. 在测试环境完整执行第一步到第六步
2. 进行全面的功能测试
3. 确认无误后，在生产环境低峰期执行

注意事项：
1. 执行前务必做好数据库备份
2. 建议在低峰期或维护窗口执行
3. 执行过程中监控数据库性能
4. 准备好回滚方案
5. 提前通知相关开发和运维人员
6. 应用代码需要同步更新，支持新的字段结构
*/
