-- 教材管理表结构（统一树形设计）
-- 节点类型（type）：
--   CATEGORY  分类（少儿启蒙/青少年/成人教材…）
--   BOOK      课本名字（English for Kids…）
--   LEVEL     课本级别（K1/K2/G1/G2…）
--   UNIT      单元（Unit 1/Unit 2…）
--   LESSON    教材课节（叶节点，对应具体课节文件）

-- 1. 教材统一树形表
DROP TABLE IF EXISTS `edu_material`;
CREATE TABLE `edu_material` (
  `id`          bigint        NOT NULL AUTO_INCREMENT   COMMENT '主键ID',
  `pid`         bigint        NOT NULL DEFAULT 0        COMMENT '父节点ID（0=根节点）',
  `type`        varchar(20)   NOT NULL                  COMMENT '节点类型（CATEGORY/BOOK/LEVEL/UNIT/LESSON）',
  `name`        varchar(200)  NOT NULL                  COMMENT '节点名称',
  `code`        varchar(50)       DEFAULT NULL          COMMENT '编码（BOOK/LEVEL层使用）',
  `cover_img`   varchar(500)      DEFAULT NULL          COMMENT '封面图片（BOOK层使用）',
  `description` varchar(1000)     DEFAULT NULL          COMMENT '描述',
  `lesson_url`  varchar(500)      DEFAULT NULL          COMMENT '课节资源链接（LESSON层使用）',
  `cloud_id`    varchar(100)      DEFAULT NULL          COMMENT 'ClassIn云盘ID（文件夹节点→文件夹ID，LESSON→文件ID）',
  `cloud_name`  varchar(200)      DEFAULT NULL          COMMENT 'ClassIn云盘名称',
  `is_show`     tinyint(1)    NOT NULL DEFAULT 1        COMMENT '是否前端展示（1:展示 0:不展示）',
  `sort`        int           NOT NULL DEFAULT 999      COMMENT '排序',
  `status`      tinyint(1)    NOT NULL DEFAULT 1        COMMENT '状态（1:启用 0:禁用）',
  `create_user` bigint        NOT NULL                  COMMENT '创建人',
  `create_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` bigint            DEFAULT NULL          COMMENT '修改人',
  `update_time` datetime          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_pid`         (`pid`),
  KEY `idx_type`        (`type`),
  KEY `idx_code`        (`code`),
  KEY `idx_cloud_id`    (`cloud_id`),
  KEY `idx_is_show`     (`is_show`),
  KEY `idx_sort`        (`sort`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教材统一树形表（CATEGORY/BOOK/LEVEL/UNIT/LESSON）';

-- edu_material_lesson 已废弃，课节数据合并至 edu_material（type=LESSON）


-- 示例数据（树形结构）
-- 层级：CATEGORY(1) → BOOK(2) → LEVEL(3) → UNIT(4) → LESSON(5)
INSERT INTO `edu_material` (`pid`, `type`, `name`, `code`, `cover_img`, `description`, `sort`, `create_user`) VALUES
-- CATEGORY 节点（pid=0）
(0, 'CATEGORY', '少儿启蒙', NULL, NULL, NULL, 1, 1),
(0, 'CATEGORY', '青少年',   NULL, NULL, NULL, 2, 1),

-- BOOK 节点（pid=CATEGORY.id，假设少儿启蒙id=1）
(1, 'BOOK', 'English for Kids', 'EFK', 'https://example.com/images/efk-cover.jpg', '专为幼儿园设计的英语启蒙教材', 1, 1),

-- LEVEL 节点（pid=BOOK.id，假设EFK id=3）
(3, 'LEVEL', 'K1', 'EFK_K1', NULL, '幼儿园小班', 1, 1),
(3, 'LEVEL', 'K2', 'EFK_K2', NULL, '幼儿园中班', 2, 1),

-- UNIT 节点（pid=LEVEL.id，假设K1 id=4）
(4, 'UNIT', 'Unit 1', NULL, NULL, NULL, 1, 1),
(4, 'UNIT', 'Unit 2', NULL, NULL, NULL, 2, 1),

-- LESSON 节点（pid=UNIT.id，假设Unit 1 id=6）
(6, 'LESSON', 'Lesson 1 - Hello', NULL, NULL, NULL, 1, 1),
(6, 'LESSON', 'Lesson 2 - Colors', NULL, NULL, NULL, 2, 1);

