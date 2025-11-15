-- 用户密码错误记录表（通用表，支持学生和教师）
CREATE TABLE `user_password_error_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `phone` varchar(20) NOT NULL COMMENT '手机号码',
  `user_type` varchar(20) NOT NULL COMMENT '用户类型(student-学生, teacher-教师)',
  `error_count` int NOT NULL DEFAULT '0' COMMENT '错误次数',
  `last_error_time` datetime NOT NULL COMMENT '最后错误时间',
  `freeze_until` datetime DEFAULT NULL COMMENT '冻结到期时间',
  `is_frozen` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否冻结(0-否 1-是)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone_user_type` (`phone`, `user_type`),
  KEY `idx_phone` (`phone`),
  KEY `idx_user_type` (`user_type`),
  KEY `idx_freeze_until` (`freeze_until`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户密码错误记录表';

-- 注意：不使用定时清理脚本，改为在业务逻辑中判断
-- 验证逻辑：
-- 1. 最后一次错误时间如果在1个小时以内，且错误次数大于等于5，直接阻断
-- 2. 最后一次错误时间超过1个小时，再次验证密码，如果密码错误，不重置错误次数，只更新错误时间
-- 3. 如果登录成功，清空错误次数为0
