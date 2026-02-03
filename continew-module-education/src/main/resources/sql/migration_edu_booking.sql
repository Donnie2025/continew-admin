-- 数据库迁移脚本：修改 edu_booking 表结构
-- 增加 lesson_id, teacher_id, teacher_name 字段
-- 删除 material_url, operator_name, operate_time 字段  
-- 修改字段名：phone → student_phone, start_date → slot_date, start_time → slot_time

-- 1. 增加新字段
ALTER TABLE `edu_booking` 
ADD COLUMN `lesson_id` bigint(20) DEFAULT NULL COMMENT '课节ID' AFTER `slot_id`,
ADD COLUMN `teacher_id` bigint(20) DEFAULT NULL COMMENT '教师ID' AFTER `student_phone`,
ADD COLUMN `teacher_name` varchar(50) DEFAULT NULL COMMENT '教师姓名' AFTER `teacher_id`;

-- 2. 修改字段名
ALTER TABLE `edu_booking` 
CHANGE COLUMN `phone` `student_phone` varchar(20) NOT NULL COMMENT '学生手机号',
CHANGE COLUMN `start_date` `slot_date` varchar(8) NOT NULL COMMENT '开课日期（格式：YYYYMMDD）',
CHANGE COLUMN `start_time` `slot_time` varchar(5) NOT NULL COMMENT '开课时间（格式：HH:MM）';

-- 3. 删除不需要的字段
ALTER TABLE `edu_booking` 
DROP COLUMN `material_url`,
DROP COLUMN `operator_name`,
DROP COLUMN `operate_time`;

-- 4. 添加外键约束
ALTER TABLE `edu_booking` 
ADD CONSTRAINT `fk_booking_lesson` FOREIGN KEY (`lesson_id`) REFERENCES `edu_material_lesson` (`id`),
ADD CONSTRAINT `fk_booking_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `edu_teacher` (`id`);

-- 5. 添加索引（可选，提升查询性能）
ALTER TABLE `edu_booking` 
ADD INDEX `idx_lesson_id` (`lesson_id`),
ADD INDEX `idx_teacher_id` (`teacher_id`),
ADD INDEX `idx_slot_date` (`slot_date`),
ADD INDEX `idx_slot_time` (`slot_time`);

-- 6. 更新现有数据的冗余字段（可选，如果有现有数据的话）
-- 注意：这个更新语句需要根据实际的关联表结构来调整
/*
UPDATE `edu_booking` b
LEFT JOIN `edu_teacher` t ON b.teacher_id = t.id
LEFT JOIN `edu_material_lesson` ml ON b.lesson_id = ml.id
SET
  b.teacher_name = t.name,
  b.lesson_name = ml.lesson_name
WHERE b.teacher_name IS NULL OR b.lesson_name IS NULL;
*/
