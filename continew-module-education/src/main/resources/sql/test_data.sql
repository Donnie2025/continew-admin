-- 测试数据插入脚本

-- 插入机构数据（如果不存在）
INSERT IGNORE INTO `edu_institution` (`id`, `name`, `status`, `create_time`, `create_user`) 
VALUES (1, '测试教育机构', 1, NOW(), 1);

-- 插入测试老师数据
INSERT IGNORE INTO `edu_teacher` (`id`, `name`, `phone`, `email`, `status`, `create_time`, `create_user`, `institution_id`) 
VALUES 
(1, '张老师', '13800138001', 'zhang@test.com', 1, NOW(), 1, 1),
(2, '李老师', '13800138002', 'li@test.com', 1, NOW(), 1, 1),
(3, '王老师', '13800138003', 'wang@test.com', 1, NOW(), 1, 1),
(4, '测试老师', '13811160169', 'test@test.com', 1, NOW(), 1, 1);

-- 插入测试凭证数据
INSERT IGNORE INTO `edu_credential` (`id`, `user_id`, `user_type`, `phone`, `password`, `credential_type`, `is_active`, `create_time`, `create_by`) 
VALUES 
(1, 1, 'teacher', '13800138001', '$2a$10$7JB720yubVSUvUHSIQashOhQkqZqF.QQSimple', 'phone', 1, NOW(), 'system'),
(2, 2, 'teacher', '13800138002', '$2a$10$7JB720yubVSUvUHSIQashOhQkqZqF.QQSimple', 'phone', 1, NOW(), 'system'),
(3, 3, 'teacher', '13800138003', '$2a$10$7JB720yubVSUvUHSIQashOhQkqZqF.QQSimple', 'phone', 1, NOW(), 'system'),
(4, 4, 'teacher', '13811160169', '$2a$10$7JB720yubVSUvUHSIQashOhQkqZqF.QQSimple', 'phone', 1, NOW(), 'system');

-- 插入测试班级数据
INSERT IGNORE INTO `edu_course` (`id`, `name`, `main_teacher_id`, `course_unique`, `status`, `institution_id`, `create_time`, `create_user`) 
VALUES 
(1, '初级英语班', 1, 'course_001', 1, 1, NOW(), 1),
(2, '中级数学班', 2, 'course_002', 1, 1, NOW(), 1),
(3, '高级物理班', 1, 'course_003', 1, 1, NOW(), 1),
(4, '计算机基础班', 3, 'course_004', 1, 1, NOW(), 1),
(5, '测试班级A', 4, 'course_005', 1, 1, NOW(), 1),
(6, '测试班级B', 4, 'course_006', 1, 1, NOW(), 1);

-- 插入测试学生数据
INSERT IGNORE INTO `edu_student` (`id`, `name`, `phone`, `email`, `status`, `create_time`, `create_user`, `institution_id`) 
VALUES 
(1, '小明', '13900139001', 'xiaoming@test.com', 1, NOW(), 1, 1),
(2, '小红', '13900139002', 'xiaohong@test.com', 1, NOW(), 1, 1),
(3, '小刚', '13900139003', 'xiaogang@test.com', 1, NOW(), 1, 1);

-- 插入班级老师关联数据
INSERT IGNORE INTO `edu_course_teacher` (`id`, `course_id`, `course_name`, `teacher_id`, `teacher_name`, `status`, `create_time`, `create_user`) 
VALUES 
(1, 1, '初级英语班', 2, '李老师', 1, NOW(), 1),
(2, 2, '中级数学班', 1, '张老师', 1, NOW(), 1),
(3, 3, '高级物理班', 2, '李老师', 1, NOW(), 1);

-- 插入班级学生关联数据
INSERT IGNORE INTO `edu_course_student` (`id`, `course_id`, `course_name`, `student_id`, `student_name`, `status`, `create_time`, `create_user`) 
VALUES 
(1, 1, '初级英语班', 1, '小明', 1, NOW(), 1),
(2, 1, '初级英语班', 2, '小红', 1, NOW(), 1),
(3, 2, '中级数学班', 2, '小红', 1, NOW(), 1),
(4, 2, '中级数学班', 3, '小刚', 1, NOW(), 1),
(5, 3, '高级物理班', 1, '小明', 1, NOW(), 1);

-- 插入测试课节数据
INSERT IGNORE INTO `edu_lesson` (`id`, `course_id`, `course_uid`, `name`, `teacher_id`, `teacher_uid`, `teacher_name`, `start_time`, `duration`, `record_state`, `status`, `create_time`, `create_user`) 
VALUES 
(1, 1, 1001, '英语基础语法', 1, 1001, '张老师', '2025-11-17 09:00:00', 60, 1, 1, NOW(), 1),
(2, 1, 1001, '英语词汇练习', 1, 1001, '张老师', '2025-11-17 14:00:00', 45, 1, 1, NOW(), 1),
(3, 2, 1002, '数学基础运算', 2, 1002, '李老师', '2025-11-18 10:00:00', 90, 1, 1, NOW(), 1),
(4, 3, 1003, '物理力学基础', 1, 1001, '张老师', '2025-11-19 15:00:00', 120, 0, 1, NOW(), 1);
