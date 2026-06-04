-- ============================================================
-- edu_stu_card 重构为 edu_account（学生课时账户）
-- 执行前请备份数据！
-- 建议先执行 refactor_edu_transaction.sql
-- ============================================================

-- Step 1：创建新表 edu_account
CREATE TABLE `edu_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  `student_name` varchar(50) DEFAULT NULL COMMENT '学生姓名（快照）',
  `account_type` varchar(10) NOT NULL DEFAULT 'PAID'
    COMMENT '账户类型（PAID:正常购买 GIFT:赠送课时 LEAVE:请假课时 FREEZE:冻结账户）',
  `balance` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '当前课时余额',
  `expire_date` date DEFAULT NULL COMMENT '到期日期（null 表示无限期）',
  `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1：正常；0：禁用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20) NOT NULL COMMENT '创建人',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_account_type` (`student_id`, `account_type`),
  INDEX `idx_expire_date` (`expire_date`),
  CONSTRAINT `fk_account_student` FOREIGN KEY (`student_id`) REFERENCES `edu_student` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='学生课时账户表';

-- Step 2：数据迁移 — 从 edu_stu_card 合并数据到 edu_account
-- 每个学生取最新的一张有效卡，余额取 balance，有效期取最晚的 expire_date
-- 所有存量卡统一迁移为 PAID 类型账户
INSERT INTO `edu_account` (
  `student_id`, `student_name`, `account_type`, `balance`, `expire_date`, `status`,
  `create_time`, `update_time`, `create_user`, `update_user`
)
SELECT
  sc.stu_id,
  sc.stu_name,
  'PAID',
  COALESCE(SUM(sc.balance), 0),
  MAX(sc.expire_date),
  1,
  MIN(sc.create_time),
  NOW(),
  MIN(sc.create_user),
  NULL
FROM `edu_stu_card` sc
WHERE sc.status = 1
GROUP BY sc.stu_id, sc.stu_name;

-- Step 3：更新 edu_transaction 关联字段
-- 将 stu_card_id 映射到对应的 account_id，stu_id/stu_name 改为 student_id/student_name

-- 3a: 添加新列
ALTER TABLE `edu_transaction`
  ADD COLUMN `account_id`    bigint(20) DEFAULT NULL COMMENT '学生课时账户ID' AFTER `id`,
  ADD COLUMN `student_id`    bigint(20) DEFAULT NULL COMMENT '学生ID' AFTER `account_id`,
  ADD COLUMN `student_name`  varchar(50) DEFAULT NULL COMMENT '学生姓名（快照）' AFTER `student_id`;

-- 3b: 从 edu_account 反查 account_id
UPDATE `edu_transaction` t
  JOIN `edu_account` a ON a.student_id = t.stu_id
  SET t.account_id = a.id, t.student_id = t.stu_id, t.student_name = t.stu_name;

-- 3c: 删除旧列
ALTER TABLE `edu_transaction`
  DROP COLUMN `stu_card_id`,
  DROP COLUMN `stu_id`,
  DROP COLUMN `stu_name`;

-- 3d: 设置 account_id NOT NULL 和索引
ALTER TABLE `edu_transaction`
  MODIFY COLUMN `account_id` bigint(20) NOT NULL COMMENT '学生课时账户ID',
  MODIFY COLUMN `student_id` bigint(20) NOT NULL COMMENT '学生ID',
  ADD INDEX `idx_account_id` (`account_id`),
  ADD INDEX `idx_student_id` (`student_id`),
  ADD CONSTRAINT `fk_transaction_account` FOREIGN KEY (`account_id`) REFERENCES `edu_account` (`id`);

-- Step 4：更新 edu_booking — 删除 stu_card_id/card_name，改用 account_id

-- 4a: 删除旧外键和字段
ALTER TABLE `edu_booking`
  DROP FOREIGN KEY `edu_booking_edu_stu_card_FK`,
  DROP COLUMN `stu_card_id`,
  DROP COLUMN `card_name`;

-- 4b: 添加 account_id 列和外键
ALTER TABLE `edu_booking`
  ADD COLUMN `account_id` bigint(20) DEFAULT NULL COMMENT '扣费课时账户ID' AFTER `teacher_name`,
  ADD CONSTRAINT `fk_booking_account` FOREIGN KEY (`account_id`) REFERENCES `edu_account` (`id`);

-- Step 5：（可选，确认数据无误后执行）删除旧表
-- edu_transaction 和 edu_booking 的外键已在上面步骤中处理
-- 5a: 删除 edu_order 对 edu_stu_card 的外键（如果存在）
-- ALTER TABLE `edu_order` DROP FOREIGN KEY `fk_order_stu_card`;

-- 5b: 删除 edu_transaction 旧外键（若 Step 3c 已执行可跳过）
-- ALTER TABLE `edu_transaction` DROP FOREIGN KEY `fk_transaction_stu_card`;

-- 5c: 删除旧表
DROP TABLE `edu_stu_card`;

-- Step 5：验证
-- SELECT COUNT(*) FROM edu_account;
-- SELECT id, student_id, balance, expire_date FROM edu_account LIMIT 10;
-- SELECT id, account_id, student_id, trans_type, direction, amount, balance FROM edu_transaction LIMIT 10;
