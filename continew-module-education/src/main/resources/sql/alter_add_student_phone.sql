-- 为 edu_fixed_booking 表添加 student_phone 字段
-- 用于在固定课预约中存储学生手机号（冗余字段，便于查询显示）

ALTER TABLE `edu_fixed_booking` 
ADD COLUMN `student_phone` varchar(20) DEFAULT NULL COMMENT '学生手机号（冗余字段）' AFTER `student_name`;

-- 如果需要为已有数据填充手机号，可以执行以下更新语句（可选）
-- UPDATE `edu_fixed_booking` fb
-- INNER JOIN `edu_student` s ON fb.student_id = s.id
-- SET fb.student_phone = s.phone
-- WHERE fb.student_phone IS NULL;
