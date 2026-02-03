-- 教材管理表结构（主子表设计）

-- 1. 教材主表（存放教材基本信息）
CREATE TABLE `edu_material` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(50) NOT NULL COMMENT '教材编码',
  `name` varchar(100) NOT NULL COMMENT '教材名字',
  `level` varchar(50) NOT NULL COMMENT '级别（K1:幼儿园小班 K2:幼儿园中班 K3:幼儿园大班 G1-G12:1-12年级 ADULT:成人）',
  `category` varchar(50) NOT NULL COMMENT '分类（CHILDREN:少儿启蒙 TEENAGER:青少年 ADULT:成人教材 COMPREHENSIVE:综合教材 READING:阅读绘本 PHONICS:自然拼读 EXAM:考试教材 GRAMMAR:语法）',
  `cover_img` varchar(500) DEFAULT NULL COMMENT '封面图片',
  `desc` varchar(500) DEFAULT NULL COMMENT '教材描述',
  `is_show` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否前端展示（1:展示 0:不展示）',
  `sort` int NOT NULL DEFAULT 999 COMMENT '排序',
  `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1:启用 0:禁用）',
  `create_user` bigint(20) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_code` (`code`),
  KEY `idx_level` (`level`),
  KEY `idx_category` (`category`),
  KEY `idx_is_show` (`is_show`),
  KEY `idx_sort` (`sort`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教材主表';

-- 2. 教材课节表（存放课节信息）
CREATE TABLE `edu_material_lesson` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `material_id` bigint(20) NOT NULL COMMENT '教材ID',
  `material_name` varchar(150) DEFAULT NULL COMMENT '教材名称（冗余字段，格式：name + level）',
  `lesson_name` varchar(100) NOT NULL COMMENT '课节名字',
  `lesson_url` varchar(500) DEFAULT NULL COMMENT '课节链接',
  `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（1:启用 0:禁用）',
  `create_user` bigint(20) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_material_name` (`material_name`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_material_lesson_material` FOREIGN KEY (`material_id`) REFERENCES `edu_material` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教材课节表';


-- 插入示例数据

-- 教材主表数据
INSERT INTO `edu_material` (
  `code`, `name`, `level`, `category`, `cover_img`, `desc`, `is_show`, `sort`, `create_user`
) VALUES 
(
  'EFK_K1_001', 'English for Kids', 'K1', 'CHILDREN', 
  'https://example.com/images/english-kids-k1-cover.jpg',
  '专为幼儿园小班设计的英语启蒙教材',
  1, 1, 1
),
(
  'MB_K2_001', 'Math Basics', 'K2', 'CHILDREN', 
  'https://example.com/images/math-basics-k2-cover.jpg',
  '幼儿园中班数学基础练习册',
  1, 2, 1
),
(
  'PF_K3_001', 'Phonics Fun', 'K3', 'PHONICS', 
  'https://example.com/images/phonics-fun-k3-cover.jpg',
  '幼儿园大班自然拼读音频教材',
  1, 3, 1
),
(
  'ST_G1_001', 'Story Time', 'G1', 'READING', 
  'https://example.com/images/story-time-g1-cover.jpg',
  '一年级英语故事阅读绘本',
  1, 4, 1
),
(
  'TG_G5_001', 'Teen Grammar', 'G5', 'TEENAGER', 
  'https://example.com/images/teen-grammar-g5-cover.jpg',
  '五年级青少年语法教材',
  1, 5, 1
),
(
  'AE_ADULT_001', 'Adult English', 'ADULT', 'ADULT', 
  'https://example.com/images/adult-english-cover.jpg',
  '成人英语综合教材',
  1, 6, 1
),
(
  'CE_G8_001', 'Comprehensive English', 'G8', 'COMPREHENSIVE', 
  'https://example.com/images/comprehensive-english-g8-cover.jpg',
  '八年级综合英语教材',
  1, 7, 1
),
(
  'ET_G12_001', 'Exam Training', 'G12', 'EXAM', 
  'https://example.com/images/exam-training-g12-cover.jpg',
  '十二年级考试训练教材',
  1, 8, 1
);

-- 教材课节表数据
INSERT INTO `edu_material_lesson` (
  `material_id`, `material_name`, `lesson_name`, `lesson_url`, `create_user`
) VALUES 
-- English for Kids 的课节
(1, 'English for Kids K1', 'Unit 1 - Hello', 'https://example.com/lessons/k1-unit1.ppt', 1),
(1, 'English for Kids K1', 'Unit 2 - Colors', 'https://example.com/lessons/k1-unit2.ppt', 1),
(1, 'English for Kids K1', 'Unit 3 - Numbers', 'https://example.com/lessons/k1-unit3.ppt', 1),
(1, 'English for Kids K1', 'Review Test 1', 'https://example.com/lessons/k1-test1.ppt', 1),

-- Math Basics 的课节
(2, 'Math Basics K2', 'Lesson 1 - Numbers 1-10', 'https://example.com/lessons/k2-math1.ppt', 1),
(2, 'Math Basics K2', 'Lesson 2 - Shapes', 'https://example.com/lessons/k2-math2.ppt', 1),
(2, 'Math Basics K2', 'Practice Test', 'https://example.com/lessons/k2-test1.ppt', 1),

-- Phonics Fun 的课节
(3, 'Phonics Fun K3', 'Audio Lesson 1 - A-E', 'https://example.com/lessons/k3-phonics1.mp3', 1),
(3, 'Phonics Fun K3', 'Audio Lesson 2 - F-J', 'https://example.com/lessons/k3-phonics2.mp3', 1),
(3, 'Phonics Fun K3', 'Phonics Review', 'https://example.com/lessons/k3-review1.mp3', 1),

-- Story Time 的课节
(4, 'Story Time G1', 'Video Story 1 - The Cat', 'https://example.com/lessons/g1-story1.mp4', 1),
(4, 'Story Time G1', 'Video Story 2 - The Dog', 'https://example.com/lessons/g1-story2.mp4', 1);

