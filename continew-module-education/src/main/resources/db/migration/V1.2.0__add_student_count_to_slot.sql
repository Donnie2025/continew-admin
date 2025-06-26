-- 为edu_slot表增加学生数量字段
ALTER TABLE `edu_slot` ADD COLUMN `student_count` int NOT NULL DEFAULT 1 COMMENT '学生数量' AFTER `duration`; 