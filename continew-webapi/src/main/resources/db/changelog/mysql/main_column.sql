-- liquibase formatted sql

-- changeset don:add-is-settled-to-edu-salary
-- comment: 添加 is_settled 字段到 edu_salary 表，并修改 status 字段注释
ALTER TABLE `edu_salary` ADD COLUMN `is_settled` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否结算（0：未结算；1：已结算）' AFTER `status`;
ALTER TABLE `edu_salary` MODIFY COLUMN `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（0：失效；1：生效）';

-- 将现有数据的 status 值迁移到 is_settled，并设置 status 为 1（生效）
UPDATE `edu_salary` SET `is_settled` = `status`, `status` = 1 WHERE 1=1;

-- changeset don:add-recv-name-to-edu-salary
-- comment: 添加 recv_name 字段到 edu_salary 表，设置为可空（如果字段不存在则添加，如果存在则修改为可空）
-- 注意：由于MySQL不支持IF NOT EXISTS，这里使用MODIFY，如果字段不存在需要先手动添加
-- 如果字段已存在但是NOT NULL，修改为可空；如果字段不存在，请先执行：ALTER TABLE `edu_salary` ADD COLUMN `recv_name` varchar(50) DEFAULT NULL COMMENT '收款人姓名' AFTER `teacher_name`;
ALTER TABLE `edu_salary` MODIFY COLUMN `recv_name` varchar(50) DEFAULT NULL COMMENT '收款人姓名';

