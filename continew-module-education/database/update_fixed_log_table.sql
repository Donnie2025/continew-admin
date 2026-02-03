-- ========================================
-- 更新 edu_fixed_log 表结构
-- 添加 BaseDO 继承的审计字段
-- ========================================

-- 添加审计字段（如果字段已存在会报错，可忽略）
ALTER TABLE `edu_fixed_log` 
ADD COLUMN `create_user` bigint DEFAULT NULL COMMENT '创建人' AFTER `op_user_name`,
ADD COLUMN `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `create_user`,
ADD COLUMN `update_user` bigint DEFAULT NULL COMMENT '修改人' AFTER `create_time`,
ADD COLUMN `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间' AFTER `update_user`;

-- 添加索引（如果索引已存在会报错，可忽略）
CREATE INDEX `idx_create_user` ON `edu_fixed_log` (`create_user`);
CREATE INDEX `idx_update_user` ON `edu_fixed_log` (`update_user`);
CREATE INDEX `idx_create_time` ON `edu_fixed_log` (`create_time`);
CREATE INDEX `idx_update_time` ON `edu_fixed_log` (`update_time`);

-- 验证表结构
SHOW COLUMNS FROM `edu_fixed_log`;

-- 说明：
-- 1. create_user: 记录创建该操作记录的用户ID
-- 2. create_time: 记录创建时间，默认为当前时间
-- 3. update_user: 记录最后修改该记录的用户ID
-- 4. update_time: 记录最后修改时间，自动更新
-- 5. 这些字段对应 Java 中 FixedLogDO 继承 BaseDO 的字段
-- 6. 如果字段或索引已存在，执行时会报错但可以忽略，继续执行即可
