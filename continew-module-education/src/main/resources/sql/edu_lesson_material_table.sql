-- 课节与教材关联表
CREATE TABLE `edu_lesson_material` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `lesson_id` bigint(20) NOT NULL COMMENT '课节ID',
  `lesson_name` varchar(100) DEFAULT NULL COMMENT '课节名称（冗余字段）',
  `material_id` bigint(20) DEFAULT NULL COMMENT '教材ID（可选）',
  `material_name` varchar(100) DEFAULT NULL COMMENT '教材名称（冗余字段）',
  `material_lesson_id` bigint(20) DEFAULT NULL COMMENT '教材课节ID（可选，如果需要关联到具体教材课节）',
  `material_lesson_name` varchar(100) DEFAULT NULL COMMENT '教材课节名称（冗余字段）',
  `lesson_url` varchar(500) DEFAULT NULL COMMENT '课节链接（冗余自edu_material_lesson.lesson_url）',
  `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1:启用 0:禁用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_user` bigint(20) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lesson_material` (`lesson_id`, `material_id`, `material_lesson_id`) COMMENT '课节-教材-教材课节唯一索引',
  KEY `idx_lesson_id` (`lesson_id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_material_lesson_id` (`material_lesson_id`),
  KEY `idx_lesson_material` (`lesson_id`, `material_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_lesson_material_lesson` FOREIGN KEY (`lesson_id`) REFERENCES `edu_lesson` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_lesson_material_material` FOREIGN KEY (`material_id`) REFERENCES `edu_material` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_lesson_material_material_lesson` FOREIGN KEY (`material_lesson_id`) REFERENCES `edu_material_lesson` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课节教材关联表';

-- 创建索引以优化查询性能
-- 复合索引：按课节查询教材（最常用）
CREATE INDEX `idx_lesson_material_create` ON `edu_lesson_material` (`lesson_id`, `create_time`);
-- 复合索引：按教材查询课节
CREATE INDEX `idx_material_lesson_create` ON `edu_lesson_material` (`material_id`, `lesson_id`, `create_time`);

-- 示例数据（可选）
-- INSERT INTO `edu_lesson_material` (`lesson_id`, `lesson_name`, `material_id`, `material_name`, `material_lesson_id`, `material_lesson_name`, `create_user`) VALUES 
-- (1, '2024-11-20 英语课', 1, 'Oxford English K1', 1, 'Unit 1 - Hello', 1),
-- (1, '2024-11-20 英语课', 1, 'Oxford English K1', 2, 'Unit 2 - Numbers', 1),
-- (2, '2024-11-21 英语课', 2, 'Oxford English K2', NULL, NULL, 1);
