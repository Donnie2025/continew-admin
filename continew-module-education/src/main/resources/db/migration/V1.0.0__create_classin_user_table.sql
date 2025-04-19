CREATE TABLE IF NOT EXISTS `edu_classin_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `classin_user_id` VARCHAR(64) NOT NULL COMMENT 'ClassIn用户ID',
    `user_type` TINYINT NOT NULL DEFAULT 0 COMMENT '用户类型：0-普通用户，1-学生，2-教师',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `password` VARCHAR(64) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(64) NOT NULL COMMENT '昵称',
    `mobile` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(64) DEFAULT NULL COMMENT '邮箱',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_classin_user_id` (`classin_user_id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ClassIn用户表'; 