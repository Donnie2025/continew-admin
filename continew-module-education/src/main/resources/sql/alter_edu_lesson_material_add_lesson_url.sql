-- 为edu_lesson_material表添加lesson_url字段
-- 执行时间：根据实际需要执行
-- 作用：在教材课节名称字段后添加课节链接字段

ALTER TABLE `edu_lesson_material` 
ADD COLUMN `lesson_url` varchar(500) DEFAULT NULL COMMENT '课节链接（冗余自edu_material_lesson.lesson_url）' AFTER `material_lesson_name`;
