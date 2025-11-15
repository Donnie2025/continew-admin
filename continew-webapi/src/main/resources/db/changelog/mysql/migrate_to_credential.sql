-- liquibase formatted sql

-- changeset don:migrate-student-passwords-to-credential
-- comment: 将学生密码数据迁移到新的 edu_credential 表
-- 迁移现有学生的密码数据到 edu_credential 表
INSERT INTO `edu_credential` (
    `user_id`, 
    `user_type`, 
    `phone`, 
    `password`, 
    `credential_type`, 
    `is_active`, 
    `error_count`, 
    `is_frozen`, 
    `create_time`, 
    `update_time`
)
SELECT 
    s.`id` as `user_id`,
    'student' as `user_type`,
    s.`phone`,
    s.`password`,
    'phone' as `credential_type`,
    1 as `is_active`,
    0 as `error_count`,
    0 as `is_frozen`,
    s.`create_time`,
    NOW() as `update_time`
FROM `edu_student` s 
WHERE s.`phone` IS NOT NULL 
  AND s.`password` IS NOT NULL 
  AND s.`password` != ''
  AND NOT EXISTS (
      SELECT 1 FROM `edu_credential` c 
      WHERE c.`user_id` = s.`id` 
        AND c.`user_type` = 'student' 
        AND c.`credential_type` = 'phone'
  );

-- changeset don:migrate-password-error-logs-to-credential
-- comment: 将密码错误记录迁移到 edu_credential 表
-- 更新 edu_credential 表中的错误记录信息（如果存在旧的错误记录表）
UPDATE `edu_credential` c
INNER JOIN `user_password_error_log` e ON c.`phone` = e.`phone` AND c.`user_type` = e.`user_type`
SET 
    c.`error_count` = e.`error_count`,
    c.`last_error_time` = e.`last_error_time`,
    c.`freeze_until` = e.`freeze_until`,
    c.`is_frozen` = e.`is_frozen`,
    c.`update_time` = NOW()
WHERE c.`user_type` = 'student';

-- changeset don:remove-password-from-student-table
-- comment: 从 edu_student 表中移除 password 字段（可选，根据需要执行）
-- 注意：这个操作会删除 edu_student 表中的 password 字段，请确认数据已完全迁移后再执行
-- ALTER TABLE `edu_student` DROP COLUMN `password`;
