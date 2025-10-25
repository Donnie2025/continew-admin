-- liquibase formatted sql

-- changeset don:add-is-settled-to-edu-salary
-- comment: 添加 is_settled 字段到 edu_salary 表，并修改 status 字段注释
ALTER TABLE `edu_salary` ADD COLUMN `is_settled` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否结算（0：未结算；1：已结算）' AFTER `status`;
ALTER TABLE `edu_salary` MODIFY COLUMN `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（0：失效；1：生效）';

-- 将现有数据的 status 值迁移到 is_settled，并设置 status 为 1（生效）
UPDATE `edu_salary` SET `is_settled` = `status`, `status` = 1 WHERE 1=1;

