-- 移除教材编码的唯一索引约束
-- 执行日期: 2025-12-29
-- 说明: 允许教材编码重复，移除唯一性约束

-- 1. 删除原有的唯一索引
ALTER TABLE `edu_material` DROP INDEX `uk_code`;

-- 2. 添加普通索引以保持查询性能
ALTER TABLE `edu_material` ADD INDEX `idx_code` (`code`);
