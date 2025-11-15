-- liquibase formatted sql

-- changeset don:remove-student-password-field
-- comment: 删除 edu_student 表中的 password 字段，因为密码管理已迁移到 edu_credential 表
ALTER TABLE `edu_student` DROP COLUMN `password`;

-- rollback: ALTER TABLE `edu_student` ADD COLUMN `password` varchar(100) NOT NULL COMMENT '密码' AFTER `avatar`;
