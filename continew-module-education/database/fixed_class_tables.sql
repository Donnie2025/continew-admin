-- 固定课功能相关表结构设计
-- 创建时间：2024-12-28
-- 说明：支持老师开设固定课，学生预约固定课的完整功能

-- ================================
-- 1. 固定课主表 (edu_fixed)
-- ================================
CREATE TABLE `edu_fixed` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `teacher_id` bigint NOT NULL COMMENT '教师ID，关联edu_teacher表',
    `teacher_name` varchar(50) NOT NULL COMMENT '教师姓名（冗余字段）',
    `duration_minutes` int NOT NULL DEFAULT '25' COMMENT '课程时长（分钟）',
    `max_students` int NOT NULL DEFAULT '1' COMMENT '最大学生数',
    
    -- 每周重复规则（简化）
    `week_day` tinyint NOT NULL COMMENT '星期几：1-周一，2-周二，3-周三，4-周四，5-周五，6-周六，7-周日',
    `start_time` varchar(5) NOT NULL COMMENT '开始时间（HH:MM格式）',
    
    -- 状态和管理字段
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `create_user` bigint DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_user` bigint DEFAULT NULL COMMENT '修改人', 
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    
    PRIMARY KEY (`id`),
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_status` (`status`),
    KEY `idx_week_time` (`week_day`, `start_time`),
    KEY `idx_teacher_name` (`teacher_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='固定课主表';

-- ================================
-- 2. 固定课预约表 (edu_fixed_booking)
-- ================================
CREATE TABLE `edu_fixed_booking` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `fixed_id` bigint NOT NULL COMMENT '固定课ID，关联edu_fixed表',
    `student_id` bigint NOT NULL COMMENT '学生ID，关联edu_student表',
    `student_name` varchar(50) NOT NULL COMMENT '学生姓名（冗余字段）',
    `teacher_id` bigint NOT NULL COMMENT '教师ID，关联edu_teacher表（冗余字段，便于查询）',
    `teacher_name` varchar(50) NOT NULL COMMENT '教师姓名（冗余字段）',
    
    -- 固定课时间信息（冗余字段，便于查询）
    `week_day` tinyint NOT NULL COMMENT '星期几：1-周一，2-周二，3-周三，4-周四，5-周五，6-周六，7-周日',
    `start_time` varchar(5) NOT NULL COMMENT '开始时间（HH:MM格式）',
    `duration_minutes` int NOT NULL DEFAULT '25' COMMENT '课程时长（分钟）',
    
    -- 状态和管理字段
    `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    
    -- 管理字段
    `create_user` bigint DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_user` bigint DEFAULT NULL COMMENT '修改人',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_fixed_student` (`fixed_id`, `student_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_status` (`status`),
    KEY `idx_student_name` (`student_name`),
    KEY `idx_teacher_name` (`teacher_name`),
    KEY `idx_week_time` (`week_day`, `start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='固定课预约表';

-- ================================  
-- 3. 固定课操作记录表 (edu_fixed_log)
-- ================================
CREATE TABLE `edu_fixed_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `fixed_id` bigint NOT NULL COMMENT '固定课ID',
    `student_id` bigint DEFAULT NULL COMMENT '学生ID',
    `student_name` varchar(50) DEFAULT NULL COMMENT '学生姓名（冗余字段）',
    `teacher_id` bigint NOT NULL COMMENT '教师ID',
    `teacher_name` varchar(50) NOT NULL COMMENT '教师姓名（冗余字段）',
    
    -- 操作信息（简化）
    `op_type` tinyint NOT NULL COMMENT '操作类型：1-预约，2-取消',
    `op_desc` varchar(200) NOT NULL COMMENT '操作描述',
    
    -- 时间信息
    `op_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `op_user` bigint DEFAULT NULL COMMENT '操作人ID',
    `op_user_name` varchar(50) DEFAULT NULL COMMENT '操作人姓名（冗余字段）',
    
    PRIMARY KEY (`id`),
    KEY `idx_fixed_id` (`fixed_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_op_type` (`op_type`),
    KEY `idx_op_time` (`op_time`),
    KEY `idx_student_name` (`student_name`),
    KEY `idx_teacher_name` (`teacher_name`),
    KEY `idx_op_user_name` (`op_user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='固定课操作记录表';
